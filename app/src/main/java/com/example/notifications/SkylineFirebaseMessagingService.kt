package com.example.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.update.UpdateManager
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * SkylineFirebaseMessagingService
 * 
 * Handles Firebase Cloud Messaging (FCM) operations:
 * - Receiving background/foreground push messages
 * - Token management & registration
 * - Dynamic Notification Channels (Android 8.0+)
 * - Adding received messages to the in-app notification inbox
 * - Triggering Remote Config update checks on update broadcasts
 */
class SkylineFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "SkylineFCM"
        const val CHANNEL_ID = "skyline_push_notifications"
        const val CHANNEL_NAME = "إشعارات سكايلان العامة والتحديثات"

        /**
         * Safely creates the notification channel for Android 8.0+
         */
        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "قناة مخصصة لإشعارات التطبيق السحابية والتنبيهات"
                    enableLights(true)
                    enableVibration(true)
                }
                notificationManager.createNotificationChannel(channel)
            }
        }

        /**
         * Asynchronously fetches the current FCM registration token if Google Play Services is available.
         */
        fun fetchFcmToken(context: Context? = null, onTokenReceived: ((String) -> Unit)? = null) {
            try {
                if (context != null) {
                    val gms = com.google.android.gms.common.GoogleApiAvailability.getInstance()
                    val resultCode = gms.isGooglePlayServicesAvailable(context)
                    if (resultCode != com.google.android.gms.common.ConnectionResult.SUCCESS) {
                        Log.d(TAG, "Google Play Services not ready for FCM registration (code: $resultCode)")
                        return
                    }
                }
                FirebaseMessaging.getInstance().token
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful && task.result != null) {
                            val token = task.result
                            Log.d(TAG, "Current FCM Token: $token")
                            onTokenReceived?.invoke(token)
                        } else {
                            Log.d(TAG, "FCM token not available in current environment: ${task.exception?.message}")
                        }
                    }
            } catch (e: Throwable) {
                Log.d(TAG, "FCM token check skipped: ${e.message}")
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel(this)
    }

    /**
     * Called when a new FCM registration token is generated.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "FCM Registration Token generated: $token")
        // Save or sync token if required
    }

    /**
     * Called when a message is received from Firebase Cloud Messaging.
     * Works for both Foreground & Background messages.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "FCM Message received from: ${remoteMessage.from}")

        val data = remoteMessage.data
        val notification = remoteMessage.notification

        // Extract title and body from data payload or notification payload
        val title = data["title"] ?: notification?.title ?: getString(R.string.app_name)
        val body = data["body"] ?: data["message"] ?: notification?.body ?: "يوجد إشعار جديد"
        val type = data["type"] ?: "general"

        // 1. Add notification to in-app inbox
        try {
            NotificationInboxManager.addNotification(
                context = applicationContext,
                title = title,
                message = body,
                type = type
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to store notification in inbox: ${e.message}")
        }

        // 2. Display System Notification
        showSystemNotification(title, body, type)

        // 3. If the message indicates a new update is available, trigger Remote Config check
        if (type == "app_update" || data["update_available"] == "true") {
            UpdateManager.checkForUpdates(applicationContext, forceImmediateFetch = true)
        }
    }

    /**
     * Builds and displays a Heads-Up system notification.
     */
    private fun showSystemNotification(title: String, message: String, type: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(this)

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("notification_type", type)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            (System.currentTimeMillis() % 10000).toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_skyline_logo)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setSound(defaultSoundUri)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        notificationManager.notify((System.currentTimeMillis() % 10000).toInt(), notificationBuilder.build())
    }
}
