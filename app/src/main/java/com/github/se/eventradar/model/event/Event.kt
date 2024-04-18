package com.github.se.eventradar.model.event

import com.github.se.eventradar.model.Location
import java.time.LocalDateTime

// new event struct final
data class Event(
    val eventName: String,
    val eventPhoto: String,
    val start: LocalDateTime,
    val end: LocalDateTime,
    val location: Location,
    val description: String,
    val ticket: EventTicket,
    val contact: String,
    val organiserList: Set<String>,
    val attendeeList: Set<String>,
    val category: EventCategory,
    val fireBaseID: String
)