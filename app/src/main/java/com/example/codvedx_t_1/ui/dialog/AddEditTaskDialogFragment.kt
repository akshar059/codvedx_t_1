package com.example.codvedx_t_1.ui.dialog

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.codvedx_t_1.R
import com.example.codvedx_t_1.data.model.Subtask
import com.example.codvedx_t_1.data.model.Task
import com.example.codvedx_t_1.ui.TaskViewModel
import com.example.codvedx_t_1.ui.adapter.SubtaskAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class AddEditTaskDialogFragment : BottomSheetDialogFragment() {

    private val viewModel: TaskViewModel by activityViewModels()

    private var taskId: Long = -1L
    private var initialDueDate: Long = System.currentTimeMillis()
    private var existingTask: Task? = null

    private val categories = arrayOf("Personal", "Work", "Study", "Shopping", "Health", "Finance", "Travel")
    private val priorities = arrayOf("LOW", "MEDIUM", "HIGH", "CRITICAL")

    private val subtaskList = mutableListOf<Subtask>()
    private lateinit var subtaskAdapter: SubtaskAdapter

    private var selectedDueDateMillis: Long = System.currentTimeMillis()
    private var selectedDueTime: String = "12:00"

    companion object {
        fun newInstance(taskId: Long = -1L, dueDateMillis: Long = System.currentTimeMillis()): AddEditTaskDialogFragment {
            val fragment = AddEditTaskDialogFragment()
            val args = Bundle().apply {
                putLong("ARG_TASK_ID", taskId)
                putLong("ARG_DUE_DATE", dueDateMillis)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            taskId = it.getLong("ARG_TASK_ID", -1L)
            initialDueDate = it.getLong("ARG_DUE_DATE", System.currentTimeMillis())
            selectedDueDateMillis = initialDueDate
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_add_edit_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvDialogTitle: TextView = view.findViewById(R.id.tvDialogTitle)
        val etTitle: TextInputEditText = view.findViewById(R.id.etTaskTitle)
        val etDesc: TextInputEditText = view.findViewById(R.id.etTaskDesc)
        val spCategory: Spinner = view.findViewById(R.id.spCategory)
        val spPriority: Spinner = view.findViewById(R.id.spPriority)
        val btnPickDate: MaterialButton = view.findViewById(R.id.btnPickDate)
        val btnPickTime: MaterialButton = view.findViewById(R.id.btnPickTime)
        val etSubtaskInput: EditText = view.findViewById(R.id.etSubtaskInput)
        val btnAddSubtask: ImageButton = view.findViewById(R.id.btnAddSubtask)
        val rvSubtasks: RecyclerView = view.findViewById(R.id.rvSubtasks)
        val etTags: TextInputEditText = view.findViewById(R.id.etTags)
        val btnSave: MaterialButton = view.findViewById(R.id.btnSaveTask)
        val btnCancel: MaterialButton = view.findViewById(R.id.btnCancelTask)

        // Spinners setup
        spCategory.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories)
        spPriority.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, priorities)
        spPriority.setSelection(1) // MEDIUM default

        // Subtasks setup
        subtaskAdapter = SubtaskAdapter(subtaskList) {}
        rvSubtasks.layoutManager = LinearLayoutManager(requireContext())
        rvSubtasks.adapter = subtaskAdapter

        btnAddSubtask.setOnClickListener {
            val title = etSubtaskInput.text.toString().trim()
            if (title.isNotEmpty()) {
                subtaskList.add(Subtask(UUID.randomUUID().toString(), title, false))
                subtaskAdapter.notifyItemInserted(subtaskList.size - 1)
                etSubtaskInput.setText("")
            }
        }

        updateDateButtonText(btnPickDate)
        updateTimeButtonText(btnPickTime)

        // Prefill task data if editing an existing task
        if (taskId != -1L) {
            tvDialogTitle.text = "Edit Task ✏️"
            btnSave.text = "Update Task"

            lifecycleScope.launch {
                val task = viewModel.getTaskById(taskId)
                if (task != null) {
                    existingTask = task
                    etTitle.setText(task.title)
                    etDesc.setText(task.description)
                    etTags.setText(task.tags)

                    val catIndex = categories.indexOf(task.category)
                    if (catIndex >= 0) spCategory.setSelection(catIndex)

                    val priIndex = priorities.indexOf(task.priority)
                    if (priIndex >= 0) spPriority.setSelection(priIndex)

                    if (task.dueDate > 0) {
                        selectedDueDateMillis = task.dueDate
                        updateDateButtonText(btnPickDate)
                    }

                    if (task.dueTime.isNotEmpty()) {
                        selectedDueTime = task.dueTime
                        updateTimeButtonText(btnPickTime)
                    }

                    try {
                        val type = object : TypeToken<List<Subtask>>() {}.type
                        val parsedSubtasks: List<Subtask> = Gson().fromJson(task.subtasksJson, type) ?: emptyList()
                        subtaskList.clear()
                        subtaskList.addAll(parsedSubtasks)
                        subtaskAdapter.notifyDataSetChanged()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        btnPickDate.setOnClickListener {
            val cal = Calendar.getInstance().apply { timeInMillis = selectedDueDateMillis }
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val sel = Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                    }
                    selectedDueDateMillis = sel.timeInMillis
                    updateDateButtonText(btnPickDate)
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        btnPickTime.setOnClickListener {
            TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minute ->
                    selectedDueTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
                    updateTimeButtonText(btnPickTime)
                },
                12, 0, true
            ).show()
        }

        btnCancel.setOnClickListener { dismiss() }

        btnSave.setOnClickListener {
            val title = etTitle.text.toString().trim()
            if (title.isEmpty()) {
                etTitle.error = "Title required"
                return@setOnClickListener
            }

            val desc = etDesc.text.toString().trim()
            val category = spCategory.selectedItem.toString()
            val priority = spPriority.selectedItem.toString()
            val tags = etTags.text.toString().trim()
            val subtasksJson = Gson().toJson(subtaskList)

            val taskToSave = existingTask?.copy(
                title = title,
                description = desc,
                dueDate = selectedDueDateMillis,
                dueTime = selectedDueTime,
                category = category,
                priority = priority,
                tags = tags,
                subtasksJson = subtasksJson,
                reminderTime = selectedDueDateMillis,
                updatedAt = System.currentTimeMillis()
            ) ?: Task(
                id = if (taskId != -1L) taskId else 0L,
                title = title,
                description = desc,
                dueDate = selectedDueDateMillis,
                dueTime = selectedDueTime,
                category = category,
                priority = priority,
                tags = tags,
                subtasksJson = subtasksJson,
                reminderTime = selectedDueDateMillis
            )

            if (taskId != -1L) {
                viewModel.updateTask(taskToSave)
                Toast.makeText(requireContext(), "Task updated successfully", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.insertTask(taskToSave)
                Toast.makeText(requireContext(), "Task created successfully", Toast.LENGTH_SHORT).show()
            }

            dismiss()
        }
    }

    private fun updateDateButtonText(btn: MaterialButton) {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        btn.text = sdf.format(Date(selectedDueDateMillis))
    }

    private fun updateTimeButtonText(btn: MaterialButton) {
        btn.text = selectedDueTime
    }
}
