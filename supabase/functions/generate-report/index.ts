import { serve } from "https://deno.land/std@0.168.0/http/server.ts"
import { createClient } from "https://esm.sh/@supabase/supabase-js@2"

const corsHeaders = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Headers': 'authorization, x-client-info, apikey, content-type',
}

serve(async (req) => {
  if (req.method === 'OPTIONS') {
    return new Response('ok', { headers: corsHeaders })
  }

  try {
    const { task_id } = await req.json()

    if (!task_id) {
      throw new Error('task_id is required')
    }

    const supabaseUrl = Deno.env.get('SUPABASE_URL') ?? ''
    const supabaseServiceKey = Deno.env.get('SUPABASE_SERVICE_ROLE_KEY') ?? ''
    const openaiApiKey = Deno.env.get('OPENAI_API_KEY') ?? ''

    const supabase = createClient(supabaseUrl, supabaseServiceKey)

    // 1. Fetch all data for the task
    const [
      { data: sessions },
      { data: transcripts },
      { data: logs }
    ] = await Promise.all([
      supabase.from('recording_sessions').select('*').eq('task_id', task_id).order('started_at', { ascending: true }),
      supabase.from('transcripts').select('*, recording_sessions!inner(task_id)').eq('recording_sessions.task_id', task_id),
      supabase.from('activity_logs').select('*').eq('task_id', task_id).order('created_at', { ascending: true })
    ])

    if (!sessions || sessions.length === 0) {
      throw new Error('No recording sessions found for this task')
    }

    // 2. Reconstruct chronological context
    let fullContext = ""
    sessions.forEach((session, index) => {
      const transcript = transcripts?.find(t => t.recording_session_id === session.id)

      fullContext += `--- SESSION ${index + 1} (${new Date(session.started_at).toLocaleTimeString()}) ---\n`
      if (transcript?.status === 'completed') {
        fullContext += transcript.text + "\n"
      } else {
        fullContext += "[Audio not yet transcribed or transcription failed]\n"
      }

      const nextSession = sessions[index + 1]
      if (nextSession) {
        const gapLog = logs?.find(l =>
          l.action === 'recording_interrupted' &&
          new Date(l.created_at) >= new Date(session.ended_at!) &&
          new Date(l.created_at) <= new Date(nextSession.started_at)
        )
        fullContext += `\n[INTERRUPTION: ${gapLog?.metadata?.reason || 'Recording gap'}]\n\n`
      }
    })

    // 3. AI Analysis via GPT-4o
    const prompt = `
      You are an expert operations analyst. Analyze the following field work transcript and provide a structured report.

      TRANSCRIPT:
      ${fullContext}

      RETURN JSON FORMAT:
      {
        "summary": "High-level summary of work performed",
        "key_findings": [
          {"title": "Finding Title", "description": "Specific observation"}
        ],
        "action_items": [
          {"title": "Task Name", "description": "What needs to be done", "priority": "low/medium/high/urgent"}
        ]
      }
    `

    const aiResponse = await fetch('https://api.openai.com/v1/chat/completions', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${openaiApiKey}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        model: 'gpt-4o',
        messages: [{ role: 'user', content: prompt }],
        response_format: { type: 'json_object' }
      }),
    })

    if (!aiResponse.ok) {
      throw new Error('AI analysis failed')
    }

    const aiResult = await aiResponse.json()
    const reportContent = aiResult.choices[0].message.content

    let reportData;
    try {
      reportData = JSON.parse(reportContent)
      // PRODUCTION HARDENING: Validate structure
      if (!reportData.summary || !Array.isArray(reportData.key_findings) || !Array.isArray(reportData.action_items)) {
        throw new Error('AI returned malformed structured data')
      }
    } catch (e) {
      throw new Error(`Failed to parse AI response: ${e.message}`)
    }

    // 4. Update task_reports
    const { error: reportError } = await supabase
      .from('task_reports')
      .upsert({
        task_id,
        summary: reportData.summary,
        key_findings: reportData.key_findings,
        action_items: reportData.action_items,
        status: 'completed',
        updated_at: new Date().toISOString(),
      }, { onConflict: 'task_id' })

    if (reportError) throw reportError

    return new Response(
      JSON.stringify({ message: 'Report generated successfully', task_id }),
      { headers: { ...corsHeaders, 'Content-Type': 'application/json' }, status: 200 }
    )

  } catch (error) {
    return new Response(
      JSON.stringify({ error: error.message }),
      { headers: { ...corsHeaders, 'Content-Type': 'application/json' }, status: 400 }
    )
  }
})
