package com.github.se.eventradar.model.event

import java.time.LocalDate
import java.time.LocalTime

enum class EventCategory {
    MUSIC, SPORTS, CONFERENCE, EXHIBITION, COMMUNITY
}

data class Event(
    val id: String,
    val hostUserId: String,
    val name: String,
    val description: String,
    val date: LocalDate,
    val time: LocalTime,
    val category: EventCategory
    // TODO: Add 'location' attribute once the Map feature is implemented
)