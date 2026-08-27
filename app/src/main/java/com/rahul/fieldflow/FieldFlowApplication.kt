package com.rahul.fieldflow

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import org.maplibre.android.MapLibre

@HiltAndroidApp
class FieldFlowApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        MapLibre.getInstance(this)
    }
}