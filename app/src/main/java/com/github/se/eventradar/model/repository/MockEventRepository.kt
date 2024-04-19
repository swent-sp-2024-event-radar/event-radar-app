package com.github.se.eventradar.model.repository

import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.event.Event

class MockEventRepository : EventRepository {
  override suspend fun getEvents(): Resource<List<Event>> {
    TODO("Not yet implemented")
  }

  override suspend fun getEvent(id: String): Resource<Event?> {
    TODO("Not yet implemented")
  }

  override suspend fun addEvent(event: Event) {
    TODO("Not yet implemented")
  }

  override suspend fun updateEvent(event: Event) {
    TODO("Not yet implemented")
  }

  override suspend fun deleteEvent(event: Event) {
    TODO("Not yet implemented")
  }
}
