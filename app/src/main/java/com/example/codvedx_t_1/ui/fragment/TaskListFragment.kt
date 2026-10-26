package com.example.codvedx_t_1.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TaskListFragment : Fragment() {

    private val viewModel: TaskViewModel by activityViewModels()
    private lateinit var adapter: TaskAdapter
    private var selectedCategory: String = "All"
    private var allTaskList: List<Task> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_task_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etSearch: EditText = view.findViewById(R.id.etSearchTasks)
        val chipGroupCategory: ChipGroup = view.findViewById(R.id.chipGroupCategory)
        val rvAllTasks: RecyclerView = view.findViewById(R.id.rvAllTasks)
        val tvEmptyList: TextView = view.findViewById(R.id.tvEmptyList)
        val fabAddTask: FloatingActionButton = view.findViewById(R.id.fabAddTask)

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

        rvAllTasks.layoutManager = LinearLayoutManager(requireContext())
        rvAllTasks.adapter = adapter

        viewModel.searchResults.observe(viewLifecycleOwner) { tasks ->
            allTaskList = tasks
            applyCategoryFilter(tvEmptyList, rvAllTasks)
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchQuery.value = s?.toString() ?: ""
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        chipGroupCategory.setOnCheckedStateChangeListener { _, checkedIds ->
            selectedCategory = if (checkedIds.contains(R.id.chipPersonal)) "Personal"
            else if (checkedIds.contains(R.id.chipWork)) "Work"
            else if (checkedIds.contains(R.id.chipStudy)) "Study"
            else if (checkedIds.contains(R.id.chipShopping)) "Shopping"
            else if (checkedIds.contains(R.id.chipHealth)) "Health"
            else "All"

            applyCategoryFilter(tvEmptyList, rvAllTasks)
        }

        fabAddTask.setOnClickListener {
            val dialog = AddEditTaskDialogFragment.newInstance()
            dialog.show(parentFragmentManager, "AddEditTaskDialog")
        }
    }

    private fun applyCategoryFilter(tvEmptyList: TextView, rvAllTasks: RecyclerView) {
        val filtered = if (selectedCategory == "All") {
            allTaskList
        } else {
            allTaskList.filter { it.category.equals(selectedCategory, ignoreCase = true) }
        }

        adapter.submitList(filtered)

        if (filtered.isEmpty()) {
            tvEmptyList.visibility = View.VISIBLE
            rvAllTasks.visibility = View.GONE
        } else {
            tvEmptyList.visibility = View.GONE
            rvAllTasks.visibility = View.VISIBLE
        }
    }
}
