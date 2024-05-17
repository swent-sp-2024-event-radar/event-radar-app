package com.github.se.eventradar.model.repository.event

import com.github.se.eventradar.model.Location
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.event.Event
import com.github.se.eventradar.model.event.EventCategory
import com.github.se.eventradar.model.event.EventTicket
import java.time.LocalDateTime

class MockEventRepository : IEventRepository {
  private val mockEvents = mutableListOf <Event>()
//    ( Event(
//      eventName = "Event 1",
//      eventPhoto = "",
//      start = LocalDateTime.now(),
//      end = LocalDateTime.now(),
//      location = Location(0.0, 0.0, "Test Location"),
//      description = "Hey,Because of the grace period until Sunday, actually the real deadline is Sunday 23:59pm. Do you think it would be feasible to have your branche merged and taken into account for the team submission ? I added a timeout because the CI originally failed with a timeout error, so I need to setup a different EMulator when testing locally? advance till idle was just a thing i added to try and make it work",
//      ticket = EventTicket("Test Ticket", 0.0, 100, 59),
//      mainOrganiser = "1",
//      organiserList = mutableListOf("Test Organiser"),
//      attendeeList = mutableListOf("user1", "user2", "user3"),
//      category = EventCategory.COMMUNITY,
//      fireBaseID = "1")
//  )



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
}
