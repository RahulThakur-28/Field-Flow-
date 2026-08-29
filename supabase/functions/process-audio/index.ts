import { serve } from "https://deno.land/std@0.168.0/http/server.ts"
import { createClient } from "https://esm.sh/@supabase/supabase-js@2"
import { encode } from "https://deno.land/std@0.168.0/encoding/base64.ts"

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
    console.log(`PROCESS_AUDIO_START for session ${recording_session_id}`)

    if (!recording_session_id) {
      throw new Error('recording_session_id is required')
    }

    const supabaseUrl = Deno.env.get('SUPABASE_URL') ?? ''
    const supabaseServiceKey = Deno.env.get('SUPABASE_SERVICE_ROLE_KEY') ?? ''
    const geminiApiKey = Deno.env.get('GEMINI_API_KEY') ?? ''

    if (!geminiApiKey) {
      throw new Error('GEMINI_API_KEY is not configured')
    }

    const supabase = createClient(supabaseUrl, supabaseServiceKey)

    // 1. Fetch recording session
    const { data: session, error: sessionError } = await supabase
      .from('recording_sessions')
      .select('*')
      .eq('id', recording_session_id)
      .single()

    if (sessionError || !session) {
      console.error(`PROCESS_AUDIO_FAILED: Session not found`)
      throw new Error(`Recording session not found: ${sessionError?.message}`)
    }
    console.log(`SESSION_FOUND: task_id=${session.task_id}`)

    if (!session.storage_path) {
      throw new Error('Recording session has no storage_path')
    }

    // 2. Check if already processed
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
    console.log(`TRANSCRIPT_UPSERT_START for session ${recording_session_id}`)
    const { data: transcript, error: transcriptError } = await supabase
      .from('transcripts')
      .upsert({
        recording_session_id,
        status: 'processing',
        provider: 'gemini-3.6-flash',
        started_at: new Date().toISOString(),
        error_message: null, // Clear any previous errors
      }, { onConflict: 'recording_session_id' })
      .select()
      .single()

    if (transcriptError) {
      console.error(`TRANSCRIPT_UPSERT_FAILED: message=${transcriptError.message}, code=${transcriptError.code}`)
      throw new Error(`Failed to create transcript row: ${transcriptError.message}`)
    }
    console.log(`TRANSCRIPT_UPSERT_SUCCESS: id=${transcript.id}`)

    // 4. Download audio from storage
    console.log(`AUDIO_DOWNLOAD_START: ${session.storage_path}`)
    const { data: audioBlob, error: downloadError } = await supabase
      .storage
      .from('recordings')
      .download(session.storage_path)

    if (downloadError) {
      console.error(`PROCESS_AUDIO_FAILED: Audio download error`)
      await updateTranscriptStatus(supabase, transcript.id, 'failed', downloadError.message)
      throw new Error(`Failed to download audio: ${downloadError.message}`)
    }
    console.log(`AUDIO_DOWNLOAD_SUCCESS`)

    // 5. Send to Gemini for transcription
    console.log(`GEMINI_TRANSCRIPTION_START model=gemini-3.5-transcribe`)

    // Convert audio to base64 using a safer method
    const audioArrayBuffer = await audioBlob.arrayBuffer()
    const audioBase64 = encode(audioArrayBuffer)
    console.log(`AUDIO_BASE64_PREPARED length=${audioBase64.length}`)

    // Use gemini-3.6-flash for robust audio-to-text
    const geminiUrl = `https://generativelanguage.googleapis.com/v1beta/models/gemini-3.6-flash:generateContent?key=${geminiApiKey}`

    const geminiRequest = {
      contents: [{
        parts: [
          { text: "Transcribe the following audio precisely. Provide only the transcription text." },
          {
            inlineData: {
              mimeType: "audio/mpeg",
              data: audioBase64
            }
          }
        ]
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
      const errorMessage = errorData.error?.message ?? 'Unknown Gemini error'
      console.error(`GEMINI_TRANSCRIPTION_FAILED: Gemini error - ${errorMessage}`)
      await updateTranscriptStatus(supabase, transcript.id, 'failed', errorMessage)
      throw new Error(`Gemini API error: ${errorMessage}`)
    }

    const geminiResult = await geminiResponse.json()
    console.log(`GEMINI_RESPONSE_SUCCESS`)

    if (!geminiResult.candidates || geminiResult.candidates.length === 0) {
      console.error(`GEMINI_TRANSCRIPTION_FAILED: No candidates in response`, JSON.stringify(geminiResult))
      throw new Error("Gemini returned no candidates")
    }

    const transcriptionText = geminiResult.candidates[0].content?.parts?.[0]?.text
    if (!transcriptionText) {
      console.error(`GEMINI_TRANSCRIPTION_FAILED: No text in parts`, JSON.stringify(geminiResult))
      throw new Error(`Gemini returned no transcription text. Result: ${JSON.stringify(geminiResult)}`)
    }
    console.log(`TRANSCRIPT_TEXT_LENGTH: ${transcriptionText.length}`)

    // 6. Construct transcript and store
    // Use session duration for a single segment since we are in plain text mode
    const duration = session.duration_seconds || 0
    const segments = [{
      start: 0.0,
      end: Number(duration),
      text: transcriptionText,
      speaker: null
    }]

    const { error: finalUpdateError } = await supabase
      .from('transcripts')
      .update({
        text: transcriptionText,
        segments: segments,
        language: 'en', // Defaulting to en in plain text mode
        status: 'completed',
        completed_at: new Date().toISOString(),
        error_message: null,
      })
      .eq('id', transcript.id)

    if (finalUpdateError) {
      throw new Error(`Failed to save final transcript: ${finalUpdateError.message}`)
    }

    console.log(`TRANSCRIPT_SAVE_SUCCESS for session ${recording_session_id}`)

    // 7. Trigger report generation if task is completed
    const { data: task, error: taskError } = await supabase
      .from('tasks')
      .select('status')
      .eq('id', session.task_id)
      .single()

    if (taskError) {
      console.error(`Failed to fetch task status: ${taskError.message}`)
    } else if (task?.status === 'completed') {
      console.log(`Triggering report generation for completed task ${session.task_id}`)
      supabase.functions.invoke('generate-report', {
        body: { task_id: session.task_id }
      }).then(() => {
          console.log(`GENERATE_REPORT_TRIGGER_SUCCESS for task ${session.task_id}`)
      }).catch(err => {
          console.error(`GENERATE_REPORT_TRIGGER_FAILED for task ${session.task_id}: ${err.message}`)
      })
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
