package cl.clickgroup.checkin.network

import androidx.annotation.Nullable
import cl.clickgroup.checkin.network.requests.CheckInByRegistrantIDsRequest
import cl.clickgroup.checkin.network.requests.CheckInByRegistrantRequest
import cl.clickgroup.checkin.network.requests.CheckInByRutRequest
import cl.clickgroup.checkin.network.requests.LoginRequest
import cl.clickgroup.checkin.network.requests.RegistrantRequest
import cl.clickgroup.checkin.network.requests.ResponseRequest
import cl.clickgroup.checkin.network.responses.CheckInByRegistrantIDsResponse
import cl.clickgroup.checkin.network.responses.CheckInByRegistrantResponse
import cl.clickgroup.checkin.network.responses.IntegrationsEventCodeResponse
import cl.clickgroup.checkin.network.responses.IntegrationsRegistrantsResponse
import cl.clickgroup.checkin.network.responses.CheckInByRutResponse
import cl.clickgroup.checkin.network.responses.RegistrantResponse
import cl.clickgroup.checkin.network.responses.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    /**
     * GETs
     */

    @GET("clickgroup/integrations/getBySessionId/{sessionId}")
    fun checkEventCode(
        @Header("Authorization") authorizationHeader: String,
        @Path("sessionId") sessionId: String
    ): Call<IntegrationsEventCodeResponse>

    /**
     * POSTs
     */

    @POST("auth/login")
    fun login(@Body input: LoginRequest): Call<LoginResponse>

    @POST("clickgroup/checkins/byRut")
    fun checkInByRut(
        @Header("Authorization") authorizationHeader: String,
        @Body input: CheckInByRutRequest
    ): Call<CheckInByRutResponse>

    @POST("clickgroup/integrations/request")
    fun sendRequest(
        @Header("Authorization") authorizationHeader: String,
        @Body input: ResponseRequest
    ): Call<Void>

    @POST("registrants")
    fun registrant(@Body input: RegistrantRequest): Call<RegistrantResponse>

    @POST("eventSwoogos/sessions")
    fun checkInByRegistrant(@Body input: CheckInByRegistrantRequest): Call<CheckInByRegistrantResponse>

    @POST("clickgroup/integrationsRegistrant/{integrationID}/{sessionID}")
    fun getRegistrant(
        @Header("Authorization") authorizationHeader: String,
        @Path("integrationID") integrationID: String,
        @Path("sessionID") sessionID: String,
        @Body input: CheckInByRegistrantIDsRequest
    ): Call<IntegrationsRegistrantsResponse>

}
