package com.example.codvedx_t_1.util

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class ReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val taskId = inputData.getLong("EXTRA_TASK_ID", -1L)
        val title = inputData.getString("EXTRA_TASK_TITLE") ?: "Task Reminder"
        val desc = inputData.getString("EXTRA_TASK_DESC") ?: ""

        if (taskId != -1L) {
            NotificationHelper.showTaskNotification(context, taskId, title, desc)
        }

        return Result.success()
    }
}
