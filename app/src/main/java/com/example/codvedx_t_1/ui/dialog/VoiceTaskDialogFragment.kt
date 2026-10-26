package com.example.codvedx_t_1.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.codvedx_t_1.R
import com.example.codvedx_t_1.data.model.Task
import com.example.codvedx_t_1.ui.TaskViewModel
import com.example.codvedx_t_1.util.VoiceTaskParser
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class VoiceTaskDialogFragment : BottomSheetDialogFragment() {

    private val viewModel: TaskViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_voice_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etText: TextInputEditText = view.findViewById(R.id.etVoiceText)
        val btnCreate: MaterialButton = view.findViewById(R.id.btnCreateVoiceTask)
        val btnCancel: MaterialButton = view.findViewById(R.id.btnCancelVoice)

        btnCancel.setOnClickListener { dismiss() }

        btnCreate.setOnClickListener {
            val text = etText.text.toString().trim()
            if (text.isEmpty()) {
                etText.error = "Please enter text"
                return@setOnClickListener
            }

            val parsed = VoiceTaskParser.parseInput(text)
            val task = Task(
                title = parsed.title,
                dueDate = parsed.dueDate,
                dueTime = parsed.dueTime,
                priority = parsed.priority,
                reminderTime = parsed.dueDate
            )

            viewModel.insertTask(task)
            Toast.makeText(requireContext(), "Task Created: ${parsed.title}", Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }
}
