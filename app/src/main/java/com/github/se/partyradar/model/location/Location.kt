package com.github.se.partyradar.model.location

data class Location(
    var latitude: Double,
    var longitude: Double,
    var name: String,
)

fun getLocation(name: String, lat: Double, lng: Double): Location {
  return Location(lat, lng, name)
}
