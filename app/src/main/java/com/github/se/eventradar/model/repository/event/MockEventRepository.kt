package com.github.se.eventradar.model.repository.event

import com.github.se.eventradar.model.Location
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.event.Event
import com.github.se.eventradar.model.event.EventCategory
import com.github.se.eventradar.model.event.EventTicket
import java.time.LocalDateTime
import javax.inject.Inject

class MockEventRepository @Inject constructor() : IEventRepository {
  override suspend fun getEvents(): Resource<List<Event>> {
    val events =
        List(20) {
          Event(
              eventName = "Event $it",
              eventPhoto = "",
              start = LocalDateTime.parse("2021-10-01T10:00:00"),
              end = LocalDateTime.parse("2021-10-01T12:00:00"),
              location = Location(0.0, 0.0, "Test Location"),
              description = "Test Description",
              ticket = EventTicket("Test Ticket", 0.0, 1),
              contact = "Test Contact Email",
              organiserList = setOf("Test Organiser"),
              attendeeList = setOf("Test Attendee"),
              category = EventCategory.COMMUNITY,
              fireBaseID = "$it")
        }

    return Resource.Success(events)
  }

  override suspend fun getEvent(id: String): Resource<Event?> {
    val event =
        Event(
            eventName = "Event $id",
            eventPhoto = "",
            start = LocalDateTime.parse("2021-10-01T10:00:00"),
            end = LocalDateTime.parse("2021-10-01T12:00:00"),
            location = Location(0.0, 0.0, "Test Location"),
            description = "Test Description",
            ticket = EventTicket("Test Ticket", 0.0, 1),
            contact = "Test Contact Email",
            organiserList = setOf("Test Organiser"),
            attendeeList = setOf("Test Attendee"),
            category = EventCategory.COMMUNITY,
            fireBaseID = id)

    return Resource.Success(event)
  }

  override suspend fun addEvent(event: Event): Resource<Boolean> {
    return Resource.Success(true)
  }

  override suspend fun updateEvent(event: Event): Resource<Boolean> {
    return Resource.Success(true)
  }

  override suspend fun deleteEvent(event: Event): Resource<Boolean> {
    return Resource.Success(true)
  }
}
