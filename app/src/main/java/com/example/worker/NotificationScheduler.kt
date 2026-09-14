package com.example.worker

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

object NotificationScheduler {

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

    fun scheduleSubscriptionAlerts(context: Context, expirationDateStr: String?) {
        if (expirationDateStr.isNullOrBlank()) return

        val expirationMillis = parseDateToMillis(expirationDateStr) ?: return
        val now = System.currentTimeMillis()

        val workManager = WorkManager.getInstance(context)

        // 1. 24 Hours Alert
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

        // 2. 1 Hour Alert
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

        // 3. Exact Expiration Alert
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
