package cl.clickgroup.checkin.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import cl.clickgroup.checkin.R
import cl.clickgroup.checkin.activities.CodeEntryActivity
import cl.clickgroup.checkin.activities.MainActivity
import cl.clickgroup.checkin.data.repositories.PersonRepository
import cl.clickgroup.checkin.utils.SharedPreferencesUtils
import cl.clickgroup.checkin.utils.ToastUtils
import kotlin.math.log

class SettingFragment : Fragment() {
    private lateinit var personRepository: PersonRepository
    private lateinit var etEventCodeValue: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)
        val btClearDatabase = view.findViewById<Button>(R.id.BT_clearDatabase)
        val btChangeCode = view.findViewById<Button>(R.id.BT_changeCode)
        etEventCodeValue = view.findViewById<TextView>(R.id.TV_eventCodeValue)

        init()
        btClearDatabase.setOnClickListener {
            confirmationDialogClearDatabase()
        }
        btChangeCode.setOnClickListener {
            confirmationDialogClearCode()
        }
        return view
    }

    private fun init() {
        val savedCode = SharedPreferencesUtils.getData(requireContext(), "session_id")
        etEventCodeValue.text = savedCode
        personRepository = PersonRepository(requireContext())
    }

    private fun confirmationDialogClearCode() {
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.custom_alert_dialog, null)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)

        val customTitle = dialogView.findViewById<TextView>(R.id.customTitle)
        val customMessage = dialogView.findViewById<TextView>(R.id.customMessage)

        customTitle.text = getString(R.string.CONFIRMATION_TITLE)
        customMessage.text = getString(R.string.CONFIRMATION_MESSAGE_CLEAR_CODE)

        builder.setPositiveButton(getString(R.string.YES)) { _, _ ->
            clearCode()
        }
        builder.setNegativeButton(getString(R.string.NO)) { _, _ ->
        }

        val dialog = builder.create()
        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            positiveButton?.setTextColor(ContextCompat.getColor(requireContext(), R.color.pink))
            negativeButton?.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_strong))
        }
        dialog.show()
    }

    private fun clearCode() {
        SharedPreferencesUtils.saveSingleData(requireContext(), "session_id", "")
        ToastUtils.showCenteredToast(
            requireContext(),
            requireContext().getString(R.string.CLEAR_CODE_SUCCESS)
        )
        clearDatabase()
    }

    private fun confirmationDialogClearDatabase() {
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.custom_alert_dialog, null)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)

        val customTitle = dialogView.findViewById<TextView>(R.id.customTitle)
        val customMessage = dialogView.findViewById<TextView>(R.id.customMessage)

        customTitle.text = getString(R.string.CONFIRMATION_TITLE)
        customMessage.text = getString(R.string.CONFIRMATION_MESSAGE)

        builder.setPositiveButton(getString(R.string.YES)) { _, _ ->
            clearDatabase()
        }
        builder.setNegativeButton(getString(R.string.NO)) { _, _ ->
        }

        val dialog = builder.create()
        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            positiveButton?.setTextColor(ContextCompat.getColor(requireContext(), R.color.pink))
            negativeButton?.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_strong))
        }
        dialog.show()
    }

    private fun clearDatabase() {
        personRepository.truncatePersonsTable()
        ToastUtils.showCenteredToast(
            requireContext(),
            requireContext().getString(R.string.CLEAR_DATABASE_SUCCESS)
        )
        logout()
    }

    private fun logout(){
        val intent = Intent(requireContext(), CodeEntryActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}