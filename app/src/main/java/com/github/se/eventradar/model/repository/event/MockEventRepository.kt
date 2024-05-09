package com.github.se.eventradar.model.repository.event

import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.event.Event
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MockEventRepository : IEventRepository {
  private val mockEvents = mutableListOf<Event>()
  private val eventsFlow = MutableStateFlow<Resource<List<Event>>>(Resource.Success(mockEvents))

  override suspend fun getEvents(): Resource<List<Event>> {
    return Resource.Success(mockEvents)
  }

  override suspend fun getEvent(id: String): Resource<Event?> {
    val event = mockEvents.find { it.fireBaseID == id }

    return if (event != null) {
      Resource.Success(event)
    } else {
      Resource.Failure(Exception("Event with id $id not found"))
    }
  }

  override suspend fun addEvent(event: Event): Resource<Unit> {
    mockEvents.add(event)
    return Resource.Success(Unit)
  }

  override suspend fun updateEvent(event: Event): Resource<Unit> {
    val index = mockEvents.indexOfFirst { it.fireBaseID == event.fireBaseID }

    return if (index != -1) {
      mockEvents[index] = event
      Resource.Success(Unit)
    } else {
      Resource.Failure(Exception("Event with id ${event.fireBaseID} not found"))
    }
  }

  override suspend fun deleteEvent(event: Event): Resource<Unit> {
    return if (mockEvents.remove(event)) {
      Resource.Success(Unit)
    } else {
      Resource.Failure(Exception("Event with id ${event.fireBaseID} not found"))
    }
  }

  override suspend fun getEventsByIds(ids: List<String>): Resource<List<Event>> {
    val events = mutableListOf<Event>()
    for (id in ids) {
      val event = mockEvents.find { it.fireBaseID == id }
      if (event != null) {
        events.add(event)
      } else {
        return Resource.Failure(Exception("Event with id $id is missing"))
      }
    }
    return Resource.Success(events)
  }

  override fun observeEvents(): Flow<Resource<List<Event>>> =
      eventsFlow.asStateFlow() // to be tested
}
