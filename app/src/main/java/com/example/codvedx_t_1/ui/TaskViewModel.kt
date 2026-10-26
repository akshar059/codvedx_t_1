package com.example.codvedx_t_1.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.codvedx_t_1.data.local.AppDatabase
import com.example.codvedx_t_1.data.model.Achievement
import com.example.codvedx_t_1.data.model.Task
import com.example.codvedx_t_1.data.repository.TaskRepository
import com.example.codvedx_t_1.util.GamificationManager
import com.example.codvedx_t_1.util.PreferencesManager
import com.example.codvedx_t_1.util.ReminderWorker
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository
    val prefs: PreferencesManager = PreferencesManager(application)
    val gamification: GamificationManager = GamificationManager(prefs)

    val allActiveTasks: LiveData<List<Task>>
    val completedTasks: LiveData<List<Task>>
    val archivedTasks: LiveData<List<Task>>
    val trashTasks: LiveData<List<Task>>
    val todayTasks: LiveData<List<Task>>

    val searchQuery = MutableLiveData<String>("")
    val searchResults: LiveData<List<Task>>

    init {
        val taskDao = AppDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)

        viewModelScope.launch {
            repository.seedSampleTasksIfEmpty()
        }

        allActiveTasks = repository.allActiveTasks
        completedTasks = repository.completedTasks
        archivedTasks = repository.archivedTasks
        trashTasks = repository.trashTasks

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfDay = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val endOfDay = calendar.timeInMillis - 1

        todayTasks = repository.getTodayTasks(startOfDay, endOfDay)

        searchResults = searchQuery.switchMap { query ->
            if (query.isNull_or_empty()) {
                allActiveTasks
            } else {
                repository.searchTasks(query)
            }
        }
    }

    private fun String?.isNull_or_empty(): Boolean = this == null || this.trim().isEmpty()

    suspend fun getTaskById(id: Long): Task? = repository.getTaskById(id)

    fun insertTask(task: Task, onComplete: ((Long) -> Unit)? = null) = viewModelScope.launch {
        val id = repository.insertTask(task)
        scheduleReminderIfNeeded(id, task)
        onComplete?.invoke(id)
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        repository.updateTask(task)
        scheduleReminderIfNeeded(task.id, task)
    }

    fun toggleTaskComplete(task: Task) = viewModelScope.launch {
        repository.toggleTaskComplete(task)
        if (task.isCompleted) {
            val count = completedTasks.value?.size ?: 0
            gamification.onTaskCompleted(count + 1)
        }
    }

    fun moveToTrash(task: Task) = viewModelScope.launch {
        repository.moveToTrash(task)
    }

    fun restoreFromTrash(task: Task) = viewModelScope.launch {
        repository.restoreFromTrash(task)
    }

    fun toggleArchive(task: Task) = viewModelScope.launch {
        repository.toggleArchive(task)
    }

    fun deleteTaskPermanently(task: Task) = viewModelScope.launch {
        repository.deleteTaskPermanently(task)
    }

    fun emptyTrash() = viewModelScope.launch {
        repository.emptyTrash()
    }

    fun addDemoTasks(onComplete: (() -> Unit)? = null) = viewModelScope.launch {
        repository.addDemoTasks()
        onComplete?.invoke()
    }

    private fun scheduleReminderIfNeeded(taskId: Long, task: Task) {
        if (task.reminderTime > System.currentTimeMillis()) {
            val delay = task.reminderTime - System.currentTimeMillis()

            val inputData = Data.Builder()
                .putLong("EXTRA_TASK_ID", taskId)
                .putString("EXTRA_TASK_TITLE", task.title)
                .putString("EXTRA_TASK_DESC", task.description)
                .build()

            val request = OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .build()

            WorkManager.getInstance(getApplication()).enqueue(request)
        }
    }

    fun getAchievements(): List<Achievement> {
        val count = completedTasks.value?.size ?: 0
        return gamification.getAchievements(count)
    }

    fun exportData(onResult: (String) -> Unit) = viewModelScope.launch {
        val json = repository.exportTasksJson()
        onResult(json)
    }

    fun importData(json: String, onResult: (Boolean) -> Unit) = viewModelScope.launch {
        val success = repository.importTasksJson(json)
        onResult(success)
    }
}
