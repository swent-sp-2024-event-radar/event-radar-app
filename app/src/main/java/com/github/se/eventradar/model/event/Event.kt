package com.github.se.eventradar.model.event

import com.github.se.eventradar.model.Location
import java.time.LocalDateTime

data class Event(
    val eventName: String,
    val eventPhoto: String,
    val start: LocalDateTime,
    val end: LocalDateTime,
    val location: Location,
    val description: String,
    val ticket: EventTicket,
    val mainOrganiser: String,
    val organiserSet: MutableSet<String>,
    val attendeeSet: MutableSet<String>,
    val category: EventCategory,
    val fireBaseID: String
) {
  constructor(
      map: Map<String, Any>,
      id: String
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
              price = (map["ticket_price"] as Long).toDouble(),
              capacity = (map["ticket_quantity"] as Long).toInt()),
      mainOrganiser = map["main_organiser"] as String,
      organiserSet = getMutableSetOfStrings(map["organisers_list"]),
      attendeeSet = getMutableSetOfStrings(map["attendees_list"]),
      category = EventCategory.valueOf(map["category"] as String),
      fireBaseID = id)

  fun toMap(): HashMap<String, Any> {
    val map = HashMap<String, Any>()
    map["name"] = eventName
    map["photo_url"] = eventPhoto
    map["start"] = start.toString()
    map["end"] = end.toString()
    map["location_name"] = location.address
    map["location_lat"] = location.latitude
    map["location_lng"] = location.longitude
    map["description"] = description
    map["ticket_name"] = ticket.name
    map["ticket_price"] = ticket.price
    map["ticket_quantity"] = ticket.capacity
    map["main_organiser"] = mainOrganiser
    map["organisers_list"] = organiserSet.toMutableSet()
    map["attendees_list"] = attendeeSet.toMutableSet()
    map["category"] = category.name
    return map
  }
}

private fun getMutableSetOfStrings(data: Any?): MutableSet<String> {
  return when (data) {
    is List<*> -> data.filterIsInstance<String>().toMutableSet()
    is String -> mutableSetOf(data)
    else -> mutableSetOf()
  }
}
