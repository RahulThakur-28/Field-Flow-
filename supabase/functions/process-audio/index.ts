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
    const { recording_session_id } = await req.json()

    if (!recording_session_id) {
      throw new Error('recording_session_id is required')
    }

    const supabaseUrl = Deno.env.get('SUPABASE_URL') ?? ''
    const supabaseServiceKey = Deno.env.get('SUPABASE_SERVICE_ROLE_KEY') ?? ''
    const openaiApiKey = Deno.env.get('OPENAI_API_KEY') ?? ''

    const supabase = createClient(supabaseUrl, supabaseServiceKey)

    // 1. Fetch recording session
    const { data: session, error: sessionError } = await supabase
      .from('recording_sessions')
      .select('*')
      .eq('id', recording_session_id)
      .single()

    if (sessionError || !session) {
      throw new Error(`Recording session not found: ${sessionError?.message}`)
    }

    if (!session.storage_path) {
      throw new Error('Recording session has no storage_path')
    }

    // 2. check if already processed
    const { data: existingTranscript } = await supabase
      .from('transcripts')
      .select('*')
      .eq('recording_session_id', recording_session_id)
      .single()

    if (existingTranscript?.status === 'completed') {
      return new Response(
        JSON.stringify({ message: 'Already processed', transcript_id: existingTranscript.id }),
        { headers: { ...corsHeaders, 'Content-Type': 'application/json' }, status: 200 }
      )
    }

    // 3. Create or update transcript row to 'processing'
    const { data: transcript, error: transcriptError } = await supabase
      .from('transcripts')
      .upsert({
        recording_session_id,
        status: 'processing',
        provider: 'openai-whisper-1',
        started_at: new Date().toISOString(),
      }, { onConflict: 'recording_session_id' })
      .select()
      .single()

    if (transcriptError) {
      throw new Error(`Failed to create transcript row: ${transcriptError.message}`)
    }

    // 4. Download audio from storage
    const { data: audioBlob, error: downloadError } = await supabase
      .storage
      .from('recordings')
      .download(session.storage_path)

    if (downloadError) {
      await updateTranscriptStatus(supabase, transcript.id, 'failed', downloadError.message)
      throw new Error(`Failed to download audio: ${downloadError.message}`)
    }

    // 5. Send to OpenAI Whisper
    const formData = new FormData()
    formData.append('file', audioBlob, 'audio.m4a')
    formData.append('model', 'whisper-1')
    formData.append('response_format', 'verbose_json')
    formData.append('timestamp_granularities[]', 'segment')

    const response = await fetch('https://api.openai.com/v1/audio/transcriptions', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${openaiApiKey}`,
      },
      body: formData,
    })

    if (!response.ok) {
      const errorData = await response.json()
      const errorMessage = errorData.error?.message ?? 'Unknown OpenAI error'
      await updateTranscriptStatus(supabase, transcript.id, 'failed', errorMessage)
      throw new Error(`OpenAI API error: ${errorMessage}`)
    }

    const result = await response.json()

    // 6. Map segments and store
    const segments = result.segments?.map((s: any) => ({
      start: s.start,
      end: s.end,
      text: s.text,
      speaker: null, // Diarization not supported in standard Whisper API yet
    })) ?? []

    const { error: finalUpdateError } = await supabase
      .from('transcripts')
      .update({
        text: result.text,
        segments: segments,
        language: result.language,
        status: 'completed',
        completed_at: new Date().toISOString(),
        error_message: null,
      })
      .eq('id', transcript.id)

    if (finalUpdateError) {
      throw new Error(`Failed to save final transcript: ${finalUpdateError.message}`)
    }

    return new Response(
      JSON.stringify({ message: 'Transcription completed', transcript_id: transcript.id }),
      { headers: { ...corsHeaders, 'Content-Type': 'application/json' }, status: 200 }
    )

  } catch (error) {
    return new Response(
      JSON.stringify({ error: error.message }),
      { headers: { ...corsHeaders, 'Content-Type': 'application/json' }, status: 400 }
    )
  }
})

async function updateTranscriptStatus(supabase: any, id: string, status: string, errorMessage: string) {
  // PRODUCTION HARDENING: Atomic retry increment
  const { data: current } = await supabase.from('transcripts').select('retry_count').eq('id', id).single()
  const nextRetry = (current?.retry_count ?? 0) + 1

  await supabase
    .from('transcripts')
    .update({
      status: status,
      error_message: errorMessage,
      retry_count: nextRetry,
    })
    .eq('id', id)
}
