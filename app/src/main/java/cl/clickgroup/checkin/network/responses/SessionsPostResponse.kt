package cl.clickgroup.checkin.network.responses

data class ErrorDetail(
    val type: String,
    val value: String,
    val msg: String,
    val path: String,
    val location: String
)

data class ErrorData(
    val errors: List<ErrorDetail>
)

data class SessionsPostResponse(
    val result: Boolean,
    val data: Any
)
