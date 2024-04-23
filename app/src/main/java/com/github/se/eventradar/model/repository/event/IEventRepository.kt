package com.github.se.eventradar.model.repository.event

import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.event.Event

interface IEventRepository {
  suspend fun getEvents(): Resource<List<Event>>

  suspend fun getEvent(id: String): Resource<Event?>

  suspend fun addEvent(event: Event): Resource<Unit>

  suspend fun updateEvent(event: Event): Resource<Unit>

  suspend fun deleteEvent(event: Event): Resource<Unit>

  suspend fun getEventsByIds(ids: List<String>): Resource<List<Event>>
}