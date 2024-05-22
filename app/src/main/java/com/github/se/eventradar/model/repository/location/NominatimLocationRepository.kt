package com.github.se.eventradar.model.repository.location

import android.util.Log
import com.github.se.eventradar.model.Location
import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException
import com.github.se.eventradar.model.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.internal.wait

class NominatimLocationRepository(val client: OkHttpClient = OkHttpClient(), val requestBuilder : Request.Builder = Request.Builder(), private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) : ILocationRepository{
    override suspend fun fetchLocation(locationName: String) : Resource<Location> {
        return try {
        val url = "https://nominatim.openstreetmap.org/search?q=${locationName}&format=json"
            println("Went by this0.0")
        val request = requestBuilder.url(url).build()
            println("Went by this0")
        val response =
            withContext(ioDispatcher) {
            client.newCall(request).execute()
        }
        println("Went by this")
        if (!response.isSuccessful){
            throw IOException("Request to get location data failed due to IOException ${response}")
        }
            println("Went by this2")
        val jsonArray = JSONArray(response.body!!.string())
            println("Went by this3")
        val firstObject = jsonArray.getJSONObject(0)
            println("Went by this4")
        val fetchedLocationName = firstObject.getString("name")
            println("Went by this5")
        val latitude = firstObject.getString("lat").toDouble()
            println("Went by this6")
        val longitude = firstObject.getString("lon").toDouble()
            println("Went by this7")

        Resource.Success(Location(latitude,longitude,fetchedLocationName))
        } catch (e: Exception){
            Resource.Failure(e)
        }
    }
}