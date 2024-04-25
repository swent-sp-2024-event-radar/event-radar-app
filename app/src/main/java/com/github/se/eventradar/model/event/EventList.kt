package com.github.se.eventradar.model.event

data class EventList(
    val allEvents: List<Event>,
    val filteredEvents: List<Event> = allEvents,
    val selectedEvent: Event? = null,
)
