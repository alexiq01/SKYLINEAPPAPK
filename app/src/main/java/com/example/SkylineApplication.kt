package com.example

import android.app.Application
import com.example.notifications.SkylineFirebaseMessagingService
import com.example.update.UpdateManager
import com.google.firebase.FirebaseApp

class SkylineApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        
        // Create high-importance Notification Channels for FCM & system alerts
        SkylineFirebaseMessagingService.createNotificationChannel(this)

        // Retrieve and log FCM Registration Token safely if Google Play Services is active
        SkylineFirebaseMessagingService.fetchFcmToken(this)

        // Initialize In-App Update Manager & Remote Config
        UpdateManager.initialize(this)
    }
}
