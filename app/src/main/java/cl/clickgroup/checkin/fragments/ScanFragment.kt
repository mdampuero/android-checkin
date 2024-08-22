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
import androidx.fragment.app.Fragment
import cl.clickgroup.checkin.R
import cl.clickgroup.checkin.data.repositories.PersonRepository
import cl.clickgroup.checkin.data.repositories.PersonDB
import cl.clickgroup.checkin.utils.RutValidatorUtils
import cl.clickgroup.checkin.utils.ToastUtils

class ScanFragment : Fragment() {

    private lateinit var personRepository: PersonRepository

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


        /*  val persons = personRepository.getAllPersons()

          // Mostrar los resultados en el Logcat o usarlos en la UI
          for (per in persons) {
              Log.d("ScanFragment", "Persona: ${per.first_name} ${per.last_name}, ID: ${per.id}")
          }*/
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
        Log.d("ScanFragment", "RUT: ${rut}")
    }

    // Crear el objeto de la solicitud
    /* val request = InputRequest(inputText)

     // Hacer la llamada a la API
     RetrofitClient.apiService.sessionsPost(request).enqueue(object : Callback<Void> {
         override fun onResponse(call: Call<Void>, response: Response<Void>) {
             if (response.isSuccessful) {
                 Toast.makeText(context, "Data sent successfully", Toast.LENGTH_SHORT).show()
             } else {
                 Toast.makeText(context, "Failed to send data", Toast.LENGTH_SHORT).show()
             }
         }

         override fun onFailure(call: Call<Void>, t: Throwable) {
             Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
         }
     })*/
    /* fun handleEnterAction(context: Context, editText: EditText) {
         val inputText = editText.text.toString()
         Toast.makeText(context, "Enter pressed: $inputText", Toast.LENGTH_SHORT).show()

         val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
         imm.hideSoftInputFromWindow(editText.windowToken, 0)
     }*/
}
