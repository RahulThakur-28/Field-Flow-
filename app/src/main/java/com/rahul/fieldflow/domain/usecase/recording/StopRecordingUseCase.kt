package com.rahul.fieldflow.domain.usecase.recording

import android.content.Context
import android.content.Intent
import com.rahul.fieldflow.core.audio.RecordingService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class StopRecordingUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    operator fun invoke() {
        val intent = Intent(context, RecordingService::class.java).apply {
            action = RecordingService.ACTION_STOP
        }
        context.startService(intent)
    }
}
