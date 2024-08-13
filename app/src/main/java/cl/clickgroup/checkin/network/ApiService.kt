package cl.clickgroup.checkin.network

import cl.clickgroup.checkin.network.requests.CheckInPostRequest
import cl.clickgroup.checkin.network.responses.SessionsPostResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("checkins")
    fun checkInsPost(@Body input: CheckInPostRequest): Call<SessionsPostResponse>

}
