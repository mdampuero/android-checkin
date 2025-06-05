package cl.clickgroup.checkin.network.requests

import android.icu.text.CaseMap.Title

data class RegistrantRequest(
    val first_name: String? = "",
    val last_name: String? = "",
    val email: String? = "",
    val company: String? = "",
    val rut: String? = "",
    val job_title: String? = "",
    val event_id: String? = "",
)
