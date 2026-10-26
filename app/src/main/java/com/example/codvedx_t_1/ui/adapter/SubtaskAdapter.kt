package com.example.codvedx_t_1.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.codvedx_t_1.R
import com.example.codvedx_t_1.data.model.Subtask

class SubtaskAdapter(
    private val subtasks: MutableList<Subtask>,
    private val onSubtaskChanged: () -> Unit
) : RecyclerView.Adapter<SubtaskAdapter.SubtaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubtaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_subtask, parent, false)
        return SubtaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubtaskViewHolder, position: Int) {
        holder.bind(subtasks[position])
    }

    override fun getItemCount(): Int = subtasks.size

    inner class SubtaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkBox: CheckBox = itemView.findViewById(R.id.cbSubtask)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvSubtaskTitle)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDeleteSubtask)

        fun bind(subtask: Subtask) {
            tvTitle.text = subtask.title
            checkBox.setOnCheckedChangeListener(null)
            checkBox.isChecked = subtask.isCompleted

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                subtask.isCompleted = isChecked
                onSubtaskChanged()
            }

            btnDelete.setOnClickListener {
                val adapterPosition = bindingAdapterPosition
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    subtasks.removeAt(adapterPosition)
                    notifyItemRemoved(adapterPosition)
                    onSubtaskChanged()
                }
            }
        }
    }
}
