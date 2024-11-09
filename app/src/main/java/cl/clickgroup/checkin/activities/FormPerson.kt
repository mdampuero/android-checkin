package cl.clickgroup.checkin.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import cl.clickgroup.checkin.R
import cl.clickgroup.checkin.data.repositories.PersonRepository
import cl.clickgroup.checkin.fragments.CheckInFragment
import cl.clickgroup.checkin.network.RetrofitClient
import cl.clickgroup.checkin.network.requests.RegistrantRequest
import cl.clickgroup.checkin.network.responses.ErrorResponse
import cl.clickgroup.checkin.network.responses.RegistrantResponse
import cl.clickgroup.checkin.utils.RutValidatorUtils
import cl.clickgroup.checkin.utils.SharedPreferencesUtils
import cl.clickgroup.checkin.utils.ToastUtils
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FormPerson : AppCompatActivity() {
    private lateinit var progressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_person)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btSave = findViewById<Button>(R.id.BT_save)
        val firstNameEditText = findViewById<EditText>(R.id.ET_firstName_value)
        val lastNameEditText = findViewById<EditText>(R.id.ET_lastName_value)
        val emailEditText = findViewById<EditText>(R.id.ET_email_value)
        val documentEditText = findViewById<EditText>(R.id.ET_document_value)
        val phoneEditText = findViewById<EditText>(R.id.ET_phone_value)
        progressBar = findViewById(R.id.progressBar)

        btnBack.setOnClickListener {
            finish()
        }

        btSave.setOnClickListener {
            val firstName = firstNameEditText.text.toString().trim()
            val lastName = lastNameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val document = documentEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()

            if (firstName.isEmpty()) {
                firstNameEditText.error = getString(R.string.ERROR_FIRST_NAME_EMPTY)
                return@setOnClickListener
            } else if (firstName.length < 2) {
                firstNameEditText.error = getString(R.string.ERROR_FIRST_NAME_SHORT)
                return@setOnClickListener
            }

            if (lastName.isEmpty()) {
                lastNameEditText.error = getString(R.string.ERROR_LAST_NAME_EMPTY)
                return@setOnClickListener
            } else if (lastName.length < 2) {
                lastNameEditText.error = getString(R.string.ERROR_LAST_NAME_SHORT)
                return@setOnClickListener
            }

            if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditText.error = getString(R.string.ERROR_EMAIL_INVALID)
                return@setOnClickListener
            }

            if (document.isEmpty()) {
                documentEditText.error = getString(R.string.ERROR_DOCUMENT_EMPTY)
                return@setOnClickListener
            }else if (!RutValidatorUtils.isValidRut(document)) {
                documentEditText.error = getString(R.string.RUT_INVALID)
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE
            val call = RetrofitClient.apiService.registrant(RegistrantRequest(firstName,lastName,email,phone, document, event_id = SharedPreferencesUtils.getData(applicationContext, "event_id") ))
            call.enqueue(object : Callback<RegistrantResponse> {
                override fun onResponse(
                    call: Call<RegistrantResponse>,
                    response: Response<RegistrantResponse>
                ) {
                    Log.d("FormPerson", "Responde OK")
                    progressBar.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    try {
                        if (!response.isSuccessful) {
                            val errorBody = response.errorBody()?.string()
                            val gson = Gson()
                            val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
                            ToastUtils.showCenteredToast(applicationContext, errorResponse.msg)
                        } else {
                            val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as? CheckInFragment
                            ToastUtils.showCenteredToast(applicationContext,getString(R.string.REGISTRANT_CREATE_OK))
                            fragment?.fetchData()
                            finish()
                        }
                    } catch (e: Exception) {
                        ToastUtils.showCenteredToast(applicationContext, getString(R.string.EVENT_CODE_INVALID))
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<RegistrantResponse>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    Log.d("FormPerson", "Fallo:"+t.message)
                    ToastUtils.showCenteredToast(applicationContext, getString(R.string.NEEDED_INTERNET))
                    t.printStackTrace()
                }
            })

        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            val needSync = data?.getBooleanExtra("needSync", false) ?: false

            // Aquí puedes pasar el valor a tu fragment o ejecutar alguna acción
            if (needSync) {

            }
        }
    }
}