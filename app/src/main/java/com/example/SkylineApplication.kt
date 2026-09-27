package com.example

import android.app.Application
import com.example.notifications.NotificationInboxManager
import com.example.update.UpdateManager
import com.google.firebase.FirebaseApp

class SkylineApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase safely for Remote Config and Analytics
        try {
            FirebaseApp.initializeApp(this)
        } catch (_: Throwable) {}
        
        // Initialize Notification Channels & Inbox
        NotificationInboxManager.init(this)

        // Initialize WorkManager Periodic Subscription Expiration Checker
        try {
            com.example.worker.NotificationScheduler.schedulePeriodicSubscriptionCheck(this)
            com.example.worker.NotificationScheduler.schedulePeriodicSupportNotification(this)
        } catch (_: Throwable) {}

        // Initialize In-App Update Manager & Remote Config
        UpdateManager.initialize(this)
    }
}
