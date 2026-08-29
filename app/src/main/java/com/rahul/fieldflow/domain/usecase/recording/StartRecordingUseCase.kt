package com.rahul.fieldflow.domain.usecase.recording

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.rahul.fieldflow.core.audio.RecordingService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class StartRecordingUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    operator fun invoke(taskId: String) {
        Log.d("RECORD_DEBUG", "START_RECORDING_USE_CASE_ENTERED taskId=$taskId")
        try {
            val intent = Intent(context, RecordingService::class.java).apply {
                action = RecordingService.ACTION_START
                putExtra(RecordingService.EXTRA_TASK_ID, taskId)
            }
            Log.d("RECORD_DEBUG", "STARTING_RECORDING_SERVICE taskId=$taskId")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
            Log.d("RECORD_DEBUG", "RECORDING_SERVICE_START_REQUEST_SENT")
        } catch (e: Exception) {
            Log.e("RECORD_DEBUG", "START_RECORDING_USE_CASE_EXCEPTION exception=${e.javaClass.simpleName} message=${e.message}")
        }
    }
}
