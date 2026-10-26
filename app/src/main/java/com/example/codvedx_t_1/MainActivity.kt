package com.example.codvedx_t_1

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.example.codvedx_t_1.ui.TaskViewModel
import com.example.codvedx_t_1.ui.dialog.PinLockDialogFragment
import com.example.codvedx_t_1.ui.fragment.CalendarFragment
import com.example.codvedx_t_1.ui.fragment.DashboardFragment
import com.example.codvedx_t_1.ui.fragment.SettingsFragment
import com.example.codvedx_t_1.ui.fragment.StatisticsFragment
import com.example.codvedx_t_1.ui.fragment.TaskListFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private val viewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_main)

        val mainRoot: View = findViewById(R.id.mainRoot)

        ViewCompat.setOnApplyWindowInsetsListener(mainRoot) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                top = systemBars.top,
                left = systemBars.left,
                right = systemBars.right
            )
            insets
        }

        val bottomNav: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        if (savedInstanceState == null) {
            loadFragment(DashboardFragment())
        }

        bottomNav.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.nav_dashboard -> DashboardFragment()
                R.id.nav_tasks -> TaskListFragment()
                R.id.nav_calendar -> CalendarFragment()
                R.id.nav_stats -> StatisticsFragment()
                R.id.nav_settings -> SettingsFragment()
                else -> DashboardFragment()
            }
            loadFragment(fragment)
            true
        }

        // PIN Lock check
        if (viewModel.prefs.isPinEnabled) {
            val pinDialog = PinLockDialogFragment()
            pinDialog.show(supportFragmentManager, "PinLockDialog")
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
