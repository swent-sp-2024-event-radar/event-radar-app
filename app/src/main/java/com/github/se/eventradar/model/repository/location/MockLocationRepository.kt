package com.github.se.eventradar.model.repository.location

import com.github.se.eventradar.model.Location
import com.github.se.eventradar.model.Resource

class MockLocationRepository : ILocationRepository {
  override suspend fun fetchLocation(locationName: String): Resource<List<Location>> {
    return if (locationName.isBlank()) {
      Resource.Failure(Exception("Empty location name is invalid"))
    } else {
      Resource.Success(
          listOf(Location(latitude = 100.0, longitude = 100.0, address = locationName)))
    }
  }
}
