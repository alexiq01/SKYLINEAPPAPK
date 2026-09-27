package com.example.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.data.storage.SessionManager

/**
 * BroadcastReceiver triggered when the device boots or when the app is updated.
 * Ensures the subscription expiration monitoring tasks and 3-day notifications
 * are immediately scheduled in WorkManager.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return
        val action = intent?.action
        Log.d(TAG, "BootReceiver received action: $action")

        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            action == "android.intent.action.QUICKBOOT_POWERON"
        ) {
            try {
                // Initialize notification channels
                com.example.notifications.NotificationInboxManager.init(context)

                // 1. Reschedule periodic background checks (every 12h)
                NotificationScheduler.schedulePeriodicSubscriptionCheck(context)
                NotificationScheduler.schedulePeriodicSupportNotification(context)

                // 2. Schedule exact expiration alerts for the cached subscription
                val sessionManager = SessionManager(context)
                val cached = sessionManager.getCachedSubscription()
                val expirationDate = cached?.service?.expiration

                if (!expirationDate.isNullOrBlank()) {
                    NotificationScheduler.scheduleSubscriptionAlerts(context, expirationDate)
                }

                // 3. Trigger an immediate background check to refresh expiration if needed
                NotificationScheduler.triggerImmediateCheck(context)
            } catch (e: Exception) {
                Log.e(TAG, "Error in BootReceiver: ${e.message}", e)
            }
        }
    }

    companion object {
        private const val TAG = "SkylineBootReceiver"
    }
}
