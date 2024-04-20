package com.github.se.eventradar

import com.github.se.eventradar.model.Location
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.event.Event
import com.github.se.eventradar.model.event.EventCategory
import com.github.se.eventradar.model.event.EventTicket
import com.github.se.eventradar.model.repository.event.IEventRepository
import com.github.se.eventradar.model.repository.event.MockEventRepository
import java.time.LocalDateTime
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class MockEventRepositoryUnitTest {
  private lateinit var eventRepository: IEventRepository

  private val eventsExpectedValue =
      List(5) {
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

  @Before
  fun setUp() {
    eventRepository = MockEventRepository()
  }

  @Test
  fun testGetEvents() = runTest {
    // Test getEvents() method
    val events = eventRepository.getEvents()

    assert(events is Resource.Success)

    for (i in eventsExpectedValue.indices) {
      assert((events as Resource.Success).data[i] == eventsExpectedValue[i])
    }
  }

  @Test
  fun testGetEvent() = runTest {
    // Test getEvent() method
    val event = eventRepository.getEvent("1")

    assert(event is Resource.Success)
    assert((event as Resource.Success).data == eventsExpectedValue[1])
  }

  @Test
  fun testAddEvent() = runTest {
    // Test addEvent() method
    val event =
        Event(
            eventName = "Event 21",
            eventPhoto = "",
            start = LocalDateTime.now(),
            end = LocalDateTime.now(),
            location = Location(0.0, 0.0, "Test Location"),
            description = "Test Description",
            ticket = EventTicket("Test Ticket", 0.0, 1),
            contact = "Test Contact Email",
            organiserList = setOf("Test Organiser"),
            attendeeList = setOf("Test Attendee"),
            category = EventCategory.COMMUNITY,
            fireBaseID = "21")

    val result = eventRepository.addEvent(event)

    assert(result is Resource.Success)
    assert((result as Resource.Success).data)
  }

  @Test
  fun testUpdateEvent() = runTest {
    // Test updateEvent() method
    val event =
        Event(
            eventName = "Event 21",
            eventPhoto = "",
            start = LocalDateTime.now(),
            end = LocalDateTime.now(),
            location = Location(0.0, 0.0, "Test Location"),
            description = "Test Description",
            ticket = EventTicket("Test Ticket", 0.0, 1),
            contact = "Test Contact Email",
            organiserList = setOf("Test Organiser"),
            attendeeList = setOf("Test Attendee"),
            category = EventCategory.COMMUNITY,
            fireBaseID = "21")

    val result = eventRepository.updateEvent(event)

    assert(result is Resource.Success)
    assert((result as Resource.Success).data)
  }

  @Test
  fun testDeleteEvent() = runTest {
    // Test deleteEvent() method
    val event =
        Event(
            eventName = "Event 21",
            eventPhoto = "",
            start = LocalDateTime.now(),
            end = LocalDateTime.now(),
            location = Location(0.0, 0.0, "Test Location"),
            description = "Test Description",
            ticket = EventTicket("Test Ticket", 0.0, 1),
            contact = "Test Contact Email",
            organiserList = setOf("Test Organiser"),
            attendeeList = setOf("Test Attendee"),
            category = EventCategory.COMMUNITY,
            fireBaseID = "21")

    val result = eventRepository.deleteEvent(event)

    assert(result is Resource.Success)
    assert((result as Resource.Success).data)
  }
}
