package com.example.codvedx_t_1.util

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("focustask_prefs", Context.MODE_PRIVATE)

    var userName: String
        get() = prefs.getString("user_name", "User") ?: "User"
        set(value) = prefs.edit().putString("user_name", value).apply()

    var userXp: Int
        get() = prefs.getInt("user_xp", 0)
        set(value) = prefs.edit().putInt("user_xp", value).apply()

    var currentStreak: Int
        get() = prefs.getInt("current_streak", 0)
        set(value) = prefs.edit().putInt("current_streak", value).apply()

    var lastCompletedDateMillis: Long
        get() = prefs.getLong("last_completed_date", 0L)
        set(value) = prefs.edit().putLong("last_completed_date", value).apply()

    var appPin: String
        get() = prefs.getString("app_pin", "") ?: ""
        set(value) = prefs.edit().putString("app_pin", value).apply()

    var isPinEnabled: Boolean
        get() = prefs.getBoolean("is_pin_enabled", false)
        set(value) = prefs.edit().putBoolean("is_pin_enabled", value).apply()

    var isDarkModeEnabled: Boolean
        get() = prefs.getBoolean("is_dark_mode", false)
        set(value) = prefs.edit().putBoolean("is_dark_mode", value).apply()

    fun addXp(amount: Int) {
        userXp += amount
    }

    fun getUserLevel(): Int {
        return (userXp / 100) + 1
    }
}
