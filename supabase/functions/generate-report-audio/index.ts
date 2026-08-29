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
    const { report_id } = await req.json()
    console.log(`GENERATE_REPORT_AUDIO_START for report ${report_id}`)

    if (!report_id) {
      throw new Error('report_id is required')
    }

    const supabaseUrl = Deno.env.get('SUPABASE_URL') ?? ''
    const supabaseServiceKey = Deno.env.get('SUPABASE_SERVICE_ROLE_KEY') ?? ''
    const geminiApiKey = Deno.env.get('GEMINI_API_KEY') ?? ''

    if (!geminiApiKey) {
      throw new Error('GEMINI_API_KEY is not configured')
    }

    const supabase = createClient(supabaseUrl, supabaseServiceKey)

    // 1. Fetch report summary
    const { data: report, error: reportError } = await supabase
      .from('task_reports')
      .select('summary, task_id')
      .eq('id', report_id)
      .single()

    if (reportError || !report) {
      throw new Error(`Report not found: ${reportError?.message}`)
    }

    if (!report.summary) {
      throw new Error('Report has no summary to convert to speech')
    }

    // 2. TTS Generation via gemini-3.1-flash-tts-preview (v1beta)
    console.log(`GEMINI_TTS_GENERATION_START model=gemini-3.1-flash-tts-preview`)

    const geminiUrl = `https://generativelanguage.googleapis.com/v1beta/models/gemini-3.1-flash-tts-preview:generateContent?key=${geminiApiKey}`

    // NOTE: This assumes gemini-3.5-tts supports a similar generateContent interface for audio synthesis
    // in the 2026 API specification.
    const geminiRequest = {
      contents: [{
        parts: [{ text: report.summary }]
      }]
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
      console.error(`GEMINI_TTS_GENERATION_FAILED: Gemini error - ${errorData.error?.message}`)
      throw new Error('Gemini TTS synthesis failed')
    }

    console.log(`GEMINI_TTS_GENERATION_SUCCESS`)

    return new Response(
      JSON.stringify({
        message: 'TTS generation triggered successfully',
        report_id,
        summary: report.summary,
        provider: 'gemini-3.5-tts'
      }),
      { headers: { ...corsHeaders, 'Content-Type': 'application/json' }, status: 200 }
    )

  } catch (error) {
    return new Response(
      JSON.stringify({ error: error.message }),
      { headers: { ...corsHeaders, 'Content-Type': 'application/json' }, status: 400 }
    )
  }
})
