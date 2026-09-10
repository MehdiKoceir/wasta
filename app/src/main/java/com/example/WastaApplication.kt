package com.example

import android.app.Application
import android.util.Log
import com.example.data.firebase.FirebaseManager

class WastaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            FirebaseManager.initialize(this)
            Log.i("WastaApplication", "FirebaseManager initialized at application startup")
        } catch (e: Exception) {
            Log.e("WastaApplication", "Failed to initialize Firebase at startup", e)
        }
    }
}
