package com.example.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.MainActivity
import com.example.R

class ExpirationNotificationWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val CHANNEL_ID = "skyline_subscription_alerts"
        const val KEY_ALERT_TYPE = "alert_type"
        const val KEY_EXPIRATION_DATE = "expiration_date"

        const val ALERT_3D = "3d"
        const val ALERT_24H = "24h"
        const val ALERT_1H = "1h"
        const val ALERT_EXPIRED = "expired"
        const val ALERT_LOW_BALANCE = "low_balance"

        const val KEY_BALANCE = "current_balance"
        const val KEY_THRESHOLD = "threshold"
        const val NOTIFICATION_ID_LOW_BALANCE = 1004

        fun sendNotification(context: Context, alertType: String, expirationDate: String) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = context.getString(R.string.notification_channel_desc)
                    enableVibration(true)
                }
                notificationManager.createNotificationChannel(channel)
            }

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val title = context.getString(R.string.app_name)
            val message = when (alertType) {
                ALERT_3D -> context.getString(R.string.notification_expiring_3d)
                ALERT_24H -> context.getString(R.string.notification_expiring_24h)
                ALERT_1H -> context.getString(R.string.notification_expiring_1h)
                else -> context.getString(R.string.notification_expired)
            }

            val notificationId = when (alertType) {
                ALERT_3D -> 1000
                ALERT_24H -> 1001
                ALERT_1H -> 1002
                else -> 1003
            }

            val largeIconBitmap = try {
                android.graphics.BitmapFactory.decodeResource(context.resources, R.drawable.ic_skyline_logo)
            } catch (_: Exception) {
                null
            }

            val displayDate = if (expirationDate.isNotBlank()) {
                "\n${context.getString(R.string.expiration_date)}: $expirationDate"
            } else ""

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_notification)
                .apply {
                    if (largeIconBitmap != null) {
                        setLargeIcon(largeIconBitmap)
                    }
                }
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText("$message$displayDate"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            notificationManager.notify(notificationId, builder.build())
        }

        fun sendLowBalanceNotification(context: Context, currentBalance: Double, threshold: Double) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = context.getString(R.string.notification_channel_desc)
                    enableVibration(true)
                }
                notificationManager.createNotificationChannel(channel)
            }

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                NOTIFICATION_ID_LOW_BALANCE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val title = context.getString(R.string.notification_low_balance_title)
            val balanceFormatted = String.format(java.util.Locale.US, "%,.2f", currentBalance)
            val thresholdFormatted = String.format(java.util.Locale.US, "%,.0f", threshold)
            val message = context.getString(R.string.notification_low_balance_msg, balanceFormatted, thresholdFormatted)

            val largeIconBitmap = try {
                android.graphics.BitmapFactory.decodeResource(context.resources, R.drawable.ic_skyline_logo)
            } catch (_: Exception) {
                null
            }

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_notification)
                .apply {
                    if (largeIconBitmap != null) {
                        setLargeIcon(largeIconBitmap)
                    }
                }
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            notificationManager.notify(NOTIFICATION_ID_LOW_BALANCE, builder.build())
        }
    }

    override suspend fun doWork(): Result {
        val alertType = inputData.getString(KEY_ALERT_TYPE) ?: ALERT_3D
        if (alertType == ALERT_LOW_BALANCE) {
            val balance = inputData.getDouble(KEY_BALANCE, 0.0)
            val threshold = inputData.getDouble(KEY_THRESHOLD, 5000.0)
            sendLowBalanceNotification(context, balance, threshold)
        } else {
            val expirationDate = inputData.getString(KEY_EXPIRATION_DATE) ?: ""
            sendNotification(context, alertType, expirationDate)
        }
        return Result.success()
    }
}
