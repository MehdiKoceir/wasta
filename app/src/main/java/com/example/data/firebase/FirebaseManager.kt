package com.example.data.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.PersistentCacheSettings

/**
 * Manages Firebase core initialization, Auth, and Firestore instances safely.
 * Includes defensive fallback initialization so the app never crashes
 * when google-services.json is absent or in local test/emulator environments.
 */
object FirebaseManager {
    private const val TAG = "FirebaseManager"
    @Volatile
    private var isInitialized = false

    @Synchronized
    fun initialize(context: Context) {
        if (isInitialized) return

        val appContext = context.applicationContext ?: context
        try {
            var defaultApp: FirebaseApp? = null
            if (FirebaseApp.getApps(appContext).isEmpty()) {
                try {
                    // 1. Attempt standard initialization using google-services resources
                    defaultApp = FirebaseApp.initializeApp(appContext)
                    if (defaultApp != null) {
                        Log.i(TAG, "Firebase initialized via default configuration")
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Default Firebase initialization threw exception", e)
                }

                // 2. If defaultApp is null (e.g. google-services.json not present or resource missing), use fallback options
                if (defaultApp == null) {
                    Log.i(TAG, "Default FirebaseApp is null, applying custom fallback options")
                    val fallbackOptions = FirebaseOptions.Builder()
                        .setApplicationId("1:558379283147:android:wasta-dz")
                        .setProjectId("wasta-marketplace-dz")
                        .setApiKey("AIzaSyWastaFallbackApiKeyForPlatformSync2026")
                        .setDatabaseUrl("https://wasta-marketplace-dz.firebaseio.com")
                        .setStorageBucket("wasta-marketplace-dz.appspot.com")
                        .build()
                    defaultApp = FirebaseApp.initializeApp(appContext, fallbackOptions)
                    Log.i(TAG, "Firebase initialized via fallback options successfully")
                }
            } else {
                defaultApp = try { FirebaseApp.getInstance() } catch (e: Exception) { null }
            }

            if (defaultApp != null) {
                isInitialized = true
            }

            // Configure Firestore offline cache persistence settings
            if (isInitialized) {
                try {
                    val firestore = FirebaseFirestore.getInstance()
                    val settings = FirebaseFirestoreSettings.Builder()
                        .setLocalCacheSettings(PersistentCacheSettings.newBuilder().build())
                        .build()
                    firestore.firestoreSettings = settings
                    Log.i(TAG, "Firestore offline persistence enabled")
                } catch (e: Exception) {
                    Log.w(TAG, "Could not apply custom Firestore settings", e)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Fatal error during Firebase initialization", e)
        }
    }

    fun getAuth(context: Context? = null): FirebaseAuth? {
        if (!isInitialized && context != null) {
            initialize(context)
        }
        return try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get FirebaseAuth instance: ${e.message}")
            null
        }
    }

    fun getFirestore(context: Context? = null): FirebaseFirestore? {
        if (!isInitialized && context != null) {
            initialize(context)
        }
        return try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get FirebaseFirestore instance: ${e.message}")
            null
        }
    }
}
