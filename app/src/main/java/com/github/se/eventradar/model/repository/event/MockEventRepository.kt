package com.github.se.eventradar.model.repository.event

import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.event.Event
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class MockEventRepository : IEventRepository {

  val mockEvents = mutableListOf<Event>()
  private var ticker = 0
  val eventsFlow = MutableStateFlow<Resource<List<Event>>>(Resource.Success(mockEvents))
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

  override suspend fun getUniqueEventId(): Resource<String> {
    return Resource.Success(ticker++.toString())
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

  override fun observeAllEvents(): Flow<Resource<List<Event>>> = eventsFlow.asStateFlow()

  override fun observeUpcomingEvents(userId: String): Flow<Resource<List<Event>>> {
    return eventsFlow.asStateFlow().map { resource ->
      when (resource) {
        is Resource.Success -> {
          val upcomingEvents = resource.data.filter { it.attendeeList.contains(userId) }
          Resource.Success(upcomingEvents)
        }
        is Resource.Failure -> resource
      }
    }
  }
}
