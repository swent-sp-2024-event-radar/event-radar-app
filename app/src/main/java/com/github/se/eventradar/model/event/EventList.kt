package com.github.se.eventradar.model.event

data class EventList(
    val getAllEvent: List<Event>,
    val getFilteredEvent: List<Event> = getAllEvent,
    val getEvent: Event? = null,
)