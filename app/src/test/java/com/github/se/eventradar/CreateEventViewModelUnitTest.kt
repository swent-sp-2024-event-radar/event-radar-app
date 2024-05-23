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
          friendsList = mutableListOf(),
          profilePicUrl = "",
          qrCodeUrl = "",
          bio = "",
          username = "john_doe")

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
    runBlocking { userRepository.addUser(mockUser) }
    runBlocking { (userRepository as MockUserRepository).updateCurrentUserId(mockUser.userId) }
    viewModel = CreateEventViewModel(locationRepository, eventRepository, userRepository)
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
    viewModel.onEventCategoryChanged(mockEvent.category.displayName, mockUiState)
    viewModel.onOrganiserListChanged(mockEvent.organiserList, mockUiState)

    viewModel.onLocationChanged(mockEvent.location.address, mockUiState)
    viewModel.onTicketNameChanged(mockEvent.ticket.name, mockUiState)
    viewModel.onTicketCapacityChanged(mockEvent.ticket.capacity.toString(), mockUiState)
    viewModel.onTicketPriceChanged(mockEvent.ticket.price.toString(), mockUiState)

    viewModel.onEventPhotoUriChanged(eventPhotoUri = uri, mockUiState)

    assert(viewModel.validateFields(mockUiState))
    runBlocking { viewModel.addEvent(mockUiState) }

    verify { Log.d("CreateEventViewModel", "Successfully added event") }
    verify {
      Log.d("CreateEventViewModel", "Successfully updated user ${mockUser.userId} host list")
    }
    assert(mockUiState.value.eventName == mockEvent.eventName)
    assert(mockUiState.value.eventDescription == mockEvent.description)
    assert(mockUiState.value.startDate == mockEvent.start.format(dateFormat))
    assert(mockUiState.value.startTime == mockEvent.start.format(timeFormat))
    assert(mockUiState.value.endDate == mockEvent.end.format(dateFormat))
    assert(mockUiState.value.endTime == mockEvent.end.format(timeFormat))
    assert(mockUiState.value.location == mockEvent.location.address)
    assert(mockUiState.value.eventCategory == mockEvent.category.displayName)
    assert(mockUiState.value.ticketCapacity.toInt() == mockEvent.ticket.capacity)
    assert(mockUiState.value.ticketPrice.toDouble() == mockEvent.ticket.price)
    assert(mockUiState.value.ticketName == mockEvent.ticket.name)
    assert(mockUiState.value.eventPhotoUri == uri)
    assert(mockUiState.value.organiserList == mockEvent.organiserList)
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
    viewModel.onEventCategoryChanged(mockEvent.category.displayName, mockUiState)
    viewModel.onOrganiserListChanged(mockEvent.organiserList, mockUiState)

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
    assert(mockUiState.value.organiserList == emptyList<String>())
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
    viewModel.onEventCategoryChanged(mockEvent.category.displayName, mockUiState)
    viewModel.onOrganiserListChanged(mockEvent.organiserList, mockUiState)

    viewModel.onLocationChanged(mockEvent.location.address, mockUiState)
    viewModel.onTicketNameChanged(mockEvent.ticket.name, mockUiState)
    viewModel.onTicketCapacityChanged(mockEvent.ticket.capacity.toString(), mockUiState)
    viewModel.onTicketPriceChanged(mockEvent.ticket.price.toString(), mockUiState)

    viewModel.onEventPhotoUriChanged(eventPhotoUri = uri, mockUiState)

    assert(viewModel.validateFields(mockUiState))
    runBlocking { viewModel.addEvent(mockUiState) }

    verify { Log.d("CreateEventViewModel", "Failed to find user non-existent-user in database") }
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
    assert(mockUiState.value.organiserList == emptyList<String>())
    unmockkAll()
  }
}
