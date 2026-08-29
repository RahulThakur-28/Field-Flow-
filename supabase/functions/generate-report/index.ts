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
    console.log(`GENERATE_REPORT_START for task ${task_id}`)

    if (!task_id) {
      throw new Error('task_id is required')
    }

    const supabaseUrl = Deno.env.get('SUPABASE_URL') ?? ''
    const supabaseServiceKey = Deno.env.get('SUPABASE_SERVICE_ROLE_KEY') ?? ''
    const geminiApiKey = Deno.env.get('GEMINI_API_KEY') ?? ''

    if (!geminiApiKey) {
      throw new Error('GEMINI_API_KEY is not configured')
    }

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
      console.error(`GENERATE_REPORT_FAILED: No sessions`)
      throw new Error('No recording sessions found for this task')
    }
    console.log(`SESSIONS_FOUND: ${sessions.length}`)
    console.log(`TRANSCRIPTS_FOUND: ${transcripts?.length || 0}`)

    // 2. Guard: Ensure at least one completed transcript exists
    const completedTranscripts = transcripts?.filter(t => t.status === 'completed') || []
    if (completedTranscripts.length === 0) {
      console.log(`GENERATE_REPORT_WAITING: No completed transcripts yet for task ${task_id}`)
      return new Response(
        JSON.stringify({ message: 'Waiting for transcriptions to complete', task_id, status: 'waiting' }),
        { headers: { ...corsHeaders, 'Content-Type': 'application/json' }, status: 200 }
      )
    }

    // 3. Reconstruct chronological context
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
    console.log(`TIMELINE_BUILT`)

    // 4. AI Analysis via Gemini 3.6 Flash
    console.log(`GEMINI_REPORT_REQUEST_START`)
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

    // Update to v1beta API and gemini-3.6-flash for 2026 stability
    const geminiUrl = `https://generativelanguage.googleapis.com/v1beta/models/gemini-3.6-flash:generateContent?key=${geminiApiKey}`

    const geminiRequest = {
      contents: [{
        parts: [{ text: prompt }]
      }],
      generationConfig: {
        responseMimeType: "application/json"
      }
    }

    const geminiResponse = await fetch(geminiUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(geminiRequest),
    })

    if (!geminiResponse.ok) {
      const errorData = await geminiResponse.json()
      console.error(`GEMINI_REPORT_REQUEST_FAILED: Gemini error - ${JSON.stringify(errorData)}`)
      throw new Error(`Gemini analysis failed: ${errorData.error?.message || 'Unknown error'}`)
    }

    const geminiResult = await geminiResponse.json()
    console.log(`GEMINI_REPORT_REQUEST_SUCCESS`)

    const reportContent = geminiResult.candidates?.[0]?.content?.parts?.[0]?.text
    if (!reportContent) {
      throw new Error("Gemini returned no report content")
    }

    let reportData;
    try {
      reportData = JSON.parse(reportContent)
      if (!reportData.summary || !Array.isArray(reportData.key_findings) || !Array.isArray(reportData.action_items)) {
        throw new Error('Gemini returned malformed structured data')
      }
    } catch (e) {
      throw new Error(`Failed to parse Gemini response: ${e.message}`)
    }

    // 5. Update task_reports
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

    if (reportError) {
      console.error(`GENERATE_REPORT_FAILED: DB error - ${reportError.message}`)
      throw reportError
    }

    console.log(`REPORT_SAVE_SUCCESS`)

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
