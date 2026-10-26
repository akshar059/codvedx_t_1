package com.example.codvedx_t_1.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.codvedx_t_1.R
import com.example.codvedx_t_1.ui.TaskViewModel
import com.example.codvedx_t_1.ui.adapter.TaskAdapter
import com.example.codvedx_t_1.ui.dialog.AddEditTaskDialogFragment
import com.example.codvedx_t_1.ui.dialog.TaskDetailDialogFragment
import com.example.codvedx_t_1.ui.dialog.VoiceTaskDialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Calendar

class DashboardFragment : Fragment() {

    private val viewModel: TaskViewModel by activityViewModels()
    private lateinit var adapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvGreeting: TextView = view.findViewById(R.id.tvGreeting)
        val tvUserLevel: TextView = view.findViewById(R.id.tvUserLevel)
        val tvStreak: TextView = view.findViewById(R.id.tvStreak)
        val pbTodayProgress: ProgressBar = view.findViewById(R.id.pbTodayProgress)
        val tvProgressPercent: TextView = view.findViewById(R.id.tvProgressPercent)
        val tvTodaySummary: TextView = view.findViewById(R.id.tvTodaySummary)
        val rvTodayTasks: RecyclerView = view.findViewById(R.id.rvTodayTasks)
        val tvEmptyToday: TextView = view.findViewById(R.id.tvEmptyToday)
        val fabAddTask: FloatingActionButton = view.findViewById(R.id.fabAddTask)
        val btnVoiceTask: ImageButton = view.findViewById(R.id.btnVoiceTask)

        // Set greeting
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        tvGreeting.text = when (hour) {
            in 4..11 -> "Good Morning 👋"
            in 12..16 -> "Good Afternoon 👋"
            else -> "Good Evening 👋"
        }

        val level = viewModel.prefs.getUserLevel()
        val xp = viewModel.prefs.userXp
        val streak = viewModel.prefs.currentStreak
        tvUserLevel.text = "Level $level Explorer • $xp XP"
        tvStreak.text = "🔥 $streak Day Streak"

        // Setup RecyclerView
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

        rvTodayTasks.layoutManager = LinearLayoutManager(requireContext())
        rvTodayTasks.adapter = adapter

        viewModel.todayTasks.observe(viewLifecycleOwner) { tasks ->
            adapter.submitList(tasks)
            val total = tasks.size
            val completed = tasks.count { it.isCompleted }
            val pending = total - completed

            if (total == 0) {
                tvEmptyToday.visibility = View.VISIBLE
                rvTodayTasks.visibility = View.GONE
                pbTodayProgress.progress = 0
                tvProgressPercent.text = "0%"
                tvTodaySummary.text = "0 Completed • 0 Pending"
            } else {
                tvEmptyToday.visibility = View.GONE
                rvTodayTasks.visibility = View.VISIBLE
                val percent = ((completed.toFloat() / total) * 100).toInt()
                pbTodayProgress.progress = percent
                tvProgressPercent.text = "$percent%"
                tvTodaySummary.text = "$completed Completed • $pending Pending"
            }
        }

        fabAddTask.setOnClickListener {
            val dialog = AddEditTaskDialogFragment.newInstance()
            dialog.show(parentFragmentManager, "AddEditTaskDialog")
        }

        btnVoiceTask.setOnClickListener {
            val dialog = VoiceTaskDialogFragment()
            dialog.show(parentFragmentManager, "VoiceTaskDialog")
        }
    }
}
