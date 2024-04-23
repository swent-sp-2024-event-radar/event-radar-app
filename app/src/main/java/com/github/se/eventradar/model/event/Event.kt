package com.github.se.eventradar.model.event

import com.github.se.eventradar.model.Location
import java.time.LocalDateTime

// new event struct final
data class Event(
    var eventName: String,
    var eventPhoto: String,
    var start: LocalDateTime,
    var end: LocalDateTime,
    var location: Location,
    var description: String,
    var ticket: EventTicket,
    var contact: String,
    var organiserList: Set<String>,
    var attendeeList: Set<String>,
    var category: EventCategory,
    var fireBaseID: String
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
              map["location_lat"] as Double,
              longitude = map["location_lng"] as Double,
              address = map["location_name"] as String),
      description = map["description"] as String,
      ticket =
          EventTicket(
              name = map["ticket_name"] as String,
              price = map["ticket_price"] as Double,
              capacity = map["ticket_quantity"] as Int),
      contact = map["contact"] as String,
      organiserList = getSetOfStrings(map["organisers_list"]),
      attendeeList = getSetOfStrings(map["attendees_list"]),
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
    map["ticket_quantity"] = ticket.capacity
    map["contact"] = contact
    map["organisers_list"] = organiserList.toList()
    map["attendees_list"] = attendeeList.toList()
    map["category"] = category.name
    return map
  }
}

private fun getSetOfStrings(data: Any?): Set<String> {
  return when (data) {
    is List<*> -> data.filterIsInstance<String>().toSet()
    is String -> setOf(data)
    else -> emptySet()
  }
}
