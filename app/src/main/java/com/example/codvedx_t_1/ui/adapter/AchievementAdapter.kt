package com.example.codvedx_t_1.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.codvedx_t_1.R
import com.example.codvedx_t_1.data.model.Achievement

class AchievementAdapter(private val achievements: List<Achievement>) :
    RecyclerView.Adapter<AchievementAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_achievement, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(achievements[position])
    }

    override fun getItemCount(): Int = achievements.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvAchTitle)
        private val tvDesc: TextView = itemView.findViewById(R.id.tvAchDesc)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvAchStatus)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.pbAch)

        fun bind(achievement: Achievement) {
            tvTitle.text = achievement.title
            tvDesc.text = achievement.description
            progressBar.max = achievement.maxProgress
            progressBar.progress = achievement.progress

            if (achievement.isUnlocked) {
                tvStatus.text = "🏆 Unlocked"
                tvStatus.setTextColor(android.graphics.Color.parseColor("#4CAF50"))
            } else {
                tvStatus.text = "${achievement.progress}/${achievement.maxProgress}"
                tvStatus.setTextColor(android.graphics.Color.parseColor("#757575"))
            }
        }
    }
}
