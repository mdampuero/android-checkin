package cl.clickgroup.checkin.network.requests

data class RegistrantRequest(
    val first_name: String? = "",
    val last_name: String? = "",
    val email: String? = "",
    val mobile_phone: String? = "",
    val rut: String? = "",
    val event_id: String? = ""
)
