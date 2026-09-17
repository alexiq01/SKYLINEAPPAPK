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
import java.util.UUID

class SupportNotificationWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        sendSupportNotification(context)
        return Result.success()
    }

    companion object {
        private const val CHANNEL_ID = "support_channel"
        private const val CHANNEL_NAME = "رسائل الدعم والتحديثات"

        fun sendSupportNotification(context: Context) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
            }

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )

            val message = "اذا عجبك تطبيقي لا تنسى دعم عبر ماستر لي يتم تحديث وتطوير ب استمرار ومتابعة على انستغرام لكل رقم ماستر وحسابات في قسم المطور وشكرا ❤️"

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_notification)
                .setContentTitle("تحديثات وتطوير مستمر 🚀")
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            // Random ID so it doesn't overwrite
            val notificationId = UUID.randomUUID().hashCode()
            notificationManager.notify(notificationId, builder.build())
            
            // Add to inbox as well
            com.example.notifications.NotificationInboxManager.addNotification(
                context = context,
                title = "تحديثات وتطوير مستمر 🚀",
                message = message,
                type = "SUPPORT"
            )
        }
    }
}
