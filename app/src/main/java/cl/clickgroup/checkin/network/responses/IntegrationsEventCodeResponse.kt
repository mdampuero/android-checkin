package cl.clickgroup.checkin.network.responses
data class Integration(
    val session_id: String,
    val integration_id: String,
    val event_id: String,
    val event_name: String,
    val extraOption: Boolean,
    val request: Boolean,
    val request_options: Array<String>,
    val request_field: String,
    val request_input_type: String,
    val request_label: String
)
data class IntegrationsEventCodeResponse(
    val result: Boolean,
    val data: Integration
)
