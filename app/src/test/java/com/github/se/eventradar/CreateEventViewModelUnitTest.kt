package com.github.se.eventradar

import android.net.Uri
import android.util.Log
import com.github.se.eventradar.model.Location
import com.github.se.eventradar.model.User
import com.github.se.eventradar.model.event.Event
import com.github.se.eventradar.model.event.EventCategory
import com.github.se.eventradar.model.event.EventTicket
import com.github.se.eventradar.model.repository.event.IEventRepository
import com.github.se.eventradar.model.repository.event.MockEventRepository
import com.github.se.eventradar.model.repository.location.ILocationRepository
import com.github.se.eventradar.model.repository.location.MockLocationRepository
import com.github.se.eventradar.model.repository.user.IUserRepository
import com.github.se.eventradar.model.repository.user.MockUserRepository
import com.github.se.eventradar.viewmodel.CreateEventUiState
import com.github.se.eventradar.viewmodel.CreateEventViewModel
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class CreateEventViewModelUnitTest {
  @RelaxedMockK lateinit var uri: Uri
  private lateinit var viewModel: CreateEventViewModel
  private lateinit var locationRepository: ILocationRepository
  private lateinit var eventRepository: IEventRepository
  private lateinit var userRepository: IUserRepository
  private lateinit var mockUiState: MutableStateFlow<CreateEventUiState>

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

  @get:Rule val mainDispatcherRule = CreateEventViewModelUnitTest.MainDispatcherRule()

  val test: MutableSet<String> = mutableSetOf("Test Organiser", "Organiser2")

  val mockUser =
      User(
          userId = "1",
          birthDate = "01/01/2000",
          email = "test@test.com",
          firstName = "John",
          lastName = "Doe",
          phoneNumber = "1234567890",
          accountStatus = "active",
          eventsAttendeeList = mutableListOf(),
          eventsHostList = mutableListOf(),
          friendsList = mutableListOf("2"),
          profilePicUrl = "",
          qrCodeUrl = "",
          bio = "",
          username = "john_doe")
  val mockUser2 =
      User(
          userId = "2",
          birthDate = "01/01/2002",
          email = "test2@test.com",
          firstName = "John",
          lastName = "Smith",
          phoneNumber = "123456789",
          accountStatus = "active",
          eventsAttendeeList = mutableListOf(),
          eventsHostList = mutableListOf(),
          friendsList = mutableListOf(),
          profilePicUrl = "",
          qrCodeUrl = "",
          bio = "",
          username = "john_smith")

  private val mockEvent =
      Event(
          eventName = "Event 1",
          eventPhoto = "",
          start = LocalDateTime.now(),
          end = LocalDateTime.now(),
          location =
              Location(
                  0.0,
                  0.0,
                  "École Polytechnique Fédérale de Lausanne"), // note: only the address will be
          // used for now.
          description = "Test Description",
          ticket = EventTicket("Test Ticket", 0.0, 1, 0),
          mainOrganiser = "1",
          organiserList = mutableListOf("1", "2"),
          attendeeList = mutableListOf("Test Attendee"),
          category = EventCategory.COMMUNITY,
          fireBaseID = "1")

  @Before
  fun setUp() {
    eventRepository = MockEventRepository()
    userRepository = MockUserRepository()
    locationRepository = MockLocationRepository()
    mockUiState = MutableStateFlow(CreateEventUiState())
    uri =
        mockk<Uri> {
          every { path } returns
              "content://media/picker/0/com.android.providers.media.photopicker/media/1000009885"
        }
    runBlocking {
      userRepository.addUser(mockUser)
      userRepository.addUser(mockUser2)
      (userRepository as MockUserRepository).updateCurrentUserId(mockUser.userId)
    }
    viewModel = CreateEventViewModel(locationRepository, eventRepository, userRepository)
  }

  @Test
  fun testGetHostFriendListSuccess() {
    viewModel.getHostFriendList(mockUiState)
    assert(mockUiState.value.hostFriendsList.map { user -> user.userId } == mockUser.friendsList)
  }

  @Test
  fun testGetHostFriendListFailure() {
    mockkStatic(Log::class)
    every { Log.d(any(), any()) } returns 0
    (userRepository as MockUserRepository).updateCurrentUserId(null)
    viewModel.getHostFriendList(mockUiState)
    Log.d("CreateEventViewModel", "User not logged in or error fetching user ID")
    assert(mockUiState.value.showAddEventFailure)
    unmockkAll()
  }

  @Test
  fun testResetStateAndSetAddEventSuccess() {
    viewModel.resetStateAndSetAddEventSuccess(true, mockUiState)
    assert(mockUiState.value.showAddEventSuccess)
  }

  @Test
  fun testEventImageUriChange() = runTest {
    viewModel.onEventPhotoUriChanged(uri, mockUiState)
    assert(mockUiState.value.eventPhotoUri == uri)
  }
  // integration test?
  @Test
  fun testCreateEventSuccessful() = runTest {
    mockkStatic(Log::class)
    every { Log.d(any(), any()) } returns 0
    val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val timeFormat = DateTimeFormatter.ofPattern("HH:mm")
    // test using MockUserRepository and MockEventRepository

    viewModel.onEventNameChanged(mockEvent.eventName, mockUiState)
    viewModel.onEventDescriptionChanged(mockEvent.description, mockUiState)
    viewModel.onStartDateChanged(mockEvent.start.format(dateFormat), mockUiState)
    viewModel.onStartTimeChanged(mockEvent.start.format(timeFormat), mockUiState)
    viewModel.onEndDateChanged(mockEvent.end.format(dateFormat), mockUiState)
    viewModel.onEndTimeChanged(mockEvent.end.format(timeFormat), mockUiState)
    viewModel.onEventCategoryChanged(mockEvent.category.toString(), mockUiState)
    viewModel.onOrganiserListChanged(listOf(mockUser2), mockUiState)

    viewModel.onLocationChanged(mockEvent.location.address, mockUiState)
    viewModel.onTicketNameChanged(mockEvent.ticket.name, mockUiState)
    viewModel.onTicketCapacityChanged(mockEvent.ticket.capacity.toString(), mockUiState)
    viewModel.onTicketPriceChanged(mockEvent.ticket.price.toString(), mockUiState)

    viewModel.onEventPhotoUriChanged(eventPhotoUri = uri, mockUiState)

    assert(viewModel.validateFields(mockUiState))
    runBlocking { viewModel.addEvent(mockUiState) } // ohh it reset the state?

    verify { Log.d("CreateEventViewModel", "Successfully added event") }
    verify {
      Log.d("CreateEventViewModel", "Successfully updated user ${mockUser.userId} host list")
    }
    verify {
      Log.d("CreateEventViewModel", "Successfully updated user ${mockUser2.userId} host list")
    }
    assert(mockUiState.value.eventName == mockEvent.eventName)
    assert(mockUiState.value.eventDescription == mockEvent.description)
    assert(mockUiState.value.startDate == mockEvent.start.format(dateFormat))
    assert(mockUiState.value.startTime == mockEvent.start.format(timeFormat))
    assert(mockUiState.value.endDate == mockEvent.end.format(dateFormat))
    assert(mockUiState.value.endTime == mockEvent.end.format(timeFormat))
    assert(mockUiState.value.location == mockEvent.location.address)
    assert(mockUiState.value.eventCategory == mockEvent.category.toString())
    assert(mockUiState.value.ticketCapacity.toInt() == mockEvent.ticket.capacity)
    assert(mockUiState.value.ticketPrice.toDouble() == mockEvent.ticket.price)
    assert(mockUiState.value.ticketName == mockEvent.ticket.name)
    assert(mockUiState.value.eventPhotoUri == uri)
    assert(mockUiState.value.organiserList.map { user -> user.userId } == listOf(mockUser2.userId))
    unmockkAll()
  }

  @Test
  fun testValidateFieldsFailsWithEmptyInput() = runTest {
    assert(viewModel.validateFields(mockUiState) == false)
  }

  @Test
  fun testCreateEventFailure() = runTest {
    (userRepository as MockUserRepository).updateCurrentUserId(null)
    mockkStatic(Log::class)
    every { Log.d(any(), any()) } returns 0
    val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val timeFormat = DateTimeFormatter.ofPattern("HH:mm")
    // test using MockUserRepository and MockEventRepository

    viewModel.onEventNameChanged(mockEvent.eventName, mockUiState)
    viewModel.onEventDescriptionChanged(mockEvent.description, mockUiState)
    viewModel.onStartDateChanged(mockEvent.start.format(dateFormat), mockUiState)
    viewModel.onStartTimeChanged(mockEvent.start.format(timeFormat), mockUiState)
    viewModel.onEndDateChanged(mockEvent.end.format(dateFormat), mockUiState)
    viewModel.onEndTimeChanged(mockEvent.end.format(timeFormat), mockUiState)
    viewModel.onEventCategoryChanged(mockEvent.category.toString(), mockUiState)
    viewModel.onOrganiserListChanged(listOf(mockUser2), mockUiState)

    viewModel.onLocationChanged(mockEvent.location.address, mockUiState)
    viewModel.onTicketNameChanged(mockEvent.ticket.name, mockUiState)
    viewModel.onTicketCapacityChanged(mockEvent.ticket.capacity.toString(), mockUiState)
    viewModel.onTicketPriceChanged(mockEvent.ticket.price.toString(), mockUiState)

    viewModel.onEventPhotoUriChanged(eventPhotoUri = uri, mockUiState)

    assert(viewModel.validateFields(mockUiState))
    runBlocking { viewModel.addEvent(mockUiState) }

    verify { Log.d("CreateEventViewModel", "User not logged in or error fetching user ID") }
    assert(mockUiState.value.eventName == "")
    assert(mockUiState.value.eventDescription == "")
    assert(mockUiState.value.startDate == "")
    assert(mockUiState.value.startTime == "")
    assert(mockUiState.value.endDate == "")
    assert(mockUiState.value.endTime == "")
    assert(mockUiState.value.location == "")
    assert(mockUiState.value.eventCategory == "")
    assert(mockUiState.value.ticketCapacity == "")
    assert(mockUiState.value.ticketPrice == "")
    assert(mockUiState.value.ticketName == "")
    assert(mockUiState.value.eventPhotoUri == null)
    assert(mockUiState.value.organiserList == emptyList<User>())
    unmockkAll()
  }

  @Test
  fun testCreateEventFailureUserRegisteredButNotInDatabase() = runTest {
    (userRepository as MockUserRepository).updateCurrentUserId(null)
    mockkStatic(Log::class)
    every { Log.d(any(), any()) } returns 0
    val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val timeFormat = DateTimeFormatter.ofPattern("HH:mm")
    // test using MockUserRepository and MockEventRepository

    (userRepository as MockUserRepository).updateCurrentUserId("non-existent-user")
    viewModel.onEventNameChanged(mockEvent.eventName, mockUiState)
    viewModel.onEventDescriptionChanged(mockEvent.description, mockUiState)
    viewModel.onStartDateChanged(mockEvent.start.format(dateFormat), mockUiState)
    viewModel.onStartTimeChanged(mockEvent.start.format(timeFormat), mockUiState)
    viewModel.onEndDateChanged(mockEvent.end.format(dateFormat), mockUiState)
    viewModel.onEndTimeChanged(mockEvent.end.format(timeFormat), mockUiState)
    viewModel.onEventCategoryChanged(mockEvent.category.toString(), mockUiState)
    viewModel.onOrganiserListChanged(listOf(mockUser2), mockUiState)

    viewModel.onLocationChanged(mockEvent.location.address, mockUiState)
    viewModel.onTicketNameChanged(mockEvent.ticket.name, mockUiState)
    viewModel.onTicketCapacityChanged(mockEvent.ticket.capacity.toString(), mockUiState)
    viewModel.onTicketPriceChanged(mockEvent.ticket.price.toString(), mockUiState)

    viewModel.onEventPhotoUriChanged(eventPhotoUri = uri, mockUiState)

    assert(viewModel.validateFields(mockUiState))
    runBlocking { viewModel.addEvent(mockUiState) }
    // Since Fetching Profile Picture Requires a User to be logged in, there will be an error
    // message
    verify { Log.d("CreateEventViewModel", "Fetching Profile Picture Error") }
    assert(mockUiState.value.eventName == "")
    assert(mockUiState.value.eventDescription == "")
    assert(mockUiState.value.startDate == "")
    assert(mockUiState.value.startTime == "")
    assert(mockUiState.value.endDate == "")
    assert(mockUiState.value.endTime == "")
    assert(mockUiState.value.location == "")
    assert(mockUiState.value.eventCategory == "")
    assert(mockUiState.value.ticketCapacity == "")
    assert(mockUiState.value.ticketPrice == "")
    assert(mockUiState.value.ticketName == "")
    assert(mockUiState.value.eventPhotoUri == null)
    assert(mockUiState.value.organiserList == emptyList<User>())
    unmockkAll()
  }

  @Test
  fun updateLocationListTestSuccess() {
    viewModel.onLocationChanged("EPFL", mockUiState)
    viewModel.updateListOfLocations(mockUiState)
    assert(mockUiState.value.listOfLocations == listOf(Location(100.0, 100.0, "EPFL")))
  }

  @Test
  fun updateLocationListTestFailure() {
    viewModel.onLocationChanged("", mockUiState)
    assert(mockUiState.value.locationIsError == false)
    viewModel.updateListOfLocations(mockUiState)
    assert(mockUiState.value.locationIsError == true)
  }

  @Test
  fun testResetStateAndSetShowAddEventFailure() = runTest {
    viewModel.resetStateAndSetAddEventFailure(true, mockUiState)
    assert(mockUiState.value.showAddEventFailure == true)
  }

  @Test
  fun testOnEventNameChanged() = runTest {
    val newName = "New Event Name"
    viewModel.onEventNameChanged(newName, mockUiState)
    assertEquals(newName, mockUiState.value.eventName)
  }

  @Test
  fun testOnEventCategoryChanged() = runTest {
    val newCategory = "New Event Category"
    viewModel.onEventCategoryChanged(newCategory, mockUiState)
    assertEquals(newCategory, mockUiState.value.eventCategory)
  }

  @Test
  fun testOnOrganiserListChanged() = runTest {
    val newOrganiserList = listOf(mockUser2)
    viewModel.onOrganiserListChanged(newOrganiserList, mockUiState)
    assertEquals(newOrganiserList, mockUiState.value.organiserList)
  }

  @Test
  fun testOnEventDescriptionChanged() = runTest {
    val newDescription = "New Event Description"
    viewModel.onEventDescriptionChanged(newDescription, mockUiState)
    assertEquals(newDescription, mockUiState.value.eventDescription)
  }

  @Test
  fun testOnStartDateChanged() = runTest {
    val newStartDate = "2024-05-25"
    viewModel.onStartDateChanged(newStartDate, mockUiState)
    assertEquals(newStartDate, mockUiState.value.startDate)
  }

  @Test
  fun testOnEndDateChanged() = runTest {
    val newEndDate = "2024-05-26"
    viewModel.onEndDateChanged(newEndDate, mockUiState)
    assertEquals(newEndDate, mockUiState.value.endDate)
  }

  @Test
  fun testOnStartTimeChanged() = runTest {
    val newStartTime = "10:00"
    viewModel.onStartTimeChanged(newStartTime, mockUiState)
    assertEquals(newStartTime, mockUiState.value.startTime)
  }

  @Test
  fun testOnEndTimeChanged() = runTest {
    val newEndTime = "18:00"
    viewModel.onEndTimeChanged(newEndTime, mockUiState)
    assertEquals(newEndTime, mockUiState.value.endTime)
  }

  @Test
  fun testOnLocationChanged() = runTest {
    val newLocation = "New Location"
    viewModel.onLocationChanged(newLocation, mockUiState)
    assertEquals(newLocation, mockUiState.value.location)
  }

  @Test
  fun testOnTicketNameChanged() = runTest {
    val newTicketName = "VIP Ticket"
    viewModel.onTicketNameChanged(newTicketName, mockUiState)
    assertEquals(newTicketName, mockUiState.value.ticketName)
  }

  @Test
  fun testOnTicketCapacityChanged() = runTest {
    val newTicketCapacity = "500"
    viewModel.onTicketCapacityChanged(newTicketCapacity, mockUiState)
    assertEquals(newTicketCapacity, mockUiState.value.ticketCapacity)
  }

  @Test
  fun testOnTicketPriceChanged() = runTest {
    val newTicketPrice = "50"
    viewModel.onTicketPriceChanged(newTicketPrice, mockUiState)
    assertEquals(newTicketPrice, mockUiState.value.ticketPrice)
  }
}
