package com.github.se.eventradar.model.event

import com.github.se.eventradar.model.ConversionUtils.convertToMutableListOfStrings
import com.github.se.eventradar.model.Location
import java.time.LocalDateTime

data class Event(
    var eventName: String,
    var eventPhoto: String,
    var start: LocalDateTime,
    var end: LocalDateTime,
    var location: Location,
    var description: String,
    var ticket: EventTicket,
    var mainOrganiser: String,
    val organiserList: MutableList<String>,
    val attendeeList: MutableList<String>,
    var category: EventCategory,
    val fireBaseID: String
) {
  constructor(
      map: Map<String, Any>,
      id: String,
  ) : this(
      eventName = map["name"] as String,
      eventPhoto = map["photo_url"] as String,
      start = LocalDateTime.parse(map["start"] as String),
      end = LocalDateTime.parse(map["end"] as String),
      location =
          Location(
              latitude = map["location_lat"] as Double,
              longitude = map["location_lng"] as Double,
              address = map["location_name"] as String),
      description = map["description"] as String,
      ticket =
          EventTicket(
              name = map["ticket_name"] as String,
              price = convertToDouble(map["ticket_price"]),
              capacity = (map["ticket_capacity"] as Long).toInt(),
              purchases = (map["ticket_purchases"] as Long).toInt()),
      mainOrganiser = map["main_organiser"] as String,
      organiserList = convertToMutableListOfStrings(map["organisers_list"]),
      attendeeList = convertToMutableListOfStrings(map["attendees_list"]),
      category = EventCategory.valueOf(map["category"] as String),
      fireBaseID = id)

  fun toMap(): HashMap<String, Any> {
    val map = HashMap<String, Any>()
    map["name"] = eventName
    map["photo_url"] = eventPhoto
    map["start"] = start.toString()
    map["end"] = end.toString()
    map["location_lat"] = location.latitude
    map["location_lng"] = location.longitude
    map["location_name"] = location.address
    map["description"] = description
    map["ticket_name"] = ticket.name
    map["ticket_price"] = ticket.price
    map["ticket_capacity"] = ticket.capacity
    map["main_organiser"] = mainOrganiser
    map["organisers_list"] = organiserList.toList()
    map["attendees_list"] = attendeeList.toList()
    map["category"] = category.name
    return map
  }
}

// helper function
private fun convertToDouble(value: Any?): Double {
  return when (value) {
    is Double -> value
    is Long -> value.toDouble()
    else -> 0.0 // Default value
  }
}
