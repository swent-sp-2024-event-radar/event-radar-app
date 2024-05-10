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
          organiserSet = mutableSetOf("Test Organiser"),
          attendeeSet = mutableSetOf("Test Attendee"),
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
          eventsAttendeeSet = mutableSetOf("1", "2"),
          eventsHostSet = mutableSetOf("3"),
          friendsSet = mutableSetOf(),
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
    (userRepository as MockUserRepository).updateCurrentUserId("user1")
    viewModel.getUpcomingEvents()

    assert(viewModel.uiState.value.upcomingEventList.allEvents.size == 2)
    assert(viewModel.uiState.value.upcomingEventList.allEvents == listOf(event1, event2))
    assert(viewModel.uiState.value.upcomingEventList.filteredEvents.size == 2)
    assert(viewModel.uiState.value.upcomingEventList.filteredEvents == listOf(event1, event2))
    assertNull(viewModel.uiState.value.upcomingEventList.selectedEvent)
  }

  @Test
  fun testGetUpcomingEventsFilteredSuccess() = runTest {
    viewModel.onTabChanged(Tab.UPCOMING)
    val events =
        listOf(
            mockEvent.copy(eventName = "Event 1", fireBaseID = "1"),
            mockEvent.copy(eventName = "Event 2", fireBaseID = "2"),
            mockEvent.copy(eventName = "Event 3", fireBaseID = "3"))

    events.forEach { event -> eventRepository.addEvent(event) }

    userRepository.addUser(mockUser)
    // MockUser is on the attendeeList for events with id "1" and "2"
    (userRepository as MockUserRepository).updateCurrentUserId("user1")
    viewModel.getUpcomingEvents()

    val anotherQuery = "Event 1"
    viewModel.onSearchQueryChanged(anotherQuery)
    assert(anotherQuery == viewModel.uiState.value.searchQuery)

    viewModel.filterEvents()
    assert(viewModel.uiState.value.upcomingEventList.allEvents.size == 2)
    assert(
        viewModel.uiState.value.upcomingEventList.allEvents ==
            listOf(
                mockEvent.copy(eventName = "Event 1", fireBaseID = "1"),
                mockEvent.copy(eventName = "Event 2", fireBaseID = "2")))
    assert(viewModel.uiState.value.upcomingEventList.filteredEvents.size == 1)
    assert(
        viewModel.uiState.value.upcomingEventList.filteredEvents ==
            listOf(mockEvent.copy(eventName = "Event 1", fireBaseID = "1")))
    assertNull(viewModel.uiState.value.upcomingEventList.selectedEvent)
  }

  @Test
  fun testGetUpcomingEventsWithEventsNotInRepo() = runTest {
    mockkStatic(Log::class)
    every { Log.d(any(), any()) } returns 0
    userRepository.addUser(mockUser.copy(userId = "user2"))
    // MockUser is on the attendeeList for events with id "1" and "2" but these are not in the
    // eventRepository
    (userRepository as MockUserRepository).updateCurrentUserId("user2")
    viewModel.getUpcomingEvents()

    assert(viewModel.uiState.value.upcomingEventList.allEvents.isEmpty())
    assert(viewModel.uiState.value.upcomingEventList.filteredEvents.isEmpty())
    assertNull(viewModel.uiState.value.upcomingEventList.selectedEvent)

    verify { Log.d("EventsOverviewViewModel", "Error getting events for user2") }
    unmockkAll()
  }

  @Test
  fun testGetUpcomingEventsUserNotFound() = runTest {
    mockkStatic(Log::class)
    every { Log.d(any(), any()) } returns 0
    val userId = "userNotFound"
    (userRepository as MockUserRepository).updateCurrentUserId(userId)
    viewModel.getUpcomingEvents()

    assert(viewModel.uiState.value.upcomingEventList.allEvents.isEmpty())
    assert(viewModel.uiState.value.upcomingEventList.filteredEvents.isEmpty())
    assertNull(viewModel.uiState.value.upcomingEventList.selectedEvent)

    verify { Log.d("EventsOverviewViewModel", "Error fetching user document") }
    unmockkAll()
  }

  @Test
  fun testGetUpcomingEventsEmptyAttendeeList() = runTest {
    val userWithEmptyList =
        mockUser.copy(userId = "userWithEmptyList", eventsAttendeeSet = mutableSetOf())
    userRepository.addUser(userWithEmptyList)
    (userRepository as MockUserRepository).updateCurrentUserId("userWithEmptyList")
    viewModel.getUpcomingEvents()

    assert(viewModel.uiState.value.upcomingEventList.allEvents.isEmpty())
    assert(viewModel.uiState.value.upcomingEventList.filteredEvents.isEmpty())
    assertNull(viewModel.uiState.value.upcomingEventList.selectedEvent)
  }

  @Test
  fun testGetUpcomingEventsUserNotLoggedIn() = runTest {
    mockkStatic(Log::class)
    every { Log.d(any(), any()) } returns 0
    (userRepository as MockUserRepository).updateCurrentUserId(null) // no user logged in

    viewModel.getUpcomingEvents()

    assert(viewModel.uiState.value.upcomingEventList.allEvents.isEmpty())
    assert(viewModel.uiState.value.upcomingEventList.filteredEvents.isEmpty())
    assertNull(viewModel.uiState.value.upcomingEventList.selectedEvent)

    verify {
      Log.d("EventsOverviewViewModel", "Error fetching user ID: No user currently signed in")
    }
  }

  @Test
  fun testViewListChange() = runTest {
    viewModel.onViewListStatusChanged()
    assert(!viewModel.uiState.value.viewList)
    viewModel.onViewListStatusChanged()
    assert(viewModel.uiState.value.viewList)
  }

  @Test
  fun testTabChange() = runTest {
    viewModel.onTabChanged(tab = Tab.UPCOMING)
    assert(viewModel.uiState.value.tab == Tab.UPCOMING)
    viewModel.onTabChanged(tab = Tab.BROWSE)
    assert(viewModel.uiState.value.tab == Tab.BROWSE)
  }

  @Test
  fun testSetFilterDialogOpen() = runTest {
    // init value is false
    viewModel.onFilterDialogOpen()
    assert(viewModel.uiState.value.isFilterDialogOpen)

    viewModel.onFilterDialogOpen()
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

    viewModel.getEvents()

    val newQuery = "Event"
    viewModel.onSearchQueryChanged(newQuery)
    assert(newQuery == viewModel.uiState.value.searchQuery)

    viewModel.filterEvents()
    assert(viewModel.uiState.value.eventList.filteredEvents.isNotEmpty())
    assert(viewModel.uiState.value.eventList.filteredEvents.size == 3)
    assert(viewModel.uiState.value.eventList.filteredEvents == events)

    val anotherQuery = "Event 1"
    viewModel.onSearchQueryChanged(anotherQuery)
    assert(anotherQuery == viewModel.uiState.value.searchQuery)

    viewModel.filterEvents()
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

    viewModel.getEvents()

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

    viewModel.filterEvents()
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
                ticket = EventTicket("Test Ticket", 5.0, 1),
                fireBaseID = "2"),
            mockEvent.copy(eventName = "Event 3", fireBaseID = "3"))

    events.forEach { event -> eventRepository.addEvent(event) }

    viewModel.getEvents()

    viewModel.onFreeSwitchChanged()
    assert(viewModel.uiState.value.isFreeSwitchOn)

    val correctFilterEvents =
        listOf(
            mockEvent.copy(eventName = "Event 1", fireBaseID = "1"),
            mockEvent.copy(eventName = "Event 3", fireBaseID = "3"))

    viewModel.filterEvents()
    assert(viewModel.uiState.value.eventList.filteredEvents.isNotEmpty())
    assert(viewModel.uiState.value.eventList.filteredEvents.size == 2)
    assert(viewModel.uiState.value.eventList.filteredEvents == correctFilterEvents)
  }

  @Test
  fun testUserLoggedIn() = runTest {
    userRepository.addUser(mockUser)
    (userRepository as MockUserRepository).updateCurrentUserId("user1")
    viewModel.checkUserLoginStatus()
    assert(viewModel.uiState.value.userLoggedIn)
  }

  @Test
  fun testUserDefaultNotLoggedIn() = runTest {
    viewModel.checkUserLoginStatus()
    assert(!viewModel.uiState.value.userLoggedIn)
  }

  @Test
  fun testUserLoggedOut() = runTest {
    userRepository.addUser(mockUser)
    (userRepository as MockUserRepository).updateCurrentUserId("user1")
    viewModel.checkUserLoginStatus()
    (userRepository as MockUserRepository).updateCurrentUserId(null)
    viewModel.checkUserLoginStatus()
    assert(!viewModel.uiState.value.userLoggedIn)
  }
}
