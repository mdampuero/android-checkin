package cl.clickgroup.checkin.network.responses

import cl.clickgroup.checkin.fragments.PrintField

data class Integration(
    val session_id: String,
    val integration_id: String,
    val event_id: String,
    val event_name: String,
    val extraOption: Boolean,
    val request: Boolean,
    val print: Boolean,
    val request_options: Array<String>,
    val request_field: String,
    val request_input_type: String,
    val request_label: String,
    val integration_type: String,
    val print_fields: List<PrintField>
)
data class IntegrationsEventCodeResponse(
    val result: Boolean,
    val data: Integration
)
