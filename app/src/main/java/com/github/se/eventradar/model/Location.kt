package com.github.se.eventradar.model

data class Location(
    var latitude: Double,
    var longitude: Double,
    var address: String,
)

fun getLocation(name: String, lat: Double, lng: Double): Location {
  return Location(lat, lng, name)
}
