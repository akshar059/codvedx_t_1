package com.example.codvedx_t_1.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var title: String,
    var description: String = "",
    var dueDate: Long = 0L,
    var dueTime: String = "",
    var priority: String = "MEDIUM", // LOW, MEDIUM, HIGH, CRITICAL
    var category: String = "Personal", // Personal, Work, Study, Shopping, Health, Finance, Travel
    var tags: String = "",
    var reminderTime: Long = 0L,
    var recurrence: String = "NONE", // NONE, DAILY, WEEKLY, MONTHLY, YEARLY
    var subtasksJson: String = "[]",
    var notes: String = "",
    var createdAt: Long = System.currentTimeMillis(),
    var updatedAt: Long = System.currentTimeMillis(),
    var completedAt: Long = 0L,
    var isCompleted: Boolean = false,
    var isArchived: Boolean = false,
    var isDeleted: Boolean = false
)
