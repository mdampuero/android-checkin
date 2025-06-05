package cl.clickgroup.checkin.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import cl.clickgroup.checkin.R
import cl.clickgroup.checkin.network.ApiService
import cl.clickgroup.checkin.network.RetrofitClient
import cl.clickgroup.checkin.network.responses.IntegrationsEventCodeResponse
import cl.clickgroup.checkin.network.responses.IntegrationsRegistrantsResponse
import cl.clickgroup.checkin.utils.SharedPreferencesUtils
import cl.clickgroup.checkin.utils.ToastUtils
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class CodeEntryActivity : AppCompatActivity() {
    private lateinit var progressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_code_entry)

        val editTextCode = findViewById<EditText>(R.id.editTextCode)
        val buttonSubmit = findViewById<Button>(R.id.BT_submit)
        progressBar = findViewById(R.id.progressBar)

        val savedCode = SharedPreferencesUtils.getData(this, "session_id")

        if (!savedCode.isNullOrEmpty()) {
            goToMainScreen(false)
        }

        buttonSubmit.setOnClickListener {
            val enteredCode = editTextCode.text.toString()
            if (enteredCode.length in 6..7) {
                checkEventCode(enteredCode)
            } else {
                ToastUtils.showCenteredToast(this, this.getString(R.string.EVENT_CODE_INVALID))
            }
        }
    }

    private fun checkEventCode(enteredCode:String) {
        Log.d("CodeEntryActivity", "Le pego al endpoint")
        progressBar.visibility = View.VISIBLE
        val call = RetrofitClient.apiService.checkEventCode(enteredCode)
        call.enqueue(object : Callback<IntegrationsEventCodeResponse> {
            override fun onResponse(
                call: Call<IntegrationsEventCodeResponse>,
                response: Response<IntegrationsEventCodeResponse>
            ) {
                Log.d("CodeEntryActivity", "Responde OK")
                progressBar.visibility = View.GONE
                try {
                    if (!response.isSuccessful) {
                        ToastUtils.showCenteredToast(applicationContext, getString(R.string.EVENT_CODE_INVALID))
                    } else {
                        val responseData = response.body()
                        if (responseData != null && responseData.result) {
                            saveResultEventCode(responseData);
                            goToMainScreen(true)
                        } else {
                            ToastUtils.showCenteredToast(applicationContext, getString(R.string.EVENT_CODE_INVALID))
                        }
                    }
                } catch (e: Exception) {
                    ToastUtils.showCenteredToast(applicationContext, getString(R.string.EVENT_CODE_INVALID))
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<IntegrationsEventCodeResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                Log.d("CodeEntryActivity", "Fallo:"+t.message)
                ToastUtils.showCenteredToast(applicationContext, getString(R.string.NEEDED_INTERNET))
                t.printStackTrace()
            }
        })
    }

    private fun saveResultEventCode(responseData: IntegrationsEventCodeResponse){

        val integration = responseData.data
        val printFieldsJson = Gson().toJson(integration.print_fields)
        val integrationId = integration.integration_id
        val eventId = integration.event_id
        val sessionId = integration.session_id
        val extraOption = integration.extraOption
        val eventName = integration.event_name
        val request = integration.request
        val print = integration.print
        val request_field = integration.request_field
        val request_input_type = integration.request_input_type
        val request_label = integration.request_label
        val request_options = integration.request_options
        val integration_type = integration.integration_type
        SharedPreferencesUtils.saveData(
            applicationContext,
            "session_id", sessionId,
            "integration_id", integrationId,
            "event_id", eventId,
            "event_name", eventName,
            "extraOption", extraOption,
            "request", request,
            "request_field", request_field,
            "request_input_type", request_input_type,
            "request_label", request_label,
            "request_options", request_options,
            "integration_type", integration_type,
            "print", print,
            "print_fields", printFieldsJson
        )
    }
    private fun goToMainScreen(needSync: Boolean) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("needSync", needSync)
        startActivity(intent)
        finish()
    }
}