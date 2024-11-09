package cl.clickgroup.checkin.utils

import cl.clickgroup.checkin.R

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import cl.clickgroup.checkin.data.repositories.PersonDB
import cl.clickgroup.checkin.data.repositories.PersonRepository
import cl.clickgroup.checkin.network.RetrofitClient.apiService
import cl.clickgroup.checkin.network.requests.ResponseRequest
import cl.clickgroup.checkin.network.responses.Person
import retrofit2.Call
import retrofit2.Response

object DialogUtils {

    fun showCustomDialog(context: Context, type: String, message: String, person: PersonDB? = null) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_custom)

        val tvMessage = dialog.findViewById<TextView>(R.id.tvMessage)
        val btnOK = dialog.findViewById<Button>(R.id.BT_accept)
        val ivSuccess = dialog.findViewById<ImageView>(R.id.IV_success)
        val ivWarning = dialog.findViewById<ImageView>(R.id.IV_warning)
        val ivError = dialog.findViewById<ImageView>(R.id.IV_error)

        when (type) {
            "success" -> {
                ivSuccess.visibility = View.VISIBLE
                ivWarning.visibility = View.GONE
                ivError.visibility = View.GONE
            }
            "warning" -> {
                ivSuccess.visibility = View.GONE
                ivWarning.visibility = View.VISIBLE
                ivError.visibility = View.GONE
            }
            "error" -> {
                ivSuccess.visibility = View.GONE
                ivWarning.visibility = View.GONE
                ivError.visibility = View.VISIBLE
            }
            else -> {
                ivSuccess.visibility = View.GONE
                ivWarning.visibility = View.GONE
                ivError.visibility = View.GONE
            }
        }
        tvMessage.text = message

        btnOK.setOnClickListener {
            dialog.dismiss()
            if(type == "success"){
                showRequestDialog(context, person)
            }
        }

        dialog.show()
    }

    fun showRequestDialog(context: Context, person: PersonDB?=null) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_request)
        val request = SharedPreferencesUtils.getDataBoolean(context, "request")
        val requestLabel = SharedPreferencesUtils.getData(context, "request_label")
        val requestField = SharedPreferencesUtils.getData(context, "request_field")
        val requestInputType = SharedPreferencesUtils.getData(context, "request_input_type")
        val requestOptions = SharedPreferencesUtils.getData(context, "request_options")
        val optionsArray = requestOptions?.split(",")?.toTypedArray() ?: emptyArray()

        if(request && person != null && person.external_id > 0){


            val tvMessage = dialog.findViewById<TextView>(R.id.tvMessage)
            val etRequestInput = dialog.findViewById<TextView>(R.id.ET_requestInput)
            val btnOK = dialog.findViewById<Button>(R.id.BT_accept)
            val spinner = dialog.findViewById<Spinner>(R.id.SP_options)
            var requestResponse: String
            if(optionsArray.isNotEmpty()){
                val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, optionsArray)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
            }

            if(requestInputType == "STRING"){
               etRequestInput.visibility = View.VISIBLE
            }
            if(requestInputType == "OPTIONS"){
                spinner.visibility = View.VISIBLE
            }
            tvMessage.text = requestLabel

            btnOK.setOnClickListener {
               if(requestInputType == "OPTIONS") {
                    requestResponse = spinner.selectedItem as String
                }else{
                    requestResponse = etRequestInput.text.toString()
                }

                val call: Call<Void> = apiService.sendRequest(ResponseRequest(requestField, person.external_id, requestResponse))
                call.enqueue(object : retrofit2.Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        Log.d("DialogUtils", response.toString())

                    }
                    override fun onFailure(call: Call<Void>, t: Throwable) { }
                })

                dialog.dismiss()
            }

            dialog.show()
        }
    }
}
