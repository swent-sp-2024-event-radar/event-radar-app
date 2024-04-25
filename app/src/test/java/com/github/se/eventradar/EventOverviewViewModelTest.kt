package com.github.se.eventradar

import com.github.se.eventradar.model.*
import com.github.se.eventradar.model.event.Event
import com.github.se.eventradar.model.event.EventCategory
import com.github.se.eventradar.model.event.EventTicket
import com.github.se.eventradar.model.repository.event.IEventRepository
import com.github.se.eventradar.model.repository.event.MockEventRepository
import com.github.se.eventradar.model.repository.user.IUserRepository
import com.github.se.eventradar.model.repository.user.MockUserRepository
import java.time.LocalDateTime
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class EventsOverviewViewModelTest {

  private lateinit var viewModel: EventsOverviewViewModel
  private lateinit var eventRepository: IEventRepository
  private lateinit var userRepository: IUserRepository
  @OptIn(DelicateCoroutinesApi::class)
  private val mainThreadSurrogate = newSingleThreadContext("UI thread")
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

  private val mockUser =
      User(
          userId = "user1",
          age = 30,
          email = "test@example.com",
          firstName = "John",
          lastName = "Doe",
          phoneNumber = "1234567890",
          accountStatus = "active",
          eventsAttendeeList = listOf("1", "2"),
          eventsHostList = listOf("3"),
          profilePicUrl = "http://example.com/pic.jpg",
          qrCodeUrl = "http://example.com/qr.jpg",
          username = "johndoe")

  @Before
  fun setUp() {
    Dispatchers.setMain(mainThreadSurrogate)
    eventRepository = MockEventRepository()
    userRepository = MockUserRepository()
    viewModel = EventsOverviewViewModel(eventRepository, userRepository)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
    mainThreadSurrogate.close()
  }

  @Test
  fun testGetEventsEmpty() = runTest {
    // Assume MockEventRepository is set to return empty list
    viewModel.getEvents()

    // Allow any launched coroutines to complete
    advanceUntilIdle()

    // Assertions to validate the state after fetching events
    assert(viewModel.uiState.value.eventList.allEvents.isEmpty())
    assert(viewModel.uiState.value.eventList.filteredEvents.isEmpty())
    assertNull(viewModel.uiState.value.eventList.selectedEvent)
  }

  @Test
  fun testGetEventsSuccess() = runTest {
    val event1 = mockEvent.copy(fireBaseID = "1")
    val event2 = mockEvent.copy(fireBaseID = "2")
    val event3 = mockEvent.copy(fireBaseID = "3")

    eventRepository.addEvent(event1)
    eventRepository.addEvent(event2)
    eventRepository.addEvent(event3)
    val events = (eventRepository.getEvents() as Resource.Success).data
    viewModel.getEvents()
    advanceUntilIdle()
    assert(viewModel.uiState.value.eventList.allEvents.isNotEmpty())
    assert(viewModel.uiState.value.eventList.allEvents.size == 3)
    assert(
        viewModel.uiState.value.eventList.allEvents ==
            (eventRepository.getEvents() as Resource.Success).data)
    assert(viewModel.uiState.value.eventList.filteredEvents.size == 3)
    assert(
        viewModel.uiState.value.eventList.filteredEvents ==
            (eventRepository.getEvents() as Resource.Success).data)
    assertNull(viewModel.uiState.value.eventList.selectedEvent)
  }

  @Test
  fun testGetUpcomingEventsSuccessWithEvents() = runTest {
    val event1 = mockEvent.copy(fireBaseID = "1")
    val event2 = mockEvent.copy(fireBaseID = "2")
    val event3 = mockEvent.copy(fireBaseID = "3")

    eventRepository.addEvent(event1)
    eventRepository.addEvent(event2)
    eventRepository.addEvent(event3)

    userRepository.addUser(mockUser)

    viewModel.getUpcomingEvents("user1")
    advanceUntilIdle()
    assert(viewModel.uiState.value.eventList.allEvents.size == 2)
    assert(
        viewModel.uiState.value.eventList.allEvents ==
            (eventRepository.getEventsByIds(listOf("1", "2")) as Resource.Success).data)
    assert(viewModel.uiState.value.eventList.filteredEvents.size == 2)
    assert(
        viewModel.uiState.value.eventList.filteredEvents ==
            (eventRepository.getEventsByIds(listOf("1", "2")) as Resource.Success).data)
    assertNull(viewModel.uiState.value.eventList.selectedEvent)
  }

  @Test
  fun testGetUpcomingEventsSuccessNoEvents() = runTest {
    (userRepository as MockUserRepository).addUser(mockUser.copy(userId = "user2"))

    viewModel.getUpcomingEvents("user2")

    advanceUntilIdle()

    assert(viewModel.uiState.value.eventList.allEvents.isEmpty())
    assert(viewModel.uiState.value.eventList.filteredEvents.isEmpty())
    assertNull(viewModel.uiState.value.eventList.selectedEvent)
  }
}
