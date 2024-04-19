package com.github.se.eventradar.model.repository

import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.event.Event

interface EventRepository {
  suspend fun getEvents(): Resource<List<Event>>

  suspend fun getEvent(id: String): Resource<Event?>

  suspend fun addEvent(event: Event)

  suspend fun updateEvent(event: Event)

  suspend fun deleteEvent(event: Event)
}
