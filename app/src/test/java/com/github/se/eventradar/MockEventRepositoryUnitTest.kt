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

  private val mockEvent =
      Event(
          eventName = "Event 1",
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
          fireBaseID = "1")

  @Before
  fun setUp() {
    eventRepository = MockEventRepository()
  }

  @Test
  fun testGetEventsEmptyAtConstruction() = runTest {
    // Test getEvents() method
    val events = eventRepository.getEvents()

    assert(events is Resource.Success)

    assert((events as Resource.Success).data.isEmpty())
  }

  @Test
  fun testGetEvent() = runTest {
    // Test getEvent() method
    eventRepository.addEvent(mockEvent)

    val event = eventRepository.getEvent("1")

    assert(event is Resource.Success)
    assert((event as Resource.Success).data == mockEvent)
  }

  @Test
  fun testAddEvent() = runTest {
    // Test addEvent() method
    val result = eventRepository.addEvent(mockEvent)

    assert(result is Resource.Success)
    var allEvents = eventRepository.getEvents()
    assert(allEvents is Resource.Success)

    allEvents = allEvents as Resource.Success
    assert(allEvents.data.isNotEmpty())
    assert(allEvents.data.contains(mockEvent))

    val getEvent = eventRepository.getEvent(mockEvent.fireBaseID)
    assert(getEvent is Resource.Success)
    assert((getEvent as Resource.Success).data == mockEvent)
  }

  @Test
  fun testUpdateEvent() = runTest {
    // Test updateEvent() method
    eventRepository.addEvent(mockEvent)

    val newEventName = "Updated Event 1"

    val updatedEvent = mockEvent.copy(eventName = newEventName)

    val result = eventRepository.updateEvent(updatedEvent)
    assert(result is Resource.Success)

    val getEvent = eventRepository.getEvent(mockEvent.fireBaseID)
    assert(getEvent is Resource.Success)
    assert(((getEvent as Resource.Success).data as Event).eventName == newEventName)
  }

  @Test
  fun testDeleteEvent() = runTest {
    // Test deleteEvent() method
    eventRepository.addEvent(mockEvent)

    val result = eventRepository.deleteEvent(mockEvent)
    assert(result is Resource.Success)

    val allEvents = eventRepository.getEvents()
    assert(allEvents is Resource.Success)
    assert((allEvents as Resource.Success).data.isEmpty())
  }

  @Test
  fun testDeleteEventNotInRepository() = runTest {
    // Test deleteEvent() method with an event not in the repository
    val result = eventRepository.deleteEvent(mockEvent)
    assert(result is Resource.Failure)
    assert(
        (result as Resource.Failure).throwable.message ==
            "Event with id ${mockEvent.fireBaseID} not found")
  }

  @Test
  fun testUpdateEventNotInRepository() = runTest {
    // Test updateEvent() method with an event not in the repository
    val result = eventRepository.updateEvent(mockEvent)
    assert(result is Resource.Failure)
    assert(
        (result as Resource.Failure).throwable.message ==
            "Event with id ${mockEvent.fireBaseID} not found")
  }

  @Test
  fun testGetEventNotInRepository() = runTest {
    // Test getEvent() method with an event not in the repository
    val result = eventRepository.getEvent(mockEvent.fireBaseID)
    assert(result is Resource.Failure)
    assert(
        (result as Resource.Failure).throwable.message ==
            "Event with id ${mockEvent.fireBaseID} not found")
  }

  @Test
  fun testGetEvents() = runTest {
    // Test getEvents() method
    val event1 = mockEvent.copy(fireBaseID = "1")
    val event2 = mockEvent.copy(fireBaseID = "2")
    val event3 = mockEvent.copy(fireBaseID = "3")

    eventRepository.addEvent(event1)
    eventRepository.addEvent(event2)
    eventRepository.addEvent(event3)

    val events = eventRepository.getEvents()
    assert(events is Resource.Success)
    assert((events as Resource.Success).data.size == 3)
  }
}
