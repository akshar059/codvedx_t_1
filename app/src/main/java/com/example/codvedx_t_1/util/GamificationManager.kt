package com.example.codvedx_t_1.util

import com.example.codvedx_t_1.data.model.Achievement

class GamificationManager(private val prefs: PreferencesManager) {

    fun onTaskCompleted(totalCompletedCount: Int): Int {
        val xpEarned = 15
        prefs.addXp(xpEarned)
        updateStreak()
        return xpEarned
    }

    private fun updateStreak() {
        val now = System.currentTimeMillis()
        val lastDate = prefs.lastCompletedDateMillis

        val oneDayMillis = 24 * 60 * 60 * 1000L
        val diff = now - lastDate

        if (diff < oneDayMillis) {
            // Already counted today or within 24h
            if (prefs.currentStreak == 0) {
                prefs.currentStreak = 1
            }
        } else if (diff in oneDayMillis..(2 * oneDayMillis)) {
            // Consecutive day
            prefs.currentStreak += 1
        } else {
            // Streak broken
            prefs.currentStreak = 1
        }

        prefs.lastCompletedDateMillis = now
    }

    fun getAchievements(completedTaskCount: Int): List<Achievement> {
        val streak = prefs.currentStreak
        return listOf(
            Achievement("1", "First Step", "Complete your 1st task", completedTaskCount >= 1, completedTaskCount.coerceAtMost(1), 1),
            Achievement("2", "Getting Things Done", "Complete 10 tasks", completedTaskCount >= 10, completedTaskCount.coerceAtMost(10), 10),
            Achievement("3", "Task Master", "Complete 50 tasks", completedTaskCount >= 50, completedTaskCount.coerceAtMost(50), 50),
            Achievement("4", "On Fire", "Maintain a 3-day streak", streak >= 3, streak.coerceAtMost(3), 3),
            Achievement("5", "Unstoppable", "Maintain a 7-day streak", streak >= 7, streak.coerceAtMost(7), 7)
        )
    }
}
