package cl.clickgroup.checkin.fragments


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
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
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import cl.clickgroup.checkin.R
import cl.clickgroup.checkin.utils.CheckInUtils
import cl.clickgroup.checkin.utils.RutValidatorUtils
import cl.clickgroup.checkin.utils.SharedPreferencesUtils
import cl.clickgroup.checkin.utils.ToastUtils
import java.util.HashMap

class ScanFragment : Fragment() {

    private val SCAN_REQUEST_CODE = 101
    private lateinit var etSearchByID: EditText
    private lateinit var etSearchByRut: EditText
    private lateinit var etSearchByURL: EditText
    private lateinit var llSearchByID: LinearLayout

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

        // Listener para manejar la tecla Enter/Done
        etSearchByURL.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastEditorActionTime > 500) {
                    hideKeyboard(requireContext(), etSearchByURL)
                    lastEditorActionTime = currentTime
                    checkInByURl(etSearchByURL.text.toString())
                    etSearchByURL.text = null
                }
                true
            } else {
                false
            }
        }

        // Listener para manejar la tecla Tab
        etSearchByURL.setOnKeyListener { view, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_TAB) {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastEditorActionTime > 500) {
                    hideKeyboard(requireContext(), etSearchByURL)
                    lastEditorActionTime = currentTime
                    checkInByURl(etSearchByURL.text.toString())
                    etSearchByURL.text = null
                    view.clearFocus()
                    view.requestFocus()
                    true
                } else {
                    false
                }
            } else {
                false
            }
        }

        etSearchByURL.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                /**
                 * Function detect string in url input
                 */
                /*val inputText = s.toString()
                if (!isProcessing && inputText.contains("type=")) {
                    isProcessing = true // Indica que se está procesando el texto
                    checkInByURl(inputText)
                    etSearchByURL.text = null
                }*/
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })

        /**
         * Search By ID
         */
        val llSearchByID = view.findViewById<LinearLayout>(R.id.LL_searchByID)
        val integration_type = SharedPreferencesUtils.getData(requireContext(), "integration_type")
        if(integration_type == "REGISTER") {
            llSearchByID.visibility = View.GONE
        }
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
            val intent = Intent("com.summi.scan")
            // Check for modern Sunmi scanner app
            if (hasScanner(requireContext())) {
                intent.action = "com.sunmi.scanner.qrscanner"
            }
            intent.putExtra("PLAY_SOUND", true)
            intent.putExtra("IS_SHOW_SETTING", false)
            intent.putExtra("IS_SHOW_ALBUM", false)

            startActivityForResult(intent, SCAN_REQUEST_CODE)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SCAN_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val bundle = data?.extras
            if (bundle != null) {
                @Suppress("UNCHECKED_CAST")
                val result = bundle.getSerializable("data") as? ArrayList<HashMap<String, Any>>
                if (result != null && result.isNotEmpty()) {
                    val scanResult = result[0]["VALUE"] as? String
                    if (scanResult != null) {
                        handleScanResult(scanResult)
                    }
                }
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

    // --- Sunmi Scanner Helper Functions ---
    private fun getPackageInfo(context: Context, pkg: String): PackageInfo? {
        return try {
            context.packageManager.getPackageInfo(pkg, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    private fun hasScanner(ctx: Context): Boolean {
        val info = getPackageInfo(ctx, "com.sunmi.scanner")
        return info != null && compareVer(info.versionName, "4.4.4", true, 3)
    }

    private fun compareVer(nVer: String, oVer: String, isEq: Boolean, bit: Int): Boolean {
        if (nVer.isEmpty() || oVer.isEmpty()) return false
        val nArr = nVer.split("[.]".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val oArr = oVer.split("[.]".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (nArr.size < bit || oArr.size < bit) return false
        var vup = false
        for (i in 0 until bit) {
            val n = nArr[i].toInt()
            val o = oArr[i].toInt()
            if (n >= o) {
                if (n > o) {
                    vup = true
                    break
                } else if (isEq && i == bit - 1) {
                    vup = true
                    break
                }
            } else {
                break
            }
        }
        return vup
    }
}
