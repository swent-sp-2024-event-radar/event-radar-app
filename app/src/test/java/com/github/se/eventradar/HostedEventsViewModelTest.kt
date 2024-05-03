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
import com.github.se.eventradar.viewmodel.HostedEventsViewModel
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
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@ExperimentalCoroutinesApi
class HostedEventsViewModelTest {
  private lateinit var viewModel: HostedEventsViewModel
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
          organiserList = mutableListOf("userid1"),
          attendeeList = mutableListOf("Test Attendee"),
          category = EventCategory.COMMUNITY,
          fireBaseID = "eventId1")

  private val mockUser =
      User(
          userId = "userid1",
          birthDate = "01/01/2000",
          email = "test@example.com",
          firstName = "John",
          lastName = "Doe",
          phoneNumber = "1234567890",
          accountStatus = "active",
          eventsAttendeeList = mutableListOf("userId1", "userId2"),
          eventsHostList = mutableListOf(),
          friendsList = mutableListOf(),
          profilePicUrl = "http://example.com/pic.jpg",
          qrCodeUrl = "http://example.com/qr.jpg",
          username = "john_doe")

  @Before
  fun setUp() {
    eventRepository = MockEventRepository()
    userRepository = MockUserRepository()
    viewModel = HostedEventsViewModel(eventRepository, userRepository)
  }

  @Test
  fun testAddUserFalseCase() = runTest {
    mockkStatic(Log::class)
    every { Log.d(any(), any()) } returns 0
    viewModel.getHostedEvents(null)
    verify { Log.d("HostedEventsViewModel", "User not logged in") }
    unmockkAll()
  }

  @Test
  fun testGetHostedEventsEmpty() = runTest {
    userRepository.addUser(mockUser)
    viewModel.getHostedEvents(mockUser.userId)
    assert(viewModel.uiState.value.eventList.allEvents.isEmpty())
    assert(viewModel.uiState.value.eventList.filteredEvents.isEmpty())
    Assert.assertNull(viewModel.uiState.value.eventList.selectedEvent)
  }

  @Test
  fun testGetHostedEventsSuccess() = runTest {
    val events =
        mutableListOf(
            mockEvent.copy(fireBaseID = "eventId1"),
            mockEvent.copy(fireBaseID = "eventId2"),
            mockEvent.copy(fireBaseID = "eventId3"))
    events.forEach { event -> eventRepository.addEvent(event) }

    val ListOfEventIds = events.map { event -> event.fireBaseID }.toMutableList()
    val userWithHostedEvent = mockUser.copy(eventsHostList = ListOfEventIds)
    userRepository.addUser(userWithHostedEvent)

    viewModel.getHostedEvents(userWithHostedEvent.userId)
    assert(viewModel.uiState.value.eventList.allEvents.isNotEmpty())
    assert(viewModel.uiState.value.eventList.allEvents.size == 3)
    assert(viewModel.uiState.value.eventList.allEvents.containsAll(events))
    assert(viewModel.uiState.value.eventList.filteredEvents.size == 3)
    assert(viewModel.uiState.value.eventList.filteredEvents.containsAll(events))
    Assert.assertNull(viewModel.uiState.value.eventList.selectedEvent)
  }

  @Test
  fun testGetHostedEventsWithEventsNotInRepo() = runTest {
    mockkStatic(Log::class)
    every { Log.d(any(), any()) } returns 0
    val events =
        mutableListOf(
            mockEvent.copy(fireBaseID = "eventId1"),
            mockEvent.copy(fireBaseID = "eventId2"),
            mockEvent.copy(fireBaseID = "eventId3"))
    // event is not added to repo.
    val ListOfEventIds = events.map { event -> event.fireBaseID }.toMutableList()
    val userWithHostedEvent = mockUser.copy(eventsHostList = ListOfEventIds)
    userRepository.addUser(userWithHostedEvent)
    viewModel.getHostedEvents(userWithHostedEvent.userId)
    assert(viewModel.uiState.value.eventList.allEvents.isEmpty())
    assert(viewModel.uiState.value.eventList.filteredEvents.isEmpty())
    Assert.assertNull(viewModel.uiState.value.eventList.selectedEvent)
    verify {
      Log.d(
          "HostedEventsViewModel", "Error getting hosted events for ${userWithHostedEvent.userId}")
    }
    unmockkAll()
  }

  @Test
  fun testGetHostedEventsUserNotFound() = runTest {
    mockkStatic(Log::class)
    every { Log.d(any(), any()) } returns 0
    val userId = "userNotFound"
    viewModel.getHostedEvents(userId)
    assert(viewModel.uiState.value.eventList.allEvents.isEmpty())
    assert(viewModel.uiState.value.eventList.filteredEvents.isEmpty())
    Assert.assertNull(viewModel.uiState.value.eventList.selectedEvent)
    verify { Log.d("HostedEventsViewModel", "Error fetching user document") }
    unmockkAll()
  }

  @Test
  fun testViewListChange() = runTest {
    viewModel.onViewListStatusChanged()
    assert(viewModel.uiState.value.viewList.equals(false))
    viewModel.onViewListStatusChanged()
    assert(viewModel.uiState.value.viewList.equals(true))
  }
}
