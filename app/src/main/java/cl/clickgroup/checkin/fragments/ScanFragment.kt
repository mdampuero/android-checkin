package cl.clickgroup.checkin.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
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
    private lateinit var etSearchByID: EditText
    private lateinit var etSearchByRut: EditText
    private lateinit var etSearchByURL: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_scan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var lastEditorActionTime: Long = 0

        /**
         * Search By RUT
         */
        val btSearchByRut = view.findViewById<Button>(R.id.BT_searchByRut)
        etSearchByRut = view.findViewById<EditText>(R.id.ET_searchByRut)

        etSearchByRut.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastEditorActionTime > 500) {
                    hideKeyboard(requireContext(), etSearchByRut)
                    lastEditorActionTime = currentTime
                    checkInByRut(etSearchByRut.text.toString())
                }
                true
            } else {
                false
            }
        }
        btSearchByRut.setOnClickListener {
            checkInByRut(etSearchByRut.text.toString())
        }

        /**
         * Search By URL
         */
        etSearchByURL = view.findViewById<EditText>(R.id.ET_searchByURL)
        etSearchByURL.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastEditorActionTime > 500) {
                    hideKeyboard(requireContext(), etSearchByURL)
                    lastEditorActionTime = currentTime
                    checkInByURl(etSearchByURL.text.toString())
                    etSearchByURL.text=null
                }
                true
            } else {
                false
            }
        }

        etSearchByURL.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val inputText = s.toString()
                if (inputText.contains("type=")) {
                    checkInByURl(inputText)
                    etSearchByURL.text = null
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })

        /**
         * Search By ID
         */
        val btSearchByID = view.findViewById<Button>(R.id.BT_searchByID)
        etSearchByID = view.findViewById<EditText>(R.id.ET_searchByID)
        etSearchByID.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastEditorActionTime > 500) {
                    hideKeyboard(requireContext(), etSearchByID)
                    lastEditorActionTime = currentTime
                    checkInByID(etSearchByID.text.toString())
                }
                true
            } else {
                false
            }
        }
        btSearchByID.setOnClickListener {
            checkInByID(etSearchByID.text.toString())
        }

        /**
         * SCAN
         */
        val btQrScan = view.findViewById<ImageButton>(R.id.BT_qrScan)
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
            ToastUtils.showCenteredToast(
                requireContext(),
                requireContext().getString(R.string.SCAN_CANCELED)
            )
        }
    }

    private fun handleScanResult(scanResult: String) {
        when {
            isNumeric(scanResult) -> {
                checkInByID(scanResult)
            }
            isURL(scanResult) -> {
                etSearchByURL.setText(scanResult);
                checkInByURl(scanResult)
            }
            else -> {
                ToastUtils.showCenteredToast(
                    requireContext(),
                    requireContext().getString(R.string.QR_INVALID)
                )
            }
        }
    }

    private fun isNumeric(str: String): Boolean {
        return str.toDoubleOrNull() != null
    }

    private fun isURL(str: String): Boolean {
        return Patterns.WEB_URL.matcher(str).matches()
    }

    private fun checkInByRut(rut: String) {
        Log.d("ScanFragment", "RUT: ${rut}")
        if (!RutValidatorUtils.isValidRut(rut)) {
            ToastUtils.showCenteredToast(requireContext(), requireContext().getString(R.string.RUT_INVALID))
            return
        }
        etSearchByRut.text = null
        CheckInUtils.checkInByRut(requireContext(), rut)
    }

    private fun checkInByID(id: String) {
        Log.d("ScanFragment", "ID: ${id}")
        etSearchByID.text = null
        CheckInUtils.checkInByID(requireContext(), id.toInt())
    }

    private fun checkInByURl(url: String) {
        Log.d("ScanFragment", "url: ${url}")
        var rut: String? = null
        rut = RutValidatorUtils.extractRut(url)
        if (rut.isNullOrBlank()) {
            ToastUtils.showCenteredToast(
                requireContext(),
                requireContext().getString(R.string.STRING_INVALID)
            )
            return
        }
        checkInByRut(rut)
    }

    private fun hideKeyboard(context: Context, editText: EditText) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
    }
}
