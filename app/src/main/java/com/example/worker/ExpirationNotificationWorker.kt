package com.example.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.MainActivity
import com.example.R
import com.example.ui.components.DEVELOPER_WHATSAPP_NUMBER
import com.example.ui.components.DEVELOPER_WHATSAPP_URL

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

        fun sendNotification(context: Context, alertType: String, expirationDate: String) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                    .build()

                val channel = NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = context.getString(R.string.notification_channel_desc)
                    enableLights(true)
                    lightColor = android.graphics.Color.BLUE
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 300, 200, 300)
                    setSound(soundUri, audioAttributes)
                    setShowBadge(true)
                }
                notificationManager.createNotificationChannel(channel)
            }

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("navigate_to", "home")
                putExtra("alert_type", alertType)
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                (System.currentTimeMillis() % 10000).toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val title = "تنبيه اشتراك Skyline"
            val message = when (alertType) {
                ALERT_3D -> "متبقي 3 أيام فقط على انتهاء اشتراكك في خدمة الإنترنت! يرجى تجديد الاشتراك لتجنب انقطاع الخدمة."
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

            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

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
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSound(soundUri)
                .setVibrate(longArrayOf(0, 300, 200, 300))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            // Direct WhatsApp action button for quick renewal
            val whatsappIntent = Intent(Intent.ACTION_VIEW).apply {
                val encodedMsg = if (alertType == ALERT_3D) {
                    Uri.encode("مرحباً، متبقي 3 أيام على انتهاء اشتراكي في خدمة Skyline وأود تجديد الاشتراك.")
                } else if (alertType == ALERT_EXPIRED) {
                    Uri.encode("مرحباً، انتهى اشتراكي في خدمة Skyline وأود تفعيل وتجديد الاشتراك.")
                } else {
                    Uri.encode("مرحباً، أود الاستفسار عن تجديد اشتراكي في خدمة Skyline.")
                }
                data = Uri.parse("https://wa.me/${DEVELOPER_WHATSAPP_NUMBER.replace("+", "")}?text=$encodedMsg")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            val whatsappPendingIntent = PendingIntent.getActivity(
                context,
                1005,
                whatsappIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            builder.addAction(
                R.drawable.ic_stat_notification,
                context.getString(R.string.notification_action_contact_agent),
                whatsappPendingIntent
            )

            notificationManager.notify(notificationId, builder.build())

            // Record into local Notification Inbox so user sees it in their Notifications tab
            try {
                val fullMsg = if (expirationDate.isNotBlank()) "$message ($expirationDate)" else message
                com.example.notifications.NotificationInboxManager.addNotification(
                    context = context,
                    title = title,
                    message = fullMsg,
                    type = alertType
                )
            } catch (_: Exception) {}
        }
    }

    override suspend fun doWork(): Result {
        val alertType = inputData.getString(KEY_ALERT_TYPE) ?: ALERT_3D
        val expirationDate = inputData.getString(KEY_EXPIRATION_DATE) ?: ""
        sendNotification(context, alertType, expirationDate)
        return Result.success()
    }
}
