package com.example.codvedx_t_1.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.codvedx_t_1.R
import com.example.codvedx_t_1.ui.TaskViewModel
import com.example.codvedx_t_1.ui.adapter.AchievementAdapter

class StatisticsFragment : Fragment() {

    private val viewModel: TaskViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvStatTotal: TextView = view.findViewById(R.id.tvStatTotal)
        val tvStatCompleted: TextView = view.findViewById(R.id.tvStatCompleted)
        val tvStatRate: TextView = view.findViewById(R.id.tvStatRate)
        val tvStatStreak: TextView = view.findViewById(R.id.tvStatStreak)
        val rvAchievements: RecyclerView = view.findViewById(R.id.rvAchievements)

        rvAchievements.layoutManager = LinearLayoutManager(requireContext())

        val streak = viewModel.prefs.currentStreak
        tvStatStreak.text = "$streak Days"

        viewModel.allActiveTasks.observe(viewLifecycleOwner) { active ->
            val totalActive = active.size
            tvStatTotal.text = "$totalActive"

            viewModel.completedTasks.observe(viewLifecycleOwner) { completed ->
                val totalCompleted = completed.size
                tvStatCompleted.text = "$totalCompleted"

                val grandTotal = totalActive + totalCompleted
                val rate = if (grandTotal > 0) ((totalCompleted.toFloat() / grandTotal) * 100).toInt() else 0
                tvStatRate.text = "$rate%"

                val achievements = viewModel.getAchievements()
                rvAchievements.adapter = AchievementAdapter(achievements)
            }
        }
    }
}
