package cl.clickgroup.checkin.network.responses
data class Person(
    val id: Int,
    val first_name: String,
    val last_name: String,
    val email: String,
    val mobile_company: String,
    val rut: String,
    val scanned: String?,
    val company: String?,
    val job_title: String?
)
data class IntegrationsRegistrantsResponse(
    val total: Int,
    val data: List<Person>
)
