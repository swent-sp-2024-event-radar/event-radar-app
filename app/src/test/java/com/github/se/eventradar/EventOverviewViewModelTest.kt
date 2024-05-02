package com.github.se.eventradar

import android.util.Log
import com.github.se.eventradar.model.Location
import com.github.se.eventradar.model.User
import com.github.se.eventradar.model.event.Event
import com.github.se.eventradar.model.event.EventCategory
import com.github.se.eventradar.model.event.EventTicket
import com.github.se.eventradar.model.repository.event.IEventRepository
import com.github.se.eventradar.model.repository.event.MockEventRepository
import com.github.se.eventradar.model.repository.user.IUserRepository
import com.github.se.eventradar.model.repository.user.MockUserRepository
import com.github.se.eventradar.viewmodel.EventsOverviewViewModel
import com.github.se.eventradar.viewmodel.Tab
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import java.time.LocalDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@ExperimentalCoroutinesApi
class EventsOverviewViewModelTest {

  private lateinit var viewModel: EventsOverviewViewModel
  private lateinit var eventRepository: IEventRepository
  private lateinit var userRepository: IUserRepository

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
          start = LocalDateTime.now(),
          end = LocalDateTime.now(),
          location = Location(0.0, 0.0, "Test Location"),
          description = "Test Description",
          ticket = EventTicket("Test Ticket", 0.0, 1),
          mainOrganiser = "1",
          organiserList = mutableListOf("Test Organiser"),
          attendeeList = mutableListOf("Test Attendee"),
          category = EventCategory.COMMUNITY,
          fireBaseID = "1")

  private val mockUser =
      User(
          userId = "user1",
          birthDate = "01/01/2000",
          email = "test@example.com",
          firstName = "John",
          lastName = "Doe",
          phoneNumber = "1234567890",
          accountStatus = "active",
          eventsAttendeeSet = mutableListOf("1", "2"),
          eventsHostSet = mutableListOf("3"),
          friendsSet = mutableListOf(),
          profilePicUrl = "http://example.com/pic.jpg",
          qrCodeUrl = "http://example.com/qr.jpg",
          username = "john_doe")

  @Before
  fun setUp() {
    eventRepository = MockEventRepository()
    userRepository = MockUserRepository()
    viewModel = EventsOverviewViewModel(eventRepository, userRepository)
  }

  @Test
  fun testGetEventsEmpty() = runTest {
    viewModel.getEvents()

    assert(viewModel.uiState.value.eventList.allEvents.isEmpty())
    assert(viewModel.uiState.value.eventList.filteredEvents.isEmpty())
    assertNull(viewModel.uiState.value.eventList.selectedEvent)
  }

  @Test
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
    assertNull(viewModel.uiState.value.eventList.selectedEvent)
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
    assertNull(viewModel.uiState.value.eventList.selectedEvent)
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
    assertNull(viewModel.uiState.value.eventList.selectedEvent)

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
    assertNull(viewModel.uiState.value.eventList.selectedEvent)
    verify { Log.d("EventsOverviewViewModel", "Error fetching user document") }
    unmockkAll()
  }

  @Test
  fun testGetUpcomingEventsEmptyAttendeeList() = runTest {
    val userWithEmptyList =
        mockUser.copy(userId = "userWithEmptyList", eventsAttendeeSet = mutableListOf())
    userRepository.addUser(userWithEmptyList)
    viewModel.getUpcomingEvents("userWithEmptyList")

    assert(viewModel.uiState.value.eventList.allEvents.isEmpty())
    assert(viewModel.uiState.value.eventList.filteredEvents.isEmpty())
    assertNull(viewModel.uiState.value.eventList.selectedEvent)
  }

  @Test
  fun testViewListChange() = runTest {
    viewModel.onViewListStatusChanged()
    assert(viewModel.uiState.value.viewList.equals(false))
    viewModel.onViewListStatusChanged()
    assert(viewModel.uiState.value.viewList.equals(true))
  }

  @Test
  fun testTabChange() = runTest {
    viewModel.onTabChanged(tab = Tab.UPCOMING)
    assert(viewModel.uiState.value.tab == Tab.UPCOMING)
    viewModel.onTabChanged(tab = Tab.BROWSE)
    assert(viewModel.uiState.value.tab == Tab.BROWSE)
  }
}
