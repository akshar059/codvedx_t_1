package com.example.codvedx_t_1.ui.adapter

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.codvedx_t_1.R
import com.example.codvedx_t_1.data.model.Task
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskAdapter(
    private val onItemClick: (Task) -> Unit,
    private val onCheckChanged: (Task) -> Unit,
    private val onDeleteClick: (Task) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task)
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkBox: CheckBox = itemView.findViewById(R.id.cbTaskComplete)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTaskTitle)
        private val tvCategory: TextView = itemView.findViewById(R.id.tvTaskCategory)
        private val tvPriority: TextView = itemView.findViewById(R.id.tvTaskPriority)
        private val tvDueDate: TextView = itemView.findViewById(R.id.tvTaskDueDate)
        private val viewPriorityIndicator: View = itemView.findViewById(R.id.viewPriorityIndicator)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDeleteTask)

        fun bind(task: Task) {
            tvTitle.text = task.title

            // Category Emoji & Styling
            val (categoryText, categoryColor) = when (task.category.lowercase(Locale.ROOT)) {
                "personal" -> "👤 Personal" to Color.parseColor("#2196F3")
                "work" -> "💼 Work" to Color.parseColor("#E91E63")
                "study" -> "📚 Study" to Color.parseColor("#9C27B0")
                "shopping" -> "🛒 Shopping" to Color.parseColor("#FF9800")
                "health" -> "❤️ Health" to Color.parseColor("#4CAF50")
                "finance" -> "💰 Finance" to Color.parseColor("#009688")
                "travel" -> "✈️ Travel" to Color.parseColor("#3F51B5")
                else -> task.category to Color.parseColor("#6200EE")
            }
            tvCategory.text = categoryText
            tvCategory.setTextColor(categoryColor)

            // Priority Emoji & Styling
            val (priorityText, priorityColor) = when (task.priority.uppercase(Locale.ROOT)) {
                "LOW" -> "🔹 Low" to Color.parseColor("#4CAF50")
                "MEDIUM" -> "⭐ Medium" to Color.parseColor("#FF9800")
                "HIGH" -> "🔥 High" to Color.parseColor("#F44336")
                "CRITICAL" -> "⚡ Critical" to Color.parseColor("#9C27B0")
                else -> task.priority to Color.parseColor("#888888")
            }
            tvPriority.text = priorityText
            tvPriority.setTextColor(priorityColor)
            viewPriorityIndicator.setBackgroundColor(priorityColor)

            if (task.dueDate > 0) {
                val sdf = SimpleDateFormat("MMM dd", Locale.getDefault())
                val dateStr = sdf.format(Date(task.dueDate))
                tvDueDate.text = if (task.dueTime.isNotEmpty()) "📅 $dateStr, ${task.dueTime}" else "📅 $dateStr"
                tvDueDate.visibility = View.VISIBLE
            } else {
                tvDueDate.visibility = View.GONE
            }

            checkBox.setOnCheckedChangeListener(null)
            checkBox.isChecked = task.isCompleted

            if (task.isCompleted) {
                tvTitle.paintFlags = tvTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                tvTitle.paintFlags = tvTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            checkBox.setOnClickListener {
                onCheckChanged(task)
            }

            btnDelete.setOnClickListener {
                onDeleteClick(task)
            }

            itemView.setOnClickListener {
                onItemClick(task)
            }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean = oldItem == newItem
    }
}
