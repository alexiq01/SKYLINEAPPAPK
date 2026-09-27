package com.example.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.repository.SkylineRepository
import com.example.data.storage.SessionManager
import java.util.concurrent.TimeUnit

/**
 * Background WorkManager task that checks the user's subscription expiration date,
 * refreshes subscription data if network is available, and triggers local push
 * notifications 3 days before the subscription ends (and ensures exact alarms are scheduled).
 */
class SubscriptionCheckWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val sessionManager = SessionManager(context)
            if (!sessionManager.hasValidSession()) {
                Log.d(TAG, "No valid user session; skipping subscription check")
                return Result.success()
            }

            var subscription = sessionManager.getCachedSubscription()

            // Try refreshing latest subscription from backend if online
            try {
                val repository = SkylineRepository(context)
                val refreshResult = repository.fetchSubscriptionData()
                if (refreshResult.isSuccess) {
                    subscription = refreshResult.getOrNull()
                }
            } catch (e: Exception) {
                Log.w(TAG, "Could not reach server, using cached subscription", e)
            }

            val expirationStr = subscription?.service?.expiration
            if (!expirationStr.isNullOrBlank()) {
                val expirationMillis = NotificationScheduler.parseDateToMillis(expirationStr)
                if (expirationMillis != null) {
                    val now = System.currentTimeMillis()
                    val diff = expirationMillis - now

                    Log.d(TAG, "Checking expiration: $expirationStr, diffMillis=$diff")

                    // Check if subscription has been renewed / extended and trigger renewal notification
                    NotificationScheduler.checkAndTriggerRenewalNotificationIfNeeded(
                        context = context,
                        newExpirationStr = expirationStr,
                        planName = subscription?.service?.profileName
                    )

                    // Schedule precise one-time workers (3-day, 24h, 1h, expired)
                    NotificationScheduler.scheduleSubscriptionAlerts(context, expirationStr)

                    // If remaining time is 3 days or fewer (and subscription not yet expired)
                    val threeDaysMillis = TimeUnit.DAYS.toMillis(3)
                    if (diff in 1..threeDaysMillis) {
                        NotificationScheduler.checkAndTrigger3DayAlertIfNeeded(
                            context = context,
                            expirationDateStr = expirationStr,
                            expirationMillis = expirationMillis
                        )
                    } else if (diff <= 0) {
                        // Subscription expired: notify user with action to contact agent to activate/renew
                        NotificationScheduler.checkAndTriggerExpiredAlertIfNeeded(
                            context = context,
                            expirationDateStr = expirationStr
                        )
                    }
                }
            }

            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error executing SubscriptionCheckWorker", e)
            Result.retry()
        }
    }

    companion object {
        private const val TAG = "SubCheckWorker"
    }
}
