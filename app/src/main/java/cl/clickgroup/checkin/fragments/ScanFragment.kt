package cl.clickgroup.checkin.fragments

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import cl.clickgroup.checkin.R
import cl.clickgroup.checkin.data.repositories.PersonRepository
import cl.clickgroup.checkin.utils.RutValidatorUtils
import cl.clickgroup.checkin.utils.ToastUtils
import cl.clickgroup.checkin.utils.CheckInUtils


class ScanFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val etResultScan = view.findViewById<EditText>(R.id.ET_resultScan)
        val etIdentification = view.findViewById<EditText>(R.id.ET_identification)
        val btSearch = view.findViewById<Button>(R.id.BT_search)
        etResultScan.setupEnterKeyListener(requireContext())
        etIdentification.setupEnterKeyListener(requireContext())

        btSearch.setOnClickListener {
            handleEnterAction(requireContext(), etIdentification)
        }

    }

    fun EditText.setupEnterKeyListener(context: Context) {
        this.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                handleEnterAction(context, this)
                true
            } else {
                false
            }
        }
    }

    fun handleEnterAction(context: Context, editText: EditText) {
        var rut: String? = null
        val inputText = editText.text.toString()

        when (editText.id) {
            R.id.ET_resultScan -> {
                rut = RutValidatorUtils.extractRut(inputText)
                if (rut.isNullOrBlank()) {
                    ToastUtils.showCenteredToast(
                        context,
                        context.getString(R.string.STRING_INVALID)
                    )
                    return
                }
            }

            R.id.ET_identification -> {
                rut = inputText
            }
        }

        if (!RutValidatorUtils.isValidRut(rut.toString())) {
            ToastUtils.showCenteredToast(context, context.getString(R.string.RUT_INVALID))
            return
        }
        /**
         * CheckIn By RUT
         */
        CheckInUtils.checkInByRut(requireContext(), rut.toString())

        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
    }
}
