package cl.clickgroup.checkin.network

import androidx.annotation.Nullable
import cl.clickgroup.checkin.network.requests.CheckInByRegistrantIDsRequest
import cl.clickgroup.checkin.network.requests.CheckInByRegistrantRequest
import cl.clickgroup.checkin.network.requests.CheckInByRutRequest
import cl.clickgroup.checkin.network.requests.RegistrantRequest
import cl.clickgroup.checkin.network.requests.ResponseRequest
import cl.clickgroup.checkin.network.responses.CheckInByRegistrantIDsResponse
import cl.clickgroup.checkin.network.responses.CheckInByRegistrantResponse
import cl.clickgroup.checkin.network.responses.IntegrationsEventCodeResponse
import cl.clickgroup.checkin.network.responses.IntegrationsRegistrantsResponse
import cl.clickgroup.checkin.network.responses.CheckInByRutResponse
import cl.clickgroup.checkin.network.responses.RegistrantResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    /**
     * GETs
     */

    @GET("integrations/getBySessionId/{sessionId}")
    fun checkEventCode(@Path("sessionId") sessionId: String): Call<IntegrationsEventCodeResponse>

    /**
     * POSTs
     */
    @POST("checkins/byRut")
    fun checkInByRut(@Body input: CheckInByRutRequest): Call<CheckInByRutResponse>

    @POST("integrations/request")
    fun sendRequest(@Body input: ResponseRequest): Call<Void>

    @POST("registrants")
    fun registrant(@Body input: RegistrantRequest): Call<RegistrantResponse>

    @POST("eventSwoogos/sessions")
    fun checkInByRegistrant(@Body input: CheckInByRegistrantRequest): Call<CheckInByRegistrantResponse>

    @POST("checkins/byRegistrantIDs")
    fun checkInByRegistrantIDs(@Body input: CheckInByRegistrantIDsRequest): Call<CheckInByRegistrantIDsResponse>

    @POST("integrations/{integrationID}/registrants/{sessionID}")
    fun getRegistrant(@Path("integrationID") integrationID: String, @Path("sessionID") sessionID: String, @Body input: CheckInByRegistrantIDsRequest): Call<IntegrationsRegistrantsResponse>

}
