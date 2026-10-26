package com.example.codvedx_t_1.util

import java.util.Calendar

data class ParsedVoiceTask(
    val title: String,
    val dueDate: Long,
    val dueTime: String,
    val priority: String
)

object VoiceTaskParser {

    fun parseInput(spokenText: String): ParsedVoiceTask {
        val text = spokenText.lowercase()
        val calendar = Calendar.getInstance()

        var priority = "MEDIUM"
        if (text.contains("urgent") || text.contains("critical")) {
            priority = "CRITICAL"
        } else if (text.contains("high priority") || text.contains("important")) {
            priority = "HIGH"
        } else if (text.contains("low priority")) {
            priority = "LOW"
        }

        var dueDate = System.currentTimeMillis()
        if (text.contains("tomorrow")) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            dueDate = calendar.timeInMillis
        } else if (text.contains("next week")) {
            calendar.add(Calendar.DAY_OF_YEAR, 7)
            dueDate = calendar.timeInMillis
        }

        var dueTime = "17:00"
        if (text.contains("morning")) {
            dueTime = "09:00"
        } else if (text.contains("evening") || text.contains("night")) {
            dueTime = "20:00"
        } else if (text.contains("afternoon")) {
            dueTime = "14:00"
        }

        // Clean title
        var title = spokenText
            .replace("remind me to", "", ignoreCase = true)
            .replace("remind me", "", ignoreCase = true)
            .replace("tomorrow", "", ignoreCase = true)
            .replace("today", "", ignoreCase = true)
            .replace("next week", "", ignoreCase = true)
            .replace("at morning", "", ignoreCase = true)
            .replace("at evening", "", ignoreCase = true)
            .replace("at night", "", ignoreCase = true)
            .replace("urgent", "", ignoreCase = true)
            .replace("important", "", ignoreCase = true)
            .trim()

        if (title.isEmpty()) {
            title = spokenText
        }

        return ParsedVoiceTask(title, dueDate, dueTime, priority)
    }
}
