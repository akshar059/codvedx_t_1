package com.example.codvedx_t_1.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.codvedx_t_1.data.local.AppDatabase
import com.example.codvedx_t_1.util.ReminderWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class NotificationActionReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_COMPLETE = "com.example.codvedx_t_1.ACTION_COMPLETE"
        const val ACTION_SNOOZE = "com.example.codvedx_t_1.ACTION_SNOOZE"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra("EXTRA_TASK_ID", -1L)
        val title = intent.getStringExtra("EXTRA_TASK_TITLE") ?: "Task"
        val desc = intent.getStringExtra("EXTRA_TASK_DESC") ?: ""

        if (taskId == -1L) return

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(taskId.toInt())

        when (intent.action) {
            ACTION_COMPLETE -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val dao = AppDatabase.getDatabase(context).taskDao()
                    val task = dao.getTaskById(taskId)
                    if (task != null) {
                        task.isCompleted = true
                        task.completedAt = System.currentTimeMillis()
                        dao.updateTask(task)
                    }
                }
            }
            ACTION_SNOOZE -> {
                val inputData = Data.Builder()
                    .putLong("EXTRA_TASK_ID", taskId)
                    .putString("EXTRA_TASK_TITLE", title)
                    .putString("EXTRA_TASK_DESC", desc)
                    .build()

                val snoozeRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
                    .setInitialDelay(10, TimeUnit.MINUTES)
                    .setInputData(inputData)
                    .build()

                WorkManager.getInstance(context).enqueue(snoozeRequest)
            }
        }
    }
}
