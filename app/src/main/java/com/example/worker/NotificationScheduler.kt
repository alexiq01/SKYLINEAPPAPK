package com.example.worker

import android.content.Context
import android.content.SharedPreferences
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

object NotificationScheduler {

    const val PERIODIC_CHECK_WORK_NAME = "skyline_periodic_subscription_check"
    private const val PREFS_NAME = "skyline_notification_prefs"
    private const val KEY_LAST_3D_NOTIFIED_DATE = "last_notified_3d_exp_date"

    const val KEY_LOW_BALANCE_ALERT_ENABLED = "low_balance_alert_enabled"
    const val KEY_LOW_BALANCE_THRESHOLD = "low_balance_threshold"
    const val KEY_LAST_LOW_BALANCE_NOTIFIED_DAY = "last_low_balance_notified_day"
    const val DEFAULT_LOW_BALANCE_THRESHOLD = 5000.0

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
     * Schedules a recurring periodic WorkManager task that checks the subscription expiration date
     * every 12 hours (with flex interval of 1 hour) and triggers local notifications 3 days before
     * expiration even when the app is in the background or killed.
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
     * Checks if low balance alerts are enabled. Defaults to true.
     */
    fun isLowBalanceAlertEnabled(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_LOW_BALANCE_ALERT_ENABLED, true)
    }

    /**
     * Sets whether low balance alerts are enabled.
     */
    fun setLowBalanceAlertEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_LOW_BALANCE_ALERT_ENABLED, enabled)
            .apply()
    }

    /**
     * Retrieves the low balance threshold (IQD). Defaults to 5000 IQD.
     */
    fun getLowBalanceThreshold(context: Context): Double {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getFloat(KEY_LOW_BALANCE_THRESHOLD, DEFAULT_LOW_BALANCE_THRESHOLD.toFloat())
            .toDouble()
    }

    /**
     * Updates the low balance threshold (IQD).
     */
    fun setLowBalanceThreshold(context: Context, threshold: Double) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putFloat(KEY_LOW_BALANCE_THRESHOLD, threshold.toFloat())
            .apply()
    }

    /**
     * Checks if the user's balance is below the configured threshold and triggers
     * a daily reminder notification if enabled and not already sent today.
     */
    fun checkAndTriggerDailyLowBalanceAlertIfNeeded(
        context: Context,
        currentBalance: Double
    ): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val enabled = prefs.getBoolean(KEY_LOW_BALANCE_ALERT_ENABLED, true)
        if (!enabled) return false

        val threshold = prefs.getFloat(KEY_LOW_BALANCE_THRESHOLD, DEFAULT_LOW_BALANCE_THRESHOLD.toFloat()).toDouble()
        if (currentBalance >= threshold) return false

        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        val lastNotifiedDay = prefs.getString(KEY_LAST_LOW_BALANCE_NOTIFIED_DAY, null)

        if (lastNotifiedDay != todayStr) {
            ExpirationNotificationWorker.sendLowBalanceNotification(
                context = context,
                currentBalance = currentBalance,
                threshold = threshold
            )
            prefs.edit().putString(KEY_LAST_LOW_BALANCE_NOTIFIED_DAY, todayStr).apply()
            return true
        }
        return false
    }

    /**
     * Directly triggers a test notification for low balance alert.
     */
    fun triggerTestLowBalanceNotification(
        context: Context,
        currentBalance: Double? = null,
        threshold: Double? = null
    ) {
        val effectiveThreshold = threshold ?: getLowBalanceThreshold(context)
        val effectiveBalance = currentBalance ?: 0.0
        ExpirationNotificationWorker.sendLowBalanceNotification(
            context = context,
            currentBalance = effectiveBalance,
            threshold = effectiveThreshold
        )
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
     * Calculates remaining days with proper ceiling so that 18 hours left displays as 1 day instead of 0.
     */
    fun calculateRemainingDays(expirationDateStr: String?): Int {
        val millis = parseDateToMillis(expirationDateStr) ?: return 0
        val diff = millis - System.currentTimeMillis()
        if (diff <= 0) return 0
        val daysFloat = diff.toDouble() / (1000.0 * 60.0 * 60.0 * 24.0)
        return kotlin.math.ceil(daysFloat).toInt()
    }

    /**
     * Formats expiration date with remaining time in Arabic taking local timezone into account, e.g.:
     * "ينتهي في 13 أكتوبر 2026 - 29 يوم متبقي"
     */
    fun formatExpirationWithRemainingTime(expirationDateStr: String?): String {
        if (expirationDateStr.isNullOrBlank()) return "غير محدد"
        val millis = parseDateToMillis(expirationDateStr) ?: return expirationDateStr
        val date = Date(millis)
        val sdf = SimpleDateFormat("d MMMM yyyy", Locale("ar"))
        sdf.timeZone = TimeZone.getDefault()
        val formattedDate = sdf.format(date)

        val diff = millis - System.currentTimeMillis()
        if (diff <= 0) {
            return "منتهي في $formattedDate"
        }

        val daysFloat = diff.toDouble() / (1000.0 * 60.0 * 60.0 * 24.0)
        val remainingDays = kotlin.math.ceil(daysFloat).toInt()
        val remainingDaysText = when (remainingDays) {
            1 -> "يوم واحد متبقي"
            2 -> "يومان متبقيان"
            in 3..10 -> "$remainingDays أيام متبقية"
            else -> "$remainingDays يوم متبقي"
        }

        return "ينتهي في $formattedDate - $remainingDaysText"
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
