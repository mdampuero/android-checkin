package cl.clickgroup.checkin.network.responses
data class Integration(
    val session_id: String,
    val integration_id: String,
    val event_id: String,
    val event_name: String
)
data class IntegrationsEventCodeResponse(
    val result: Boolean,
    val data: Integration
)
