package cl.clickgroup.checkin.network

import cl.clickgroup.checkin.network.requests.CheckInPostRequest
import cl.clickgroup.checkin.network.responses.IntegrationsEventCodeResponse
import cl.clickgroup.checkin.network.responses.IntegrationsRegistrantsResponse
import cl.clickgroup.checkin.network.responses.SessionsPostResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

private var integrationId = "";
interface ApiService {

    @POST("checkins")
    fun checkInsPost(@Body input: CheckInPostRequest): Call<SessionsPostResponse>

    @GET("integrations/{integrationID}/registrants/{sessionID}")
    fun getRegistrant(@Path("integrationID") integrationID: String, @Path("sessionID") sessionID: String): Call<IntegrationsRegistrantsResponse>

    @GET("integrations/getBySessionId/{sessionId}")
    fun checkEventCode(@Path("sessionId") sessionId: String): Call<IntegrationsEventCodeResponse>
}
