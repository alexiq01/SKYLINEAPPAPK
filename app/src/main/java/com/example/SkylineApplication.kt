package com.example

import android.app.Application
import android.content.Context
import com.example.notifications.SkylineFirebaseMessagingService
import com.example.update.UpdateManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging

class SkylineApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase safely
        try {
            FirebaseApp.initializeApp(this)
            
            // Check Google Play Services before initializing FCM
            val isGmsAvailable = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS
            if (isGmsAvailable) {
                SkylineFirebaseMessagingService.initAndSubscribeTopics(this)
            } else {
                // If GMS is not active (such as browser emulator), clear lingering topic queues to prevent background sync errors
                try {
                    getSharedPreferences("com.google.android.gms.appid", Context.MODE_PRIVATE).edit().clear().apply()
                    getSharedPreferences("com.google.firebase.messaging", Context.MODE_PRIVATE).edit().clear().apply()
                } catch (_: Throwable) {}
            }
        } catch (_: Throwable) {}
        
        // Create high-importance Notification Channels for FCM & system alerts
        SkylineFirebaseMessagingService.createNotificationChannel(this)

        // Initialize In-App Update Manager & Remote Config
        UpdateManager.initialize(this)
    }
}
