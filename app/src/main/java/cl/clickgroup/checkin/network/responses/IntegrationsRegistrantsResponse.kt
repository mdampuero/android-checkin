package cl.clickgroup.checkin.network.responses
data class Person(
    val id: Int,
    val first_name: String,
    val last_name: String,
    val email: String,
    val mobile_phone: String,
    val rut: String,
    val scanned: String?
)
data class IntegrationsRegistrantsResponse(
    val total: Int,
    val data: List<Person>
)
