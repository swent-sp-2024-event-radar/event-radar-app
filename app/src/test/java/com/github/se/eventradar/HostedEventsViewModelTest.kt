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
          start = LocalDateTime.parse("2025-12-31T09:00:00"),
          end = LocalDateTime.parse("2025-01-01T00:00:00"),
          location = Location(0.0, 0.0, "Test Location"),
          description = "Test Description",
          ticket = EventTicket("Test Ticket", 0.0, 1, 0),
          mainOrganiser = "1",
          organiserList = mutableListOf("userid1"),
          attendeeList = mutableListOf("Test Attendee"),
          category = EventCategory.COMMUNITY,
          fireBaseID = "eventId1")

  private val mockUser =
      User(
          userId = "userid1",
          birthDate = "01.01.2000",
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
          bio = "",
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
    (userRepository as MockUserRepository).updateCurrentUserId(null)
    viewModel.getHostedEvents()
    verify { Log.d("HostedEventsViewModel", "User not logged in or error fetching user ID") }
    assert(viewModel.uiState.value.eventList.allEvents.isEmpty())
    assert(viewModel.uiState.value.eventList.filteredEvents.isEmpty())
    Assert.assertNull(viewModel.uiState.value.eventList.selectedEvent)
    unmockkAll()
  }

  @Test
  fun testGetHostedEventsEmpty() = runTest {
    userRepository.addUser(mockUser)
    (userRepository as MockUserRepository).updateCurrentUserId(mockUser.userId)
    viewModel.getHostedEvents()
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

    val listOfEventIds = events.map { event -> event.fireBaseID }.toMutableList()
    val userWithHostedEvent = mockUser.copy(eventsHostList = listOfEventIds)
    userRepository.addUser(userWithHostedEvent)
    (userRepository as MockUserRepository).updateCurrentUserId(userWithHostedEvent.userId)
    viewModel.getHostedEvents()
    assert(viewModel.uiState.value.eventList.allEvents.isNotEmpty())
    assert(viewModel.uiState.value.eventList.allEvents.size == 3)
    assert(viewModel.uiState.value.eventList.allEvents.containsAll(events))
    assert(viewModel.uiState.value.eventList.filteredEvents.size == 3)
    assert(viewModel.uiState.value.eventList.filteredEvents.containsAll(events))
    Assert.assertNull(viewModel.uiState.value.eventList.selectedEvent)
  }

  @Test
  fun testGetHostedEventsFilteredSuccess() = runTest {
    val events =
        listOf(
            mockEvent.copy(eventName = "Event 1", fireBaseID = "1"),
            mockEvent.copy(eventName = "Event 2", fireBaseID = "2"),
            mockEvent.copy(eventName = "Event 3", fireBaseID = "3"))
    events.forEach { event -> eventRepository.addEvent(event) }

    val listOfEventIds = events.map { event -> event.fireBaseID }.toMutableList()
    val userWithHostedEvent = mockUser.copy(eventsHostList = listOfEventIds)
    userRepository.addUser(userWithHostedEvent)
    (userRepository as MockUserRepository).updateCurrentUserId(userWithHostedEvent.userId)
    viewModel.getHostedEvents()

    val anotherQuery = "Event 1"
    viewModel.onSearchQueryChanged(anotherQuery)
    assert(anotherQuery == viewModel.uiState.value.searchQuery)

    viewModel.filterHostedEvents()

    assert(viewModel.uiState.value.eventList.allEvents.isNotEmpty())
    assert(viewModel.uiState.value.eventList.allEvents.size == 3)
    assert(viewModel.uiState.value.eventList.allEvents.containsAll(events))
    assert(viewModel.uiState.value.eventList.filteredEvents.size == 1)
    assert(
        viewModel.uiState.value.eventList.filteredEvents ==
            listOf(mockEvent.copy(eventName = "Event 1", fireBaseID = "1")))
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
    val listOfEventIds = events.map { event -> event.fireBaseID }.toMutableList()
    val userWithHostedEvent = mockUser.copy(eventsHostList = listOfEventIds)
    userRepository.addUser(userWithHostedEvent)
    (userRepository as MockUserRepository).updateCurrentUserId(userWithHostedEvent.userId)
    viewModel.getHostedEvents()
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
    (userRepository as MockUserRepository).updateCurrentUserId(userId)
    viewModel.getHostedEvents()
    assert(viewModel.uiState.value.eventList.allEvents.isEmpty())
    assert(viewModel.uiState.value.eventList.filteredEvents.isEmpty())
    Assert.assertNull(viewModel.uiState.value.eventList.selectedEvent)
    verify { Log.d("HostedEventsViewModel", "Error fetching user document") }
    unmockkAll()
  }

  @Test
  fun testViewListChange() = runTest {
    viewModel.onViewListStatusChanged()
    assert(!viewModel.uiState.value.viewList)
    viewModel.onViewListStatusChanged()
    assert(viewModel.uiState.value.viewList)
  }

  @Test
  fun testSetFilterDialogOpen() = runTest {
    // init value is false
    viewModel.onFilterDialogOpenChanged()
    assert(viewModel.uiState.value.isFilterDialogOpen)

    viewModel.onFilterDialogOpenChanged()
    assert(!viewModel.uiState.value.isFilterDialogOpen)
  }

  @Test
  fun testOnSearchQueryChange() = runTest {
    val newQuery = "sample search"
    viewModel.onSearchQueryChanged(newQuery)
    assert(newQuery == viewModel.uiState.value.searchQuery)

    val anotherQuery = "another search"
    viewModel.onSearchQueryChanged(anotherQuery)
    assert(anotherQuery == viewModel.uiState.value.searchQuery)
  }

  @Test
  fun testOnSearchActiveChange() = runTest {
    viewModel.onSearchActiveChanged(true)
    assert(viewModel.uiState.value.isSearchActive)

    viewModel.onSearchActiveChanged(false)
    assert(!viewModel.uiState.value.isSearchActive)
  }

  @Test
  fun testOnRadiusQueryChange() = runTest {
    val newQuery = "10"
    viewModel.onRadiusQueryChanged(newQuery)
    assert(newQuery == viewModel.uiState.value.radiusQuery)

    val anotherQuery = "5"
    viewModel.onRadiusQueryChanged(anotherQuery)
    assert(anotherQuery == viewModel.uiState.value.radiusQuery)
  }

  @Test
  fun testOnFreeSwitchChange() = runTest {
    // init value is false
    viewModel.onFreeSwitchChanged()
    assert(viewModel.uiState.value.isFreeSwitchOn)

    viewModel.onFreeSwitchChanged()
    assert(!viewModel.uiState.value.isFreeSwitchOn)
  }

  @Test
  fun testOnFilterApply() = runTest {
    // init value is false
    viewModel.onFilterApply()
    assert(viewModel.uiState.value.isFilterActive)

    viewModel.onFilterApply()
    assert(viewModel.uiState.value.isFilterActive)
  }

  @Test
  fun testFilterEventsSearchSuccess() = runTest {
    val events =
        listOf(
            mockEvent.copy(eventName = "Event 1", fireBaseID = "1"),
            mockEvent.copy(eventName = "Event 2", fireBaseID = "2"),
            mockEvent.copy(eventName = "Event 3", fireBaseID = "3"))
    events.forEach { event -> eventRepository.addEvent(event) }

    val listOfEventIds = events.map { event -> event.fireBaseID }.toMutableList()
    val userWithHostedEvent = mockUser.copy(eventsHostList = listOfEventIds)
    userRepository.addUser(userWithHostedEvent)
    (userRepository as MockUserRepository).updateCurrentUserId(userWithHostedEvent.userId)
    viewModel.getHostedEvents()

    val newQuery = "Event"
    viewModel.onSearchQueryChanged(newQuery)
    assert(newQuery == viewModel.uiState.value.searchQuery)

    viewModel.filterHostedEvents()
    assert(viewModel.uiState.value.eventList.filteredEvents.isNotEmpty())
    assert(viewModel.uiState.value.eventList.filteredEvents.size == 3)
    assert(viewModel.uiState.value.eventList.filteredEvents == events)

    val anotherQuery = "Event 1"
    viewModel.onSearchQueryChanged(anotherQuery)
    assert(anotherQuery == viewModel.uiState.value.searchQuery)

    viewModel.filterHostedEvents()
    assert(viewModel.uiState.value.eventList.filteredEvents.isNotEmpty())
    assert(viewModel.uiState.value.eventList.filteredEvents.size == 1)
    assert(
        viewModel.uiState.value.eventList.filteredEvents ==
            listOf(mockEvent.copy(fireBaseID = "1")))
  }

  @Test
  fun testFilterEventsRadiusSuccess() = runTest {
    val events =
        listOf(
            mockEvent.copy(eventName = "Event 1", fireBaseID = "1"),
            mockEvent.copy(
                eventName = "Event 2",
                location = Location(38.92, 78.78, "Test Location near user"),
                fireBaseID = "2"),
            mockEvent.copy(
                eventName = "Event 3",
                location = Location(38.8, 78.7, "Test Location near user 2"),
                fireBaseID = "3"))
    events.forEach { event -> eventRepository.addEvent(event) }

    val listOfEventIds = events.map { event -> event.fireBaseID }.toMutableList()
    val userWithHostedEvent = mockUser.copy(eventsHostList = listOfEventIds)
    userRepository.addUser(userWithHostedEvent)
    (userRepository as MockUserRepository).updateCurrentUserId(userWithHostedEvent.userId)
    viewModel.getHostedEvents()
    viewModel.onUserLocationChanged(Location(38.9, 78.8, "User Location"))

    val newQuery = "20"
    viewModel.onRadiusQueryChanged(newQuery)
    assert(newQuery == viewModel.uiState.value.radiusQuery)

    val correctFilterEvents =
        listOf(
            mockEvent.copy(
                eventName = "Event 2",
                location = Location(38.92, 78.78, "Test Location near user"),
                fireBaseID = "2"),
            mockEvent.copy(
                eventName = "Event 3",
                location = Location(38.8, 78.7, "Test Location near user 2"),
                fireBaseID = "3"))

    viewModel.filterHostedEvents()
    assert(viewModel.uiState.value.eventList.filteredEvents.isNotEmpty())
    assert(viewModel.uiState.value.eventList.filteredEvents.size == 2)
    assert(viewModel.uiState.value.eventList.filteredEvents == correctFilterEvents)
  }

  @Test
  fun testFilterEventsFreeSuccess() = runTest {
    val events =
        listOf(
            mockEvent.copy(eventName = "Event 1", fireBaseID = "1"),
            mockEvent.copy(
                eventName = "Event 2",
                ticket = EventTicket("Test Ticket", 5.0, 1, 0),
                fireBaseID = "2"),
            mockEvent.copy(eventName = "Event 3", fireBaseID = "3"))

    events.forEach { event -> eventRepository.addEvent(event) }

    val listOfEventIds = events.map { event -> event.fireBaseID }.toMutableList()
    val userWithHostedEvent = mockUser.copy(eventsHostList = listOfEventIds)
    userRepository.addUser(userWithHostedEvent)
    (userRepository as MockUserRepository).updateCurrentUserId(userWithHostedEvent.userId)
    viewModel.getHostedEvents()

    viewModel.onFreeSwitchChanged()
    assert(viewModel.uiState.value.isFreeSwitchOn)

    val correctFilterEvents =
        listOf(
            mockEvent.copy(eventName = "Event 1", fireBaseID = "1"),
            mockEvent.copy(eventName = "Event 3", fireBaseID = "3"))

    viewModel.filterHostedEvents()
    assert(viewModel.uiState.value.eventList.filteredEvents.isNotEmpty())
    assert(viewModel.uiState.value.eventList.filteredEvents.size == 2)
    assert(viewModel.uiState.value.eventList.filteredEvents == correctFilterEvents)
  }

  @Test
  fun testRadiusQueryLessThanZero() = runTest {
    mockkStatic(Log::class)
    every { Log.d(any(), any()) } returns 0

    val newQuery = "-10.0"
    viewModel.onRadiusQueryChanged(newQuery)
    assert(newQuery == viewModel.uiState.value.radiusQuery)
    viewModel.filterHostedEvents()

    verify { Log.d("HostedEventsViewModel", "Invalid radius query: $newQuery") }
    assert(viewModel.uiState.value.radiusQuery == "")
    unmockkAll()
  }
}
