package com.github.se.eventradar.model.repository.location

import com.github.se.eventradar.model.Location
import com.github.se.eventradar.model.Resource

interface ILocationRepository {
    suspend fun fetchLocation(locationName: String) : Resource<Location>
}