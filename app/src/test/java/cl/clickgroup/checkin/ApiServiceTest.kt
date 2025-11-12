package cl.clickgroup.checkin

import cl.clickgroup.checkin.network.RetrofitClient
import cl.clickgroup.checkin.network.requests.CheckInByRegistrantRequest
import cl.clickgroup.checkin.network.requests.CheckInByRutRequest
import cl.clickgroup.checkin.network.responses.CheckInByRegistrantResponse
import cl.clickgroup.checkin.network.responses.CheckInByRutResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.junit.Test
import org.junit.Assert.*
import retrofit2.Call
import retrofit2.Response
import java.io.IOException

class ApiServiceTest {

    private val apiService = RetrofitClient.apiService
    private val authorizationHeader = ""

    @Test
    fun testCheckinsPostSessionIdNotEmpty() {
        val sessionId = ""
        val rut = "3403231-9"
        val call: Call<CheckInByRutResponse> =
            apiService.checkInByRut(authorizationHeader, CheckInByRutRequest(sessionId, rut))

        try {
            val response: Response<CheckInByRutResponse> = call.execute()
            val errorBody = response.errorBody()?.string()

            assertEquals("Status code should be 400", 400, response.code())
            assertNotNull("Error body should not be null", errorBody)

            val gson = Gson()
            val type = object : TypeToken<CheckInByRutResponse>() {}.type
            val checkInByRutResponse: CheckInByRutResponse? = gson.fromJson(errorBody, type)

            assertNotNull("Deserialized error body should not be null", checkInByRutResponse)
            assertFalse("Result should be false", checkInByRutResponse?.result == true)

        } catch (e: IOException) {
            fail("Network error: ${e.message}")
        }
    }

    @Test
    fun testCheckinsPostSessionIdNotString() {
        val sessionId = "abcd"
        val rut = "3403231-9"
        val call: Call<CheckInByRutResponse> =
            apiService.checkInByRut(authorizationHeader, CheckInByRutRequest(sessionId, rut))

        try {
            val response: Response<CheckInByRutResponse> = call.execute()
            val errorBody = response.errorBody()?.string()

            assertEquals("Status code should be 400", 400, response.code())
            assertNotNull("Error body should not be null", errorBody)

            val gson = Gson()
            val type = object : TypeToken<CheckInByRutResponse>() {}.type
            val checkInByRutResponse: CheckInByRutResponse? = gson.fromJson(errorBody, type)

            assertNotNull("Deserialized error body should not be null", checkInByRutResponse)
            assertFalse("Result should be false", checkInByRutResponse?.result == true)

        } catch (e: IOException) {
            fail("Network error: ${e.message}")
        }
    }

    @Test
    fun testCheckinsPostRutNotEmpty() {
        val sessionId = -2
        val rut = ""
        val call: Call<CheckInByRutResponse> =
            apiService.checkInByRut(authorizationHeader, CheckInByRutRequest(sessionId, rut))

        try {
            val response: Response<CheckInByRutResponse> = call.execute()
            val errorBody = response.errorBody()?.string()

            assertEquals("Status code should be 400", 400, response.code())
            assertNotNull("Error body should not be null", errorBody)

            val gson = Gson()
            val type = object : TypeToken<CheckInByRutResponse>() {}.type
            val checkInByRutResponse: CheckInByRutResponse? = gson.fromJson(errorBody, type)

            assertNotNull("Deserialized error body should not be null", checkInByRutResponse)
            assertFalse("Result should be false", checkInByRutResponse?.result == true)

        } catch (e: IOException) {
            fail("Network error: ${e.message}")
        }
    }

    @Test
    fun testCheckinsPostRutNotValid() {
        val sessionId = -2
        val rut = "12345678-9"
        val call: Call<CheckInByRutResponse> =
            apiService.checkInByRut(authorizationHeader, CheckInByRutRequest(sessionId, rut))

        try {
            val response: Response<CheckInByRutResponse> = call.execute()
            val errorBody = response.errorBody()?.string()

            assertEquals("Status code should be 400", 400, response.code())
            assertNotNull("Error body should not be null", errorBody)

            val gson = Gson()
            val type = object : TypeToken<CheckInByRutResponse>() {}.type
            val checkInByRutResponse: CheckInByRutResponse? = gson.fromJson(errorBody, type)

            assertNotNull("Deserialized error body should not be null", checkInByRutResponse)
            assertFalse("Result should be false", checkInByRutResponse?.result == true)

        } catch (e: IOException) {
            fail("Network error: ${e.message}")
        }
    }

