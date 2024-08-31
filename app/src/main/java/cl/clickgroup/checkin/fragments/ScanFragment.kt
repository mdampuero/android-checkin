package cl.clickgroup.checkin.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import cl.clickgroup.checkin.R
import cl.clickgroup.checkin.activities.ScanActivity
import cl.clickgroup.checkin.utils.CheckInUtils
import cl.clickgroup.checkin.utils.RutValidatorUtils
import cl.clickgroup.checkin.utils.ToastUtils

class ScanFragment : Fragment() {

    private val SCAN_REQUEST_CODE = 101

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_scan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val etResultScan = view.findViewById<EditText>(R.id.ET_resultScan)
        val etIdentification = view.findViewById<EditText>(R.id.ET_identification)
        val btSearch = view.findViewById<Button>(R.id.BT_search)
        val btQrScan = view.findViewById<ImageButton>(R.id.BT_qrScan)
        etResultScan.setupEnterKeyListener(requireContext())
        etIdentification.setupEnterKeyListener(requireContext())

        btSearch.setOnClickListener {
            handleEnterAction(requireContext(), etIdentification)
        }

        btQrScan.setOnClickListener {
            val intent = Intent(requireActivity(), ScanActivity::class.java)
            startActivityForResult(intent, SCAN_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SCAN_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val scanResult = data?.getStringExtra("SCAN_RESULT")
            if (scanResult != null) {
                handleScanResult(scanResult)
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            showToast("Escaneo cancelado")
        }
    }

    private fun handleScanResult(scanResult: String) {
        when {
            isNumeric(scanResult) -> {
                // TODO hacer el metodo para registrar por ID
            }

            isUrl(scanResult) -> {
                var rut: String? = null
                rut = RutValidatorUtils.extractRut(scanResult)
                if (rut.isNullOrBlank()) {
                    ToastUtils.showCenteredToast(
                        requireContext(),
                        requireContext().getString(R.string.STRING_INVALID)
                    )
                    return
                }
                checkInByRut(rut)
            }

            else -> {
                //etResultScan?.setText("No es ni un número ni una URL: $scanResult")
            }
        }
        val etResultScan = view?.findViewById<EditText>(R.id.ET_resultScan)
        etResultScan?.setText(scanResult)
    }

    private fun isNumeric(str: String): Boolean {
        return str.toDoubleOrNull() != null
    }

    private fun isUrl(str: String): Boolean {
        return Patterns.WEB_URL.matcher(str).matches()
    }

    private fun showToast(message: String) {
        ToastUtils.showCenteredToast(requireContext(), message)
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

    fun checkInByRut(rut: String) {
        if (!RutValidatorUtils.isValidRut(rut)) {
            ToastUtils.showCenteredToast(requireContext(), requireContext().getString(R.string.RUT_INVALID))
            return
        }
        CheckInUtils.checkInByRut(requireContext(), rut)

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

        checkInByRut(rut.toString())

        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
    }
}
