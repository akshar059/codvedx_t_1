package com.example.codvedx_t_1.data.repository

import androidx.lifecycle.LiveData
import com.example.codvedx_t_1.data.local.TaskDao
import com.example.codvedx_t_1.data.model.Task
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TaskRepository(private val taskDao: TaskDao) {

    val allActiveTasks: LiveData<List<Task>> = taskDao.getAllActiveTasks()
    val completedTasks: LiveData<List<Task>> = taskDao.getCompletedTasks()
    val archivedTasks: LiveData<List<Task>> = taskDao.getArchivedTasks()
    val trashTasks: LiveData<List<Task>> = taskDao.getTrashTasks()

    fun getTodayTasks(startOfDay: Long, endOfDay: Long): LiveData<List<Task>> {
        return taskDao.getTodayTasks(startOfDay, endOfDay)
    }

    fun searchTasks(query: String): LiveData<List<Task>> {
        return taskDao.searchTasks(query)
    }

    suspend fun getTaskById(id: Long): Task? {
        return taskDao.getTaskById(id)
    }

    suspend fun insertTask(task: Task): Long {
        return taskDao.insertTask(task)
    }

    suspend fun updateTask(task: Task) {
        task.updatedAt = System.currentTimeMillis()
        taskDao.updateTask(task)
    }

    suspend fun toggleTaskComplete(task: Task) {
        task.isCompleted = !task.isCompleted
        task.completedAt = if (task.isCompleted) System.currentTimeMillis() else 0L
        task.updatedAt = System.currentTimeMillis()
        taskDao.updateTask(task)
    }

    suspend fun moveToTrash(task: Task) {
        task.isDeleted = true
        task.updatedAt = System.currentTimeMillis()
        taskDao.updateTask(task)
    }

    suspend fun restoreFromTrash(task: Task) {
        task.isDeleted = false
        task.updatedAt = System.currentTimeMillis()
        taskDao.updateTask(task)
    }

    suspend fun toggleArchive(task: Task) {
        task.isArchived = !task.isArchived
        task.updatedAt = System.currentTimeMillis()
        taskDao.updateTask(task)
    }

    suspend fun deleteTaskPermanently(task: Task) {
        taskDao.deleteTaskPermanently(task)
    }

    suspend fun emptyTrash() {
        taskDao.emptyTrash()
    }

    suspend fun exportTasksJson(): String {
        val tasks = taskDao.getAllTasksSync()
        val gson = Gson()
        return gson.toJson(tasks)
    }

    suspend fun importTasksJson(jsonString: String): Boolean {
        return try {
            val gson = Gson()
            val type = object : TypeToken<List<Task>>() {}.type
            val tasks: List<Task> = gson.fromJson(jsonString, type)
            if (tasks.isNotEmpty()) {
                taskDao.insertTasksBatch(tasks)
                true
            } else false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun seedSampleTasksIfEmpty() = withContext(Dispatchers.IO) {
        val currentTasks = taskDao.getAllTasksSync()
        if (currentTasks.isEmpty()) {
            addDemoTasks()
        }
    }

    suspend fun addDemoTasks() = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val dayMs = 24 * 60 * 60 * 1000L

        val sampleTasks = listOf(
            Task(
                title = "Complete Mobile App Project",
                description = "Finish Room Database & ViewModels for To-Do List application project.",
                dueDate = now,
                dueTime = "17:00",
                priority = "CRITICAL",
                category = "Study",
                tags = "college, android, project",
                subtasksJson = """[{"id":"1","title":"Design Layouts","isCompleted":true},{"id":"2","title":"Setup Room DB","isCompleted":true},{"id":"3","title":"Write Unit Tests","isCompleted":false}]""",
                reminderTime = now + 3600000
            ),
            Task(
                title = "Buy Groceries for the Week",
                description = "Oatmeal, Milk, Eggs, Bananas, Almond Milk, Chicken breast, Vegetables.",
                dueDate = now,
                dueTime = "19:00",
                priority = "MEDIUM",
                category = "Shopping",
                tags = "groceries, home",
                subtasksJson = """[{"id":"1","title":"Milk & Eggs","isCompleted":true},{"id":"2","title":"Fresh Produce","isCompleted":false}]"""
            ),
            Task(
                title = "Morning Workout & Yoga",
                description = "30 mins cardio, 20 mins stretching and core exercises.",
                dueDate = now - dayMs,
                dueTime = "07:30",
                priority = "MEDIUM",
                category = "Health",
                tags = "fitness, morning",
                isCompleted = true,
                completedAt = now - dayMs
            ),
            Task(
                title = "Team Sprint Planning Meeting",
                description = "Review user stories and estimate story points for Sprint 14.",
                dueDate = now + dayMs,
                dueTime = "10:00",
                priority = "HIGH",
                category = "Work",
                tags = "meeting, agile",
                subtasksJson = """[{"id":"1","title":"Prepare Slides","isCompleted":false},{"id":"2","title":"Review Backlog","isCompleted":false}]"""
            ),
            Task(
                title = "Pay Monthly Utility Bills",
                description = "Pay Electricity bill, Fiber Broadband bill, and Water bill online.",
                dueDate = now + (2 * dayMs),
                dueTime = "12:00",
                priority = "HIGH",
                category = "Finance",
                tags = "bills, monthly"
            ),
            Task(
                title = "Read 2 Chapters of Clean Code",
                description = "Focus on Chapter 3: Functions and Chapter 4: Comments.",
                dueDate = now,
                dueTime = "21:00",
                priority = "LOW",
                category = "Study",
                tags = "reading, self-improvement"
            ),
            Task(
                title = "Book Flight Tickets for Summer Trip",
                description = "Compare prices on Skyscanner and book return flight to Goa.",
                dueDate = now + (5 * dayMs),
                dueTime = "15:00",
                priority = "MEDIUM",
                category = "Travel",
                tags = "vacation, travel"
            ),
            Task(
                title = "Dentist Appointment Checkup",
                description = "Bi-annual dental cleaning at City Dental Clinic.",
                dueDate = now + (3 * dayMs),
                dueTime = "11:30",
                priority = "HIGH",
                category = "Health",
                tags = "doctor, health"
            ),
            Task(
                title = "Organize Workspace & Desk",
                description = "Clean monitor screen, cable management, organize tech drawers.",
                dueDate = now - (2 * dayMs),
                dueTime = "16:00",
                priority = "LOW",
                category = "Personal",
                tags = "cleaning, home",
                isCompleted = true,
                completedAt = now - (2 * dayMs)
            ),
            Task(
                title = "Submit Expense Report for October",
                description = "Upload receipts for client dinners and travel reimbursements.",
                dueDate = now + (4 * dayMs),
                dueTime = "18:00",
                priority = "HIGH",
                category = "Finance",
                tags = "work, finance"
            ),
            Task(
                title = "Prepare Presentation for Client",
                description = "Draft slide deck highlighting key project milestones and metrics.",
                dueDate = now + dayMs,
                dueTime = "14:00",
                priority = "CRITICAL",
                category = "Work",
                tags = "presentation, client",
                subtasksJson = """[{"id":"1","title":"Outline Agenda","isCompleted":true},{"id":"2","title":"Design Slides","isCompleted":false}]"""
            ),
            Task(
                title = "Car Maintenance & Oil Change",
                description = "Schedule service with Authorized Service Center.",
                dueDate = now + (6 * dayMs),
                dueTime = "09:00",
                priority = "MEDIUM",
                category = "Personal",
                tags = "car, maintenance"
            ),
            Task(
                title = "Weekly Grocery Restock",
                description = "Buy snacks, coffee beans, sparkling water.",
                dueDate = now - dayMs,
                dueTime = "18:30",
                priority = "LOW",
                category = "Shopping",
                tags = "shopping",
                isCompleted = true,
                completedAt = now - dayMs
            ),
            Task(
                title = "Update Portfolio Website",
                description = "Add recent Android projects and update resume PDF link.",
                dueDate = now + (7 * dayMs),
                dueTime = "20:00",
                priority = "MEDIUM",
                category = "Personal",
                tags = "portfolio, dev"
            ),
            Task(
                title = "Water House Plants",
                description = "Water the Monstera, Succulents, and Snake plant.",
                dueDate = now,
                dueTime = "08:00",
                priority = "LOW",
                category = "Personal",
                tags = "home, plants",
                isCompleted = true,
                completedAt = now
            )
        )

        taskDao.insertTasksBatch(sampleTasks)
    }
}
