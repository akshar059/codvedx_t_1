package com.example.codvedx_t_1.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.codvedx_t_1.R
import com.example.codvedx_t_1.data.model.Task
import com.example.codvedx_t_1.ui.TaskViewModel
import com.example.codvedx_t_1.ui.adapter.TaskAdapter
import com.example.codvedx_t_1.ui.dialog.AddEditTaskDialogFragment
import com.example.codvedx_t_1.ui.dialog.TaskDetailDialogFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CalendarFragment : Fragment() {

    private val viewModel: TaskViewModel by activityViewModels()
    private lateinit var adapter: TaskAdapter
    private var selectedDateMillis: Long = System.currentTimeMillis()
    private var allActiveTasks: List<Task> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val calendarView: CalendarView = view.findViewById(R.id.calendarView)
        val tvSelectedDateTitle: TextView = view.findViewById(R.id.tvSelectedDateTitle)
        val btnAddCalendarTask: ImageButton = view.findViewById(R.id.btnAddCalendarTask)
        val rvCalendarTasks: RecyclerView = view.findViewById(R.id.rvCalendarTasks)
        val tvEmptyCalendarTasks: TextView = view.findViewById(R.id.tvEmptyCalendarTasks)

        adapter = TaskAdapter(
            onItemClick = { task ->
                val dialog = TaskDetailDialogFragment.newInstance(task.id)
                dialog.show(parentFragmentManager, "TaskDetailDialog")
            },
            onCheckChanged = { task ->
                viewModel.toggleTaskComplete(task)
            },
            onDeleteClick = { task ->
                viewModel.moveToTrash(task)
            }
        )

        rvCalendarTasks.layoutManager = LinearLayoutManager(requireContext())
        rvCalendarTasks.adapter = adapter

        updateTitle(tvSelectedDateTitle)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val cal = Calendar.getInstance().apply {
                set(year, month, dayOfMonth, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }
            selectedDateMillis = cal.timeInMillis
            updateTitle(tvSelectedDateTitle)
            filterTasksForSelectedDate(tvEmptyCalendarTasks, rvCalendarTasks)
        }

        viewModel.allActiveTasks.observe(viewLifecycleOwner) { tasks ->
            allActiveTasks = tasks
            filterTasksForSelectedDate(tvEmptyCalendarTasks, rvCalendarTasks)
        }

        btnAddCalendarTask.setOnClickListener {
            val dialog = AddEditTaskDialogFragment.newInstance(dueDateMillis = selectedDateMillis)
            dialog.show(parentFragmentManager, "AddEditTaskDialog")
        }
    }

    private fun updateTitle(tvTitle: TextView) {
        val sdf = SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault())
        tvTitle.text = "Tasks for ${sdf.format(Date(selectedDateMillis))}"
    }

    private fun filterTasksForSelectedDate(tvEmpty: TextView, rvTasks: RecyclerView) {
        val selCal = Calendar.getInstance().apply {
            timeInMillis = selectedDateMillis
        }

        val filtered = allActiveTasks.filter { task ->
            if (task.dueDate <= 0) return@filter false
            val taskCal = Calendar.getInstance().apply { timeInMillis = task.dueDate }
            taskCal.get(Calendar.YEAR) == selCal.get(Calendar.YEAR) &&
                    taskCal.get(Calendar.DAY_OF_YEAR) == selCal.get(Calendar.DAY_OF_YEAR)
        }

        adapter.submitList(filtered)

        if (filtered.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
            rvTasks.visibility = View.GONE
        } else {
            tvEmpty.visibility = View.GONE
            rvTasks.visibility = View.VISIBLE
        }
    }
}
