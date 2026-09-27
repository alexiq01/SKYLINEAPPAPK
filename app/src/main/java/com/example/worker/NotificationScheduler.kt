package com.example.worker

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.MainActivity
import com.example.R
import com.example.notifications.NotificationInboxManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

object NotificationScheduler {

    const val PERIODIC_CHECK_WORK_NAME = "skyline_periodic_subscription_check"
    private const val PREFS_NAME = "skyline_notification_prefs"
    private const val KEY_LAST_3D_NOTIFIED_DATE = "last_notified_3d_exp_date"
    private const val KEY_LAST_EXPIRED_NOTIFIED_DATE = "last_notified_expired_exp_date"
    private const val KEY_LAST_KNOWN_EXP_MILLIS = "last_known_exp_millis"
    private const val KEY_LAST_KNOWN_WAS_EXPIRED = "last_known_was_expired"

    private val patternList = listOf(
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd HH:mm",
        "yyyy-MM-dd",
        "yyyy/MM/dd HH:mm:ss",
        "yyyy/MM/dd HH:mm",
        "yyyy/MM/dd",
        "dd-MM-yyyy HH:mm:ss",
        "dd-MM-yyyy HH:mm",
        "dd-MM-yyyy",
        "dd/MM/yyyy HH:mm:ss",
        "dd/MM/yyyy HH:mm",
        "dd/MM/yyyy"
    )

