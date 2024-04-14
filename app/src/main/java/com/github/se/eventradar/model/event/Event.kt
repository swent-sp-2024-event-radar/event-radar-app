package com.github.se.eventradar.model.event
import com.github.se.eventradar.model.Location
import com.google.firebase.firestore.DocumentReference
import java.time.LocalDateTime
import java.util.Date

enum class EventCategory {
  MUSIC,
  SPORTS,
  CONFERENCE,
  EXHIBITION,
  COMMUNITY
}
data class Ticket(
    val name: String, val price: Double, val quantity: Int
)
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
