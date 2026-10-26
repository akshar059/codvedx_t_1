package com.example.codvedx_t_1.ui.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.codvedx_t_1.R
import com.example.codvedx_t_1.ui.TaskViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsFragment : Fragment() {

    private val viewModel: TaskViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val switchDarkMode: SwitchMaterial = view.findViewById(R.id.switchDarkMode)
        val btnExportJson: MaterialButton = view.findViewById(R.id.btnExportJson)
        val btnImportJson: MaterialButton = view.findViewById(R.id.btnImportJson)
        val btnLoadDemoTasks: MaterialButton = view.findViewById(R.id.btnLoadDemoTasks)
        val btnEmptyTrash: MaterialButton = view.findViewById(R.id.btnEmptyTrash)
        val switchPinLock: SwitchMaterial = view.findViewById(R.id.switchPinLock)

        switchDarkMode.isChecked = viewModel.prefs.isDarkModeEnabled
        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            viewModel.prefs.isDarkModeEnabled = isChecked
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        switchPinLock.isChecked = viewModel.prefs.isPinEnabled
        switchPinLock.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked && viewModel.prefs.appPin.isEmpty()) {
                viewModel.prefs.appPin = "1234" // Default PIN
                Toast.makeText(requireContext(), "Default PIN set to 1234", Toast.LENGTH_SHORT).show()
            }
            viewModel.prefs.isPinEnabled = isChecked
        }

        btnExportJson.setOnClickListener {
            viewModel.exportData { json ->
                val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Tasks JSON Backup", json)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(requireContext(), "Backup JSON copied to clipboard!", Toast.LENGTH_LONG).show()
            }
        }

        btnImportJson.setOnClickListener {
            val etInput = EditText(requireContext()).apply {
                hint = "Paste JSON string here"
            }
            AlertDialog.Builder(requireContext())
                .setTitle("Import JSON")
                .setView(etInput)
                .setPositiveButton("Import") { _, _ ->
                    val text = etInput.text.toString().trim()
                    if (text.isNotEmpty()) {
                        viewModel.importData(text) { success ->
                            if (success) {
                                Toast.makeText(requireContext(), "Tasks imported successfully!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(requireContext(), "Failed to import JSON", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        btnLoadDemoTasks.setOnClickListener {
            viewModel.addDemoTasks {
                Toast.makeText(requireContext(), "Demo tasks added successfully!", Toast.LENGTH_SHORT).show()
            }
        }

        btnEmptyTrash.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Empty Trash")
                .setMessage("Are you sure you want to permanently delete all items in trash?")
                .setPositiveButton("Delete All") { _, _ ->
                    viewModel.emptyTrash()
                    Toast.makeText(requireContext(), "Trash emptied", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}
