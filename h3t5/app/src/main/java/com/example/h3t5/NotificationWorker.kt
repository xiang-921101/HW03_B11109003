package com.example.h3t5

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotificationWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.Default) {
        sendNotification()
        Result.success()
    }

    private fun sendNotification() {
        val channelId = "rotate_notification_channel"
        val notificationId = 123

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 創建通知渠道（僅適用於 Android O 及更高版本）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Rotate Notification Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // 創建通知
        val notificationBuilder = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Image Rotated!")
            .setContentText("Your image has been rotated successfully.")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // 顯示通知
        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}
