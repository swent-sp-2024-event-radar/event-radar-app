package com.github.se.eventradar

import com.github.se.eventradar.model.Location
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.event.Event
import com.github.se.eventradar.model.event.EventCategory
import com.github.se.eventradar.model.event.EventTicket
import com.github.se.eventradar.model.repository.event.IEventRepository
import com.github.se.eventradar.model.repository.event.MockEventRepository
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class MockEventRepositoryUnitTest {
  private lateinit var eventRepository: IEventRepository

  private val mockEvent =
    Event(
      eventName = "Event 1",
      eventPhoto = "",
      start = LocalDateTime.now().plus(1, ChronoUnit.DAYS),
      end = LocalDateTime.now().plus(2, ChronoUnit.DAYS),
      location = Location(0.0, 0.0, "Test Location"),
      description = "Test Description",
      ticket = EventTicket("Test Ticket", 0.0, 1, 0),
      mainOrganiser = "1",
      organiserList = mutableListOf("Test Organiser"),
      attendeeList = mutableListOf("Test Attendee"),
      category = EventCategory.COMMUNITY,
      fireBaseID = "1")

  private val expiredEvent =
    Event(
      eventName = "Event 1",
      eventPhoto = "",
      start = LocalDateTime.now().minus(2, ChronoUnit.DAYS),
      end = LocalDateTime.now().minus(1, ChronoUnit.DAYS),
      location = Location(0.0, 0.0, "Test Location"),
      description = "Test Description",
      ticket = EventTicket("Test Ticket", 0.0, 1, 0),
      mainOrganiser = "1",
      organiserList = mutableListOf("Test Organiser"),
      attendeeList = mutableListOf("Test Attendee"),
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

  @Test
  fun testFilterExpired() = runTest {
    val event1 = mockEvent.copy(fireBaseID = "1")
    val event2 = expiredEvent.copy(fireBaseID = "2")
    val event3 = mockEvent.copy(fireBaseID = "3")

    eventRepository.addEvent(event1)
    eventRepository.addEvent(event2)
    eventRepository.addEvent(event3)

    val events = eventRepository.getEvents()
    assert(events is Resource.Success)
    assert((events as Resource.Success).data.size == 2)
  }

  @Test
  fun testGetEventsByIdsAllValid() = runTest {
    val event1 = mockEvent.copy(fireBaseID = "1")
    val event2 = mockEvent.copy(fireBaseID = "2")
    val event3 = mockEvent.copy(fireBaseID = "3")

    eventRepository.addEvent(event1)
    eventRepository.addEvent(event2)
    eventRepository.addEvent(event3)

    val events = eventRepository.getEventsByIds(listOf("1", "2", "3"))

    assert(events is Resource.Success)
    val eventsData = (events as Resource.Success).data
    assert(eventsData.size == 3)
    assert(eventsData.containsAll(listOf(event1, event2, event3)))
  }

  @Test
  fun testGetEventsByIdsWithSomeInvalid() = runTest {
    val event1 = mockEvent.copy(fireBaseID = "1")
    val event2 = mockEvent.copy(fireBaseID = "2")

    eventRepository.addEvent(event1)
    eventRepository.addEvent(event2)

    val events = eventRepository.getEventsByIds(listOf("1", "2", "4"))

    // Check that the response is a failure
    assert(events is Resource.Failure)
    assert((events as Resource.Failure).throwable.message == "Event with id 4 is missing")
  }

  @Test
  fun testGetEventsByIdsAllInvalid() = runTest {
    val events = eventRepository.getEventsByIds(listOf("100", "200"))

    // Check that the response is a failure
    assert(events is Resource.Failure)
    assert(
      (events as Resource.Failure).throwable.message ==
              "Event with id 100 is missing") // Shows id of first missing event
  }

  @Test
  fun testObserveAllEventsReflectsChanges() = runTest {
    val initialEvent = mockEvent.copy(fireBaseID = "1", eventName = "Initial Event")
    eventRepository.addEvent(initialEvent)

    val results = mutableListOf<Resource<List<Event>>>()
    val job = launch { eventRepository.observeAllEvents().toList(results) }

    val newEvent = mockEvent.copy(fireBaseID = "2", eventName = "New Event")
    eventRepository.addEvent(newEvent)

    (eventRepository as MockEventRepository).eventsFlow.value =
      Resource.Success((eventRepository as MockEventRepository).mockEvents.toList())

    delay(100)
    // Check if state holds both events
    assert(results[0] is Resource.Success && (results[0] as Resource.Success).data.size == 2)
    assert((results[0] as Resource.Success).data.containsAll(listOf(initialEvent, newEvent)))

    job.cancel()
  }

  @Test
  fun testObserveUpcomingEventsReflectsChanges() = runTest {
    val userId = "user1"
    val initialEvent =
      mockEvent.copy(
        fireBaseID = "1",
        eventName = "Initial Event User Attends",
        attendeeList = mutableListOf(userId))
    eventRepository.addEvent(initialEvent)

    // Start observing upcoming events
    val results = mutableListOf<Resource<List<Event>>>()
    val job = launch {
      (eventRepository as MockEventRepository).observeUpcomingEvents(userId).toList(results)
    }

    // Add a new upcoming event that the specified user will attend
    val newEvent =
      mockEvent.copy(
        fireBaseID = "2",
        eventName = "New Upcoming Event",
        attendeeList = mutableListOf(userId))
    // Add a new event that the specified user will not attend
    val newEvent2 =
      mockEvent.copy(fireBaseID = "3", eventName = "New Event", attendeeList = mutableListOf())
    eventRepository.addEvent(newEvent)
    eventRepository.addEvent(newEvent2)

    (eventRepository as MockEventRepository).eventsFlow.value =
      Resource.Success(listOf(initialEvent, newEvent, newEvent2))

    delay(100)

    // Assert that both the initial and new event are emitted
    assert(results[0] is Resource.Success && (results[0] as Resource.Success).data.size == 2)
    assert((results[0] as Resource.Success).data.containsAll(listOf(initialEvent, newEvent)))

    job.cancel()
  }
}