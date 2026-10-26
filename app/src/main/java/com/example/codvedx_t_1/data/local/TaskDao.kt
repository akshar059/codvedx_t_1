package com.example.codvedx_t_1.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.codvedx_t_1.data.model.Task

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE isDeleted = 0 AND isArchived = 0 ORDER BY isCompleted ASC, dueDate ASC, id DESC")
    fun getAllActiveTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE isDeleted = 0 AND isArchived = 0 AND dueDate >= :startOfDay AND dueDate <= :endOfDay ORDER BY isCompleted ASC, priority DESC")
    fun getTodayTasks(startOfDay: Long, endOfDay: Long): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE isDeleted = 0 AND isArchived = 0 AND isCompleted = 1 ORDER BY completedAt DESC")
    fun getCompletedTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE isDeleted = 0 AND isArchived = 1 ORDER BY updatedAt DESC")
    fun getArchivedTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE isDeleted = 1 ORDER BY updatedAt DESC")
    fun getTrashTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE isDeleted = 0 AND isArchived = 0 AND (title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%') ORDER BY isCompleted ASC, dueDate ASC")
    fun searchTasks(query: String): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): Task?

    @Query("SELECT * FROM tasks")
    suspend fun getAllTasksSync(): List<Task>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasksBatch(tasks: List<Task>)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTaskPermanently(task: Task)

    @Query("DELETE FROM tasks WHERE isDeleted = 1")
    suspend fun emptyTrash()
}
