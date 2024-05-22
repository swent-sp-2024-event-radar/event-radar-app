package com.github.se.eventradar

import com.github.se.eventradar.model.Location
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.repository.location.NominatimLocationRepository
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.Call
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONArray
import org.json.JSONObject
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import kotlin.math.exp

class NominatimLocationRepositoryUnitTest {
    private lateinit var locationRepository : NominatimLocationRepository
    private val mockLocation =
        Location(100.0,100.0,"EPFL")
    @RelaxedMockK lateinit var mockClient : OkHttpClient
    @RelaxedMockK lateinit var mockRequest : Request
    @RelaxedMockK lateinit var mockResponse : Response
    @RelaxedMockK lateinit var mockCall : Call
    @RelaxedMockK lateinit var mockRequestBuilder : Request.Builder
    @RelaxedMockK lateinit var mockJsonArray: JSONArray
    @RelaxedMockK lateinit var mockJSONObject: JSONObject



    private val expectedLocation =
        Location(latitude = 46.51, longitude = 6.56, address = "École Polytechnique Fédérale de Lausanne")

    @Before
    fun setUp() {
        mockClient = mockk()
        mockRequest = mockk()
        mockResponse = mockk()
        mockCall = mockk()
        mockRequestBuilder = mockk()
        mockJSONObject = mockk()
        mockJsonArray = mockk()
        val dispatcher = StandardTestDispatcher()
        every{mockRequestBuilder.url(any<String>())} returns mockRequestBuilder
        every{mockRequestBuilder.build()} returns mockRequest
        every {mockClient.newCall(request = mockRequest) } returns mockCall
        every {mockCall.execute()} returns mockResponse
        //need to pass in request and client!
        locationRepository = NominatimLocationRepository(mockClient, mockRequestBuilder, dispatcher)
    }

    @Test
    fun testGetEventsEmptyAtConstruction() = runTest {

        val jsonString = "[{\"name\":\"École Polytechnique Fédérale de Lausanne\",\"lat\":46.51,\"lon\":6.56}]"
        val responseBody = jsonString.toResponseBody("application/json".toMediaType())

        every { mockClient.newCall(any()).execute() } returns mockResponse

        every{mockResponse.isSuccessful} returns true
        every { mockResponse.body } returns responseBody

        val location = locationRepository.fetchLocation("EPFL")

        assert(location is Resource.Success)
        assert((location as Resource.Success).data.address == mockLocation.address)
        assert(location.data.latitude == mockLocation.latitude)
        assert(location.data.longitude == mockLocation.longitude)
    }
}