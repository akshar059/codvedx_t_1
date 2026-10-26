package com.example.codvedx_t_1.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.codvedx_t_1.R
import com.example.codvedx_t_1.ui.TaskViewModel
import com.google.android.material.button.MaterialButton

class PinLockDialogFragment : DialogFragment() {

    private val viewModel: TaskViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_pin_lock, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etPin: EditText = view.findViewById(R.id.etPinInput)
        val btnUnlock: MaterialButton = view.findViewById(R.id.btnUnlock)

        btnUnlock.setOnClickListener {
            val inputPin = etPin.text.toString().trim()
            val correctPin = viewModel.prefs.appPin.ifEmpty { "1234" }

            if (inputPin == correctPin) {
                dismiss()
            } else {
                Toast.makeText(requireContext(), "Incorrect PIN!", Toast.LENGTH_SHORT).show()
                etPin.setText("")
            }
        }
    }
}
