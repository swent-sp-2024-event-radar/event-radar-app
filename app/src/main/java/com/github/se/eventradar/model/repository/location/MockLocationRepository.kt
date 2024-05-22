package com.github.se.eventradar.model.repository.location

import com.github.se.eventradar.model.Location
import com.github.se.eventradar.model.Resource

class MockLocationRepository : ILocationRepository {
  override suspend fun fetchLocation(locationName: String): Resource<Location> {
    return Resource.Success(Location(latitude = 100.0, longitude = 100.0, address = locationName))
  }
}
