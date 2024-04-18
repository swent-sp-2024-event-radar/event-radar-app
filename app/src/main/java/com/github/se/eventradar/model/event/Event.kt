package com.github.se.eventradar.model.event

import com.github.se.eventradar.model.Location
import java.time.LocalDateTime

enum class EventCategory {
  MUSIC,
  SPORTS,
  CONFERENCE,
  EXHIBITION,
  COMMUNITY
}

data class Ticket(val name: String, val price: Double, val quantity: Int)
// new event struct final
data class Event(
    val eventName: String,
    val eventPhoto: String,
    val start: LocalDateTime,
    val end: LocalDateTime,
    val location: Location,
    val description: String,
    val ticket: Ticket,
    val contact: String,
    val organiserList: Set<String>,
    val attendeeList: Set<String>,
    val category: EventCategory,
    val fireBaseID: String
)
