package com.example.codvedx_t_1.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.codvedx_t_1.MainActivity
import com.example.codvedx_t_1.R
import com.example.codvedx_t_1.receiver.NotificationActionReceiver

object NotificationHelper {
    const val CHANNEL_ID = "task_reminders_channel"
    const val CHANNEL_NAME = "Task Reminders"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for upcoming and due tasks"
                enableVibration(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun showTaskNotification(context: Context, taskId: Long, title: String, description: String) {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            taskId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Complete Action
        val completeIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = NotificationActionReceiver.ACTION_COMPLETE
            putExtra("EXTRA_TASK_ID", taskId)
        }
        val completePendingIntent = PendingIntent.getBroadcast(
            context,
            (taskId * 10 + 1).toInt(),
            completeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Snooze Action
        val snoozeIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = NotificationActionReceiver.ACTION_SNOOZE
            putExtra("EXTRA_TASK_ID", taskId)
            putExtra("EXTRA_TASK_TITLE", title)
            putExtra("EXTRA_TASK_DESC", description)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            (taskId * 10 + 2).toInt(),
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle("⏰ Reminder: $title")
            .setContentText(description.ifEmpty { "You have a task scheduled now!" })
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(android.R.drawable.checkbox_on_background, "Complete", completePendingIntent)
            .addAction(android.R.drawable.ic_popup_reminder, "Snooze 10m", snoozePendingIntent)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(taskId.toInt(), builder.build())
    }
}