    /**
     * Schedules WorkManager tasks for subscription alerts:
     * - 3 days before expiration (72 hours)
     * - 24 hours before expiration
     * - 1 hour before expiration
     * - Exact expiration time
     */
    fun scheduleSubscriptionAlerts(context: Context, expirationDateStr: String?) {
        if (expirationDateStr.isNullOrBlank()) return

        val expirationMillis = parseDateToMillis(expirationDateStr) ?: return
        val now = System.currentTimeMillis()

        val workManager = WorkManager.getInstance(context)

        // 1. 3 Days (72 Hours) Alert
        val target3d = expirationMillis - TimeUnit.DAYS.toMillis(3)
        val delay3d = target3d - now
        if (delay3d > 0) {
            val data = Data.Builder()
                .putString(ExpirationNotificationWorker.KEY_ALERT_TYPE, ExpirationNotificationWorker.ALERT_3D)
                .putString(ExpirationNotificationWorker.KEY_EXPIRATION_DATE, expirationDateStr)
                .build()

            val work = OneTimeWorkRequestBuilder<ExpirationNotificationWorker>()
                .setInitialDelay(delay3d, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .build()

            workManager.enqueueUniqueWork("skyline_exp_3d", ExistingWorkPolicy.REPLACE, work)
        } else {
            // If already within the 3-day window and hasn't notified yet:
            val diff = expirationMillis - now
            if (diff > 0 && diff <= TimeUnit.DAYS.toMillis(3)) {
                checkAndTrigger3DayAlertIfNeeded(context, expirationDateStr, expirationMillis)
            }
            workManager.cancelUniqueWork("skyline_exp_3d")
        }

        // 2. 24 Hours Alert
        val target24h = expirationMillis - TimeUnit.HOURS.toMillis(24)
        val delay24h = target24h - now
        if (delay24h > 0) {
            val data = Data.Builder()
                .putString(ExpirationNotificationWorker.KEY_ALERT_TYPE, ExpirationNotificationWorker.ALERT_24H)
                .putString(ExpirationNotificationWorker.KEY_EXPIRATION_DATE, expirationDateStr)
                .build()

            val work = OneTimeWorkRequestBuilder<ExpirationNotificationWorker>()
                .setInitialDelay(delay24h, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .build()

            workManager.enqueueUniqueWork("skyline_exp_24h", ExistingWorkPolicy.REPLACE, work)
        } else {
            workManager.cancelUniqueWork("skyline_exp_24h")
        }

        // 3. 1 Hour Alert
        val target1h = expirationMillis - TimeUnit.HOURS.toMillis(1)
        val delay1h = target1h - now
        if (delay1h > 0) {
            val data = Data.Builder()
                .putString(ExpirationNotificationWorker.KEY_ALERT_TYPE, ExpirationNotificationWorker.ALERT_1H)
                .putString(ExpirationNotificationWorker.KEY_EXPIRATION_DATE, expirationDateStr)
                .build()

            val work = OneTimeWorkRequestBuilder<ExpirationNotificationWorker>()
                .setInitialDelay(delay1h, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .build()

            workManager.enqueueUniqueWork("skyline_exp_1h", ExistingWorkPolicy.REPLACE, work)
        } else {
            workManager.cancelUniqueWork("skyline_exp_1h")
        }

        // 4. Exact Expiration Alert
        val delayExpired = expirationMillis - now
        if (delayExpired > 0) {
            val data = Data.Builder()
                .putString(ExpirationNotificationWorker.KEY_ALERT_TYPE, ExpirationNotificationWorker.ALERT_EXPIRED)
                .putString(ExpirationNotificationWorker.KEY_EXPIRATION_DATE, expirationDateStr)
                .build()

            val work = OneTimeWorkRequestBuilder<ExpirationNotificationWorker>()
                .setInitialDelay(delayExpired, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .build()

            workManager.enqueueUniqueWork("skyline_exp_expired", ExistingWorkPolicy.REPLACE, work)
        } else {
            workManager.cancelUniqueWork("skyline_exp_expired")
        }
    }

    /**
     * Checks if a 3-day expiration alert has already been sent for this expiration date.
     * If not, triggers the local push notification and records that it was sent.
     */
    fun checkAndTrigger3DayAlertIfNeeded(
        context: Context,
        expirationDateStr: String,
        expirationMillis: Long
    ) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lastNotifiedDate = prefs.getString(KEY_LAST_3D_NOTIFIED_DATE, null)

        if (lastNotifiedDate != expirationDateStr) {
            ExpirationNotificationWorker.sendNotification(
                context = context,
                alertType = ExpirationNotificationWorker.ALERT_3D,
                expirationDate = expirationDateStr
            )
            prefs.edit().putString(KEY_LAST_3D_NOTIFIED_DATE, expirationDateStr).apply()
        }
    }

    /**
     * Checks if the subscription has expired and triggers the expired notification with the action
     * to contact the agent for renewal/activation if not already sent for this expiration date.
     */
    fun checkAndTriggerExpiredAlertIfNeeded(
        context: Context,
        expirationDateStr: String
    ) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lastNotifiedDate = prefs.getString(KEY_LAST_EXPIRED_NOTIFIED_DATE, null)

        if (lastNotifiedDate != expirationDateStr) {
            ExpirationNotificationWorker.sendNotification(
                context = context,
                alertType = ExpirationNotificationWorker.ALERT_EXPIRED,
                expirationDate = expirationDateStr
            )
            prefs.edit().putString(KEY_LAST_EXPIRED_NOTIFIED_DATE, expirationDateStr).apply()
        }
    }

    /**
     * Schedules a recurring periodic WorkManager task that checks the subscription expiration date
     * every 12 hours (with flex interval of 1 hour) and triggers local notifications 3 days before
     * expiration, and when expired, even when the app is in the background or killed.
     */
    fun schedulePeriodicSubscriptionCheck(context: Context) {
        val workManager = WorkManager.getInstance(context)
        val constraints = Constraints.Builder().build()

        val periodicWork = PeriodicWorkRequestBuilder<SubscriptionCheckWorker>(
            12, TimeUnit.HOURS,
            1, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .addTag(PERIODIC_CHECK_WORK_NAME)
            .build()

        workManager.enqueueUniquePeriodicWork(
            PERIODIC_CHECK_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWork
        )
    }

    /**
     * Enqueues an immediate on-demand subscription check via WorkManager.
     */
    /**
     * Schedules a support/thank you notification to be shown every 2 days.
     */
    fun schedulePeriodicSupportNotification(context: Context) {
        val workManager = WorkManager.getInstance(context)
        val constraints = Constraints.Builder().build()
        val periodicWork = PeriodicWorkRequestBuilder<SupportNotificationWorker>(
            2, TimeUnit.DAYS
        )
            .setInitialDelay(2, TimeUnit.DAYS)
            .setConstraints(constraints)
            .addTag("periodic_support_notification")
            .build()
        workManager.enqueueUniquePeriodicWork(
            "periodic_support_notification",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWork
        )
    }

    fun triggerImmediateCheck(context: Context) {
        val workManager = WorkManager.getInstance(context)
        val oneTimeWork = OneTimeWorkRequestBuilder<SubscriptionCheckWorker>()
            .build()
        workManager.enqueue(oneTimeWork)
    }

    /**
     * Directly triggers a test notification for verification.
     */
    fun triggerTestNotification(
        context: Context,
        alertType: String = ExpirationNotificationWorker.ALERT_3D,
        expirationDateStr: String? = null
    ) {
        val displayDate = expirationDateStr ?: SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(
            Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(3))
        )
        ExpirationNotificationWorker.sendNotification(context, alertType, displayDate)
    }

    /**
     * Sends local notification and inbox entry when subscription is renewed/reactivated.
     * Message: "تم التفعيل بنجاح! استمتع بجودة الإنترنت فائقة السرعة."
     */
    fun sendSubscriptionRenewedNotification(
        context: Context,
        planName: String? = null,
        newExpirationDate: String? = null
    ) {
        try {
            NotificationInboxManager.init(context)
            NotificationInboxManager.createNotificationChannel(context)
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? android.app.NotificationManager
                ?: return

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                2001,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val title = "تم تفعيل الاشتراك بنجاح 🎉"
            val message = "تم التفعيل بنجاح! استمتع بجودة الإنترنت فائقة السرعة."
            val extraInfo = if (!planName.isNullOrBlank() || !newExpirationDate.isNullOrBlank()) {
                val planText = if (!planName.isNullOrBlank()) "الباقة: $planName" else ""
                val dateText = if (!newExpirationDate.isNullOrBlank()) "ينتهي في: $newExpirationDate" else ""
                listOf(planText, dateText).filter { it.isNotBlank() }.joinToString(" • ")
            } else ""

            val fullBody = if (extraInfo.isNotBlank()) "$message\n$extraInfo" else message

            val largeIconBitmap = try {
                android.graphics.BitmapFactory.decodeResource(context.resources, R.drawable.ic_skyline_logo)
            } catch (_: Exception) {
                null
            }

            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val builder = NotificationCompat.Builder(context, NotificationInboxManager.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_notification)
                .apply {
                    if (largeIconBitmap != null) {
                        setLargeIcon(largeIconBitmap)
                    }
                }
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(fullBody))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSound(soundUri)
                .setVibrate(longArrayOf(0, 250, 150, 250))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            notificationManager.notify(2001, builder.build())

            // Record in Notification Inbox
            NotificationInboxManager.addNotification(
                context = context,
                title = title,
                message = fullBody,
                type = "renewed"
            )
        } catch (_: Exception) {}
    }

    /**
     * Checks if the subscription was previously expired or extended, and triggers the renewed notification.
     */
    fun checkAndTriggerRenewalNotificationIfNeeded(
        context: Context,
        newExpirationStr: String?,
        planName: String? = null
    ) {
        if (newExpirationStr.isNullOrBlank()) return
        val newMillis = parseDateToMillis(newExpirationStr) ?: return
        val now = System.currentTimeMillis()

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lastMillis = prefs.getLong(KEY_LAST_KNOWN_EXP_MILLIS, 0L)
        val wasExpired = prefs.getBoolean(KEY_LAST_KNOWN_WAS_EXPIRED, false)

        val isNowActive = newMillis > now

        if (isNowActive) {
            // If it was expired before and now renewed, or the new expiration date is extended by more than 1 day
            val isExtended = lastMillis > 0 && newMillis > (lastMillis + TimeUnit.DAYS.toMillis(1))
            if (wasExpired || isExtended) {
                sendSubscriptionRenewedNotification(
                    context = context,
                    planName = planName,
                    newExpirationDate = newExpirationStr
                )
            }
            prefs.edit()
                .putLong(KEY_LAST_KNOWN_EXP_MILLIS, newMillis)
                .putBoolean(KEY_LAST_KNOWN_WAS_EXPIRED, false)
                .apply()
        } else {
            // Currently expired
            prefs.edit()
                .putLong(KEY_LAST_KNOWN_EXP_MILLIS, newMillis)
                .putBoolean(KEY_LAST_KNOWN_WAS_EXPIRED, true)
                .apply()
        }
    }

    /**
     * Thread-safe date parsing supporting ISO, slashed, and day-first date formats.
     */
    fun parseDateToMillis(dateStr: String?): Long? {
        if (dateStr.isNullOrBlank()) return null
        val trimmed = dateStr.trim()
        for (pattern in patternList) {
            try {
                val sdf = SimpleDateFormat(pattern, Locale.US)
                sdf.isLenient = false
                val date = sdf.parse(trimmed)
                if (date != null) return date.time
            } catch (_: Exception) {
            }
        }
        return null
    }

    /**
     * Returns true if expiration date is in the future.
     */
    fun isSubscriptionActive(expirationDateStr: String?): Boolean {
        val millis = parseDateToMillis(expirationDateStr) ?: return false
        return millis > System.currentTimeMillis()
    }

    /**
     * Calculates remaining days accurately based on the exact expiration timestamp.
     * Uses Math.floor(diff / 86400000) or ceiling based on actual calendar boundary.
     * diff / (24 * 3600 * 1000) converted cleanly to remaining full/partial days.
     */
    fun calculateRemainingDays(expirationDateStr: String?): Int {
        val millis = parseDateToMillis(expirationDateStr) ?: return 0
        val diff = millis - System.currentTimeMillis()
        if (diff <= 0) return 0
        val days = (diff / (1000L * 60L * 60L * 24L)).toInt()
        return days
    }

    /**
     * Formats expiration date with exact hour and minute (e.g. "26 أكتوبر 2026 الساعة 02:30 م (26 يوم متبقي)")
     * Ensures accurate display matching the exact expiration hour and exact remaining days.
     */
    fun formatExpirationWithRemainingTime(expirationDateStr: String?): String {
        if (expirationDateStr.isNullOrBlank()) return "غير محدد"
        val millis = parseDateToMillis(expirationDateStr) ?: return expirationDateStr
        val date = Date(millis)
        
        // Format date and time e.g. "26 أكتوبر 2026 الساعة 02:30 م"
        val dateSdf = SimpleDateFormat("d MMMM yyyy", Locale("ar"))
        dateSdf.timeZone = TimeZone.getDefault()
        val formattedDate = dateSdf.format(date)

        val timeSdf = SimpleDateFormat("hh:mm a", Locale("ar"))
        timeSdf.timeZone = TimeZone.getDefault()
        val formattedTime = timeSdf.format(date)

        val diff = millis - System.currentTimeMillis()
        if (diff <= 0) {
            return "منتهي في $formattedDate الساعة $formattedTime"
        }

        val remainingDays = (diff / (1000L * 60L * 60L * 24L)).toInt()
        val remainingHours = ((diff % (1000L * 60L * 60L * 24L)) / (1000L * 60L * 60L)).toInt()

        val remainingDaysText = when {
            remainingDays == 0 && remainingHours > 0 -> "$remainingHours ساعة متبقية"
            remainingDays == 1 -> "يوم واحد متبقي"
            remainingDays == 2 -> "يومان متبقيان"
            remainingDays in 3..10 -> "$remainingDays أيام متبقية"
            else -> "$remainingDays يوم متبقي"
        }

        return "$formattedDate ($formattedTime) - $remainingDaysText"
    }

    /**
     * Formats the exact expiration time for display (e.g. "الساعة 02:30 م")
     */
    fun formatExpirationTimeOnly(expirationDateStr: String?): String {
        if (expirationDateStr.isNullOrBlank()) return "--"
        val millis = parseDateToMillis(expirationDateStr) ?: return "--"
        val timeSdf = SimpleDateFormat("hh:mm a", Locale("ar"))
        timeSdf.timeZone = TimeZone.getDefault()
        return timeSdf.format(Date(millis))
    }

    /**
     * Formats full date with hour and minute (e.g. "26 أكتوبر 2026 - 02:30 م")
     */
    fun formatFullDateTime(expirationDateStr: String?): String {
        if (expirationDateStr.isNullOrBlank()) return "--"
        val millis = parseDateToMillis(expirationDateStr) ?: return expirationDateStr
        val sdf = SimpleDateFormat("d MMMM yyyy - hh:mm a", Locale("ar"))
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(Date(millis))
    }

    /**
     * Formats any date string into standard Arabic date: e.g. "13 أكتوبر 2026"
     */
    fun formatArabicDate(dateStr: String?): String {
        if (dateStr.isNullOrBlank()) return "--"
        val millis = parseDateToMillis(dateStr) ?: return dateStr.take(10)
        val sdf = SimpleDateFormat("d MMMM yyyy", Locale("ar"))
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(Date(millis))
    }
}
