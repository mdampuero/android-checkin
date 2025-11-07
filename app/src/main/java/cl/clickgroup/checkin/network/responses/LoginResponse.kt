package cl.clickgroup.checkin.network.responses

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val token: String?,
    @SerializedName("refresh_token") val refreshToken: String?,
    val user: Any?
)
