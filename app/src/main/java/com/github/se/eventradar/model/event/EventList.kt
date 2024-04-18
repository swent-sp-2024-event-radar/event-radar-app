package com.github.se.eventradar.model.event

data class EventList(
    val allEvents: List<Event>,
    val filteredEvent: List<Event> = allEvents,
    val selectedEvent: Event? = null,
)