    @Test
    fun testCheckinsPostSessionIdNotFound() {
        val sessionId = -2
        val rut = "3403231-9"
        val call: Call<CheckInByRutResponse> =
            apiService.checkInByRut(authorizationHeader, CheckInByRutRequest(sessionId, rut))

        try {
            val response: Response<CheckInByRutResponse> = call.execute()
            val errorBody = response.errorBody()?.string()

            assertEquals("Status code should be 404", 404, response.code())
            assertNotNull("Error body should not be null", errorBody)

            val gson = Gson()
            val type = object : TypeToken<CheckInByRutResponse>() {}.type
            val checkInByRutResponse: CheckInByRutResponse? = gson.fromJson(errorBody, type)

            assertNotNull("Deserialized error body should not be null", checkInByRutResponse)
            assertFalse("Result should be false", checkInByRutResponse?.result == true)

        } catch (e: IOException) {
            fail("Network error: ${e.message}")
        }
    }

    @Test
    fun testCheckinsPostSuccess() {
        val sessionId = 2042882
        val rut = "3403231-9"
        val call: Call<CheckInByRutResponse> =
            apiService.checkInByRut(authorizationHeader, CheckInByRutRequest(sessionId, rut))
        try {
            val response: Response<CheckInByRutResponse> = call.execute()
            val errorBody = response.errorBody()?.string()
            val checkInByRutResponse: CheckInByRutResponse? = response.body()

            assertEquals("Status code should be 200", 200, response.code())
            assertNull("Error body should not be null", errorBody)
            assertNotNull("Response body should not be null", checkInByRutResponse)
            assertTrue("Result should be false", checkInByRutResponse?.result == true)

        } catch (e: IOException) {
            fail("Network error: ${e.message}")
        }
    }
    @Test
    fun testCheckinsByRegistrantFailString() {
        val sessionId = "zzzzzzz"
        val registrantId = "zzzzzz"
        val call: Call<CheckInByRegistrantResponse> =
            apiService.checkInByRegistrant(CheckInByRegistrantRequest(sessionId, registrantId))

        try {
            val response: Response<CheckInByRegistrantResponse> = call.execute()
            val errorBody = response.errorBody()?.string()

            assertEquals("Status code should be 400", 400, response.code())
            assertNotNull("Error body should not be null", errorBody)

            val gson = Gson()
            val type = object : TypeToken<CheckInByRegistrantResponse>() {}.type
            val sessionsPostResponse: CheckInByRegistrantResponse? = gson.fromJson(errorBody, type)

            assertNotNull("Deserialized error body should not be null", sessionsPostResponse)
            assertFalse("Result should be false", sessionsPostResponse?.result == true)

        } catch (e: IOException) {
            fail("Network error: ${e.message}")
        }
    }

    @Test
    fun testCheckinsByRegistrantFailNumber() {
        val sessionId = "1"
        val registrantId = "1"
        val call: Call<CheckInByRegistrantResponse> =
            apiService.checkInByRegistrant(CheckInByRegistrantRequest(sessionId, registrantId))

        try {
            val response: Response<CheckInByRegistrantResponse> = call.execute()
            val errorBody = response.errorBody()?.string()

            assertEquals("Status code should be 404", 404, response.code())
            assertNotNull("Error body should not be null", errorBody)

            val gson = Gson()
            val type = object : TypeToken<CheckInByRegistrantResponse>() {}.type
            val sessionsPostResponse: CheckInByRegistrantResponse? = gson.fromJson(errorBody, type)

            assertNotNull("Deserialized error body should not be null", sessionsPostResponse)
            assertFalse("Result should be false", sessionsPostResponse?.result == true)

        } catch (e: IOException) {
            fail("Network error: ${e.message}")
        }
    }

    @Test
    fun testCheckinsByRutFailRutEmpty() {
        val sessionId = "123456"
        val rut = ""
        val call: Call<CheckInByRutResponse> =
            apiService.checkInByRut(authorizationHeader, CheckInByRutRequest(sessionId, rut))

        try {
            val response: Response<CheckInByRutResponse> = call.execute()
            val errorBody = response.errorBody()?.string()

            assertEquals("Status code should be 400", 400, response.code())
            assertNotNull("Error body should not be null", errorBody)

            val gson = Gson()
            val type = object : TypeToken<CheckInByRutResponse>() {}.type
            val checkInByRutResponse: CheckInByRutResponse? = gson.fromJson(errorBody, type)

            assertNotNull("Deserialized error body should not be null", checkInByRutResponse)
            assertFalse("Result should be false", checkInByRutResponse?.result == true)

        } catch (e: IOException) {
            fail("Network error: ${e.message}")
        }
    }





    /*val sessionsPostResponse: SessionsPostResponse? = response.body()
            assertNotNull("Response body should not be null", sessionsPostResponse)
            assertFalse("Result should be false", sessionsPostResponse?.result == true)*/
}