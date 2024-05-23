package com.github.se.eventradar.model.repository.location

import com.github.se.eventradar.model.Location
import com.github.se.eventradar.model.Resource

fun interface ILocationRepository {
  suspend fun fetchLocation(locationName: String): Resource<List<Location>>
}
