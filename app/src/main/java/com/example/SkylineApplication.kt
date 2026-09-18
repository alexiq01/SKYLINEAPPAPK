package com.example

import android.app.Application
import com.example.notifications.SkylineFirebaseMessagingService
import com.example.update.UpdateManager
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class SkylineApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        
        // Create high-importance Notification Channels for FCM & system alerts
        SkylineFirebaseMessagingService.createNotificationChannel(this)

        // Retrieve and log FCM Registration Token safely if Google Play Services is active
        SkylineFirebaseMessagingService.fetchFcmToken(this)
        
        // Prepare Firestore with offline persistence
        val firestore = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .build()
        firestore.firestoreSettings = settings

        // Initialize In-App Update Manager & Remote Config
        UpdateManager.initialize(this)
    }
}
