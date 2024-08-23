package cl.clickgroup.checkin.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import cl.clickgroup.checkin.network.RetrofitClient.apiService
import cl.clickgroup.checkin.network.requests.CheckInByRegistrantRequest
import cl.clickgroup.checkin.network.requests.CheckInByRutRequest
import cl.clickgroup.checkin.network.responses.CheckInByRegistrantResponse
import cl.clickgroup.checkin.network.responses.CheckInByRutResponse
import cl.clickgroup.checkin.utils.DialogUtils
import cl.clickgroup.checkin.utils.RutValidatorUtils
import cl.clickgroup.checkin.utils.SharedPreferencesUtils
import cl.clickgroup.checkin.utils.ToastUtils
import retrofit2.Call
import retrofit2.Response


class ScanFragment : Fragment() {

    private lateinit var personRepository: PersonRepository
    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView
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

        checkInByRut(rut.toString())

        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)

    }

    fun checkInByRut(rut: String) {
        personRepository = PersonRepository(requireContext())
        val person = personRepository.getPersonByRut(rut)
        if (person != null) {
            Log.d("ScanFragment", "Persona encontrada: ${person.external_id}")
            if(person.scanned.isNullOrEmpty()){
                syncCheckIn(rut)
                DialogUtils.showCustomDialog(requireContext(), "success", this.getString(R.string.CHECKIN_SUCCESS))
            }else{
                DialogUtils.showCustomDialog(requireContext(), "error", this.getString(R.string.CHECKIN_EXIST))
            }
        } else {
            DialogUtils.showCustomDialog(requireContext(), "warning", this.getString(R.string.PERSON_NOT_FOUND))
            Log.d("ScanFragment", "Persona no encontrada con el RUT: $rut")
        }

    }

    private fun syncCheckIn(rut:String) {
        val sessionID = SharedPreferencesUtils.getData(requireContext(), "session_id")
        val eventID = SharedPreferencesUtils.getData(requireContext(), "event_id")
        val call: Call<CheckInByRutResponse> = apiService.checkInByRut(CheckInByRutRequest(eventID, sessionID, rut))
        call.enqueue(object : retrofit2.Callback<CheckInByRutResponse> {
            override fun onResponse(
                call: Call<CheckInByRutResponse>,
                response: Response<CheckInByRutResponse>
            ) {
                try {
                    if (response.isSuccessful) {
                        personRepository.updateScannedFieldByRut(rut, "SERVER")
                        Log.d("ScanFragment", "OK el checkin online SERVER")
                    } else {
                        personRepository.updateScannedFieldByRut(rut, "APP")
                        Log.d("ScanFragment", "FALLO el checkin online APP")
                    }
                } catch (e: Exception) {
                    personRepository.updateScannedFieldByRut(rut, "APP")
                    Log.d("ScanFragment", "FALLO el checkin online exception APP: ${e.message}")
                }
            }

            override fun onFailure(call: Call<CheckInByRutResponse>, t: Throwable) {
                personRepository.updateScannedFieldByRut(rut, "APP")
                Log.d("ScanFragment", "FALLO el checkin online APP: ${t.message}")
            }
        })
    }
}
