package com.github.se.eventradar

import com.github.se.eventradar.model.Location
import com.github.se.eventradar.model.event.Event
import com.github.se.eventradar.model.event.EventCategory
import com.github.se.eventradar.model.event.EventDetailsViewModel
import com.github.se.eventradar.model.event.EventTicket
import com.github.se.eventradar.model.repository.event.IEventRepository
import com.github.se.eventradar.model.repository.event.MockEventRepository
import java.time.LocalDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@ExperimentalCoroutinesApi
class EventDetailsViewmodelUnitTest {
  private lateinit var viewModel: EventDetailsViewModel
  private lateinit var eventRepository: IEventRepository

  class MainDispatcherRule(
      private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
  ) : TestWatcher() {
    override fun starting(description: Description) {
      Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
      Dispatchers.resetMain()
    }
  }

  @get:Rule val mainDispatcherRule = MainDispatcherRule()

  private val mockEvent =
      Event(
          eventName = "Event 1",
          eventPhoto = "",
          start = LocalDateTime.MAX,
          end = LocalDateTime.MIN,
          location = Location(0.0, 0.0, "Test Location"),
          description = "Test Description",
          ticket = EventTicket("Test Ticket", 0.0, 1),
          mainOrganiser = "1",
          organiserList = setOf("Test Organiser", "Organiser2"),
          attendeeList = setOf("Test Attendee", "Attendee2"),
          category = EventCategory.COMMUNITY,
          fireBaseID = "1")

  @Before
  fun setUp() {
    eventRepository = MockEventRepository()
    viewModel = EventDetailsViewModel(eventRepository)
    viewModel.saveEventId(mockEvent.fireBaseID)
  }

  @Test
  fun testGetEvent() = runTest {
    eventRepository.addEvent(mockEvent)
    viewModel.getEventData()

    assert(viewModel.uiState.value.eventName == mockEvent.eventName)
  }

  /* @Test
  fun testGetEventsSuccess() = runTest {
    val events =
      listOf(
        mockEvent.copy(fireBaseID = "1"),
        mockEvent.copy(fireBaseID = "2"),
        mockEvent.copy(fireBaseID = "3"))

    events.forEach { event -> eventRepository.addEvent(event) }

    viewModel.getEvents()

    assert(viewModel.uiState.value.eventList.allEvents.isNotEmpty())
    assert(viewModel.uiState.value.eventList.allEvents.size == 3)
    assert(viewModel.uiState.value.eventList.allEvents == events)
    assert(viewModel.uiState.value.eventList.filteredEvents.size == 3)
    assert(viewModel.uiState.value.eventList.filteredEvents == events)
    Assert.assertNull(viewModel.uiState.value.eventList.selectedEvent)
  }

  @Test
  fun testGetUpcomingEventsSuccess() = runTest {
    val event1 = mockEvent.copy(fireBaseID = "1")
    val event2 = mockEvent.copy(fireBaseID = "2")
    val event3 = mockEvent.copy(fireBaseID = "3")

    eventRepository.addEvent(event1)
    eventRepository.addEvent(event2)
    eventRepository.addEvent(event3)

    userRepository.addUser(mockUser)
    // MockUser is on the attendeeList for events with id "1" and "2"
    viewModel.getUpcomingEvents("user1")

    assert(viewModel.uiState.value.eventList.allEvents.size == 2)
    assert(viewModel.uiState.value.eventList.allEvents == listOf(event1, event2))
    assert(viewModel.uiState.value.eventList.filteredEvents.size == 2)
    assert(viewModel.uiState.value.eventList.filteredEvents == listOf(event1, event2))
    Assert.assertNull(viewModel.uiState.value.eventList.selectedEvent)
  }

  @Test
  fun testGetUpcomingEventsWithEventsNotInRepo() = runTest {
    mockkStatic(Log::class)
    every { Log.d(any(), any()) } returns 0
    (userRepository as MockUserRepository).addUser(mockUser.copy(userId = "user2"))
    // MockUser is on the attendeeList for events with id "1" and "2" but these are not in the
    // eventRepository
    viewModel.getUpcomingEvents("user2")

    assert(viewModel.uiState.value.eventList.allEvents.isEmpty())
    assert(viewModel.uiState.value.eventList.filteredEvents.isEmpty())
    Assert.assertNull(viewModel.uiState.value.eventList.selectedEvent)

    verify { Log.d("EventsOverviewViewModel", "Error getting events for user2") }
    unmockkAll()
  }

  @Test
  fun testGetUpcomingEventsUserNotFound() = runTest {
    mockkStatic(Log::class)
    every { Log.d(any(), any()) } returns 0
    val userId = "userNotFound"

    viewModel.getUpcomingEvents(userId)

    assert(viewModel.uiState.value.eventList.allEvents.isEmpty())
    assert(viewModel.uiState.value.eventList.filteredEvents.isEmpty())
    Assert.assertNull(viewModel.uiState.value.eventList.selectedEvent)
    verify { Log.d("EventsOverviewViewModel", "Error fetching user document") }
    unmockkAll()
  }

  @Test
  fun testGetUpcomingEventsEmptyAttendeeList() = runTest {
    val userWithEmptyList =
      mockUser.copy(userId = "userWithEmptyList", eventsAttendeeList = emptyList())
    userRepository.addUser(userWithEmptyList)
    viewModel.getUpcomingEvents("userWithEmptyList")

    assert(viewModel.uiState.value.eventList.allEvents.isEmpty())
    assert(viewModel.uiState.value.eventList.filteredEvents.isEmpty())
    Assert.assertNull(viewModel.uiState.value.eventList.selectedEvent)
  }*/
}
