package com.example.codvedx_t_1

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.codvedx_t_1.util.PreferencesManager

class FocusTaskApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val prefs = PreferencesManager(this)
        if (prefs.isDarkModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
