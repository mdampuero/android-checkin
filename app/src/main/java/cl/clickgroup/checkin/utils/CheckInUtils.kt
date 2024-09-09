package cl.clickgroup.checkin.utils

import android.content.Context
import cl.clickgroup.checkin.R.*
import cl.clickgroup.checkin.data.repositories.PersonRepository
import cl.clickgroup.checkin.network.RetrofitClient.apiService
import cl.clickgroup.checkin.network.requests.CheckInByRutRequest
import cl.clickgroup.checkin.network.responses.CheckInByRutResponse
import retrofit2.Call
import retrofit2.Response

object CheckInUtils {
    fun checkInByRut(context: Context, rut: String) {
        val personRepository = PersonRepository(context)
        val person = personRepository.getPersonByRut(rut)
        if (person != null) {
            if(person.scanned.isNullOrEmpty()){
                syncCheckIn(context, rut)
                DialogUtils.showCustomDialog(context, "success", context.getString(string.CHECKIN_SUCCESS))
            }else{
                DialogUtils.showCustomDialog(context, "error", context.getString(string.CHECKIN_EXIST))
            }
        } else {
            DialogUtils.showCustomDialog(context, "warning", context.getString(string.PERSON_NOT_FOUND))
        }
    }

    fun checkInByID(context: Context, id: Int) {
        val personRepository = PersonRepository(context)
        val person = personRepository.getPersonByExternalID(id)
        if (person != null) {
            if(person.scanned.isNullOrEmpty()){
                syncCheckIn(context, person.rut)
                DialogUtils.showCustomDialog(context, "success", context.getString(string.CHECKIN_SUCCESS))
            }else{
                DialogUtils.showCustomDialog(context, "error", context.getString(string.CHECKIN_EXIST))
            }
        } else {
            DialogUtils.showCustomDialog(context, "warning", context.getString(string.PERSON_NOT_FOUND_BY_ID))
        }
    }

    private fun syncCheckIn(context: Context, rut: String) {
        val personRepository = PersonRepository(context)
        val sessionID = SharedPreferencesUtils.getData(context, "session_id")
        val eventID = SharedPreferencesUtils.getData(context, "event_id")
        val call: Call<CheckInByRutResponse> = apiService.checkInByRut(CheckInByRutRequest(eventID, sessionID, rut))
        call.enqueue(object : retrofit2.Callback<CheckInByRutResponse> {
            override fun onResponse(
                call: Call<CheckInByRutResponse>,
                response: Response<CheckInByRutResponse>
            ) {
                try {
                    if (response.isSuccessful) {
                        personRepository.updateScannedFieldByRut(rut, "SERVER")
                    } else {
                        personRepository.updateScannedFieldByRut(rut, "APP")
                    }
                } catch (e: Exception) {
                    personRepository.updateScannedFieldByRut(rut, "APP")
                }
            }

            override fun onFailure(call: Call<CheckInByRutResponse>, t: Throwable) {
                personRepository.updateScannedFieldByRut(rut, "APP")
            }
        })
    }
}
