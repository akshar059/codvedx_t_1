package com.example.codvedx_t_1.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.codvedx_t_1.R
import com.example.codvedx_t_1.data.model.Subtask
import com.example.codvedx_t_1.data.model.Task
import com.example.codvedx_t_1.ui.TaskViewModel
import com.example.codvedx_t_1.ui.adapter.SubtaskAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskDetailDialogFragment : BottomSheetDialogFragment() {

    private val viewModel: TaskViewModel by activityViewModels()
    private var taskId: Long = -1L
    private var currentTask: Task? = null

    companion object {
        fun newInstance(taskId: Long): TaskDetailDialogFragment {
            val fragment = TaskDetailDialogFragment()
            val args = Bundle().apply { putLong("ARG_TASK_ID", taskId) }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { taskId = it.getLong("ARG_TASK_ID", -1L) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_task_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvTitle: TextView = view.findViewById(R.id.tvDetailTitle)
        val cbComplete: CheckBox = view.findViewById(R.id.cbDetailComplete)
        val tvCatPri: TextView = view.findViewById(R.id.tvDetailCategoryPriority)
        val tvDate: TextView = view.findViewById(R.id.tvDetailDate)
        val tvDesc: TextView = view.findViewById(R.id.tvDetailDesc)
        val tvTags: TextView = view.findViewById(R.id.tvDetailTags)
        val rvSubtasks: RecyclerView = view.findViewById(R.id.rvDetailSubtasks)
        val btnEdit: MaterialButton = view.findViewById(R.id.btnDetailEdit)
        val btnDelete: MaterialButton = view.findViewById(R.id.btnDetailDelete)

        lifecycleScope.launch {
            val task = viewModel.allActiveTasks.value?.find { it.id == taskId }
                ?: viewModel.completedTasks.value?.find { it.id == taskId }

            if (task == null) {
                dismiss()
                return@launch
            }
            currentTask = task

            tvTitle.text = task.title
            cbComplete.isChecked = task.isCompleted
            tvCatPri.text = "Category: ${task.category} | Priority: ${task.priority}"

            if (task.dueDate > 0) {
                val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                tvDate.text = "Due: ${sdf.format(Date(task.dueDate))}, ${task.dueTime}"
            } else {
                tvDate.text = "No Due Date"
            }

            tvDesc.text = task.description.ifEmpty { "No description provided." }
            tvTags.text = if (task.tags.isNotEmpty()) "Tags: ${task.tags}" else ""

            cbComplete.setOnClickListener {
                viewModel.toggleTaskComplete(task)
            }

            // Subtasks
            val subtasks = try {
                val type = object : TypeToken<List<Subtask>>() {}.type
                Gson().fromJson<List<Subtask>>(task.subtasksJson, type).toMutableList()
            } catch (e: Exception) {
                mutableListOf<Subtask>()
            }

            val adapter = SubtaskAdapter(subtasks) {
                task.subtasksJson = Gson().toJson(subtasks)
                viewModel.updateTask(task)
            }
            rvSubtasks.layoutManager = LinearLayoutManager(requireContext())
            rvSubtasks.adapter = adapter

            btnEdit.setOnClickListener {
                dismiss()
                val editDialog = AddEditTaskDialogFragment.newInstance(task.id)
                editDialog.show(parentFragmentManager, "AddEditTaskDialog")
            }

            btnDelete.setOnClickListener {
                viewModel.moveToTrash(task)
                dismiss()
            }
        }
    }
}
