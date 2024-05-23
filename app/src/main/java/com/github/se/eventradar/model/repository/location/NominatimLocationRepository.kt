package com.github.se.eventradar.model.repository.location

import com.github.se.eventradar.model.Location
import com.github.se.eventradar.model.Resource
import java.io.IOException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray

class NominatimLocationRepository(
    val client: OkHttpClient = OkHttpClient(),
    val requestBuilder: Request.Builder = Request.Builder(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ILocationRepository {
  override suspend fun fetchLocation(locationName: String): Resource<List<Location>> {
    return try {
      val locationList = mutableListOf<Location>()
      val url = "https://nominatim.openstreetmap.org/search?q=${locationName}&format=json"
      val request = requestBuilder.url(url).build()
      val response = withContext(ioDispatcher) { client.newCall(request).execute() }
      if (!response.isSuccessful) {
        return Resource.Failure(
            IOException("Request to get location data failed due to IOException ${response}"))
      }
      val jsonArray = JSONArray(response.body!!.string())
      for (i in 0 until jsonArray.length()) {
        val location = jsonArray.getJSONObject(i)
        locationList.add(
            Location(
                location.getDouble("lat"),
                location.getDouble("lon"),
                location.getString("name"),
            ))
      }
      Resource.Success(locationList)
    } catch (e: Exception) {
      Resource.Failure(e)
    }
  }
}
