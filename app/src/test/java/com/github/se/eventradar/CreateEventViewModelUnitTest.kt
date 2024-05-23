package com.github.se.eventradar

import android.net.Uri
import android.util.Log
import com.github.se.eventradar.model.Location
import com.github.se.eventradar.model.Resource
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
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
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
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class CreateEventViewModelUnitTest {
    @RelaxedMockK
    lateinit var uri: Uri
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

    @get:Rule
    val mainDispatcherRule = CreateEventViewModelUnitTest.MainDispatcherRule()

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
            location = Location(0.0, 0.0, "École Polytechnique Fédérale de Lausanne"), //note: only the address will be used for now.
            description = "Test Description",
            ticket = EventTicket("Test Ticket", 0.0, 1, 0),
            mainOrganiser = "1",
            organiserList = mutableListOf(),
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
        runBlocking {userRepository.addUser(mockUser)}
        runBlocking{(userRepository as MockUserRepository).updateCurrentUserId(mockUser.userId)}
        viewModel = CreateEventViewModel(locationRepository, eventRepository, userRepository)
    }
    //integration test?
    @Test
    fun testCreateEventSuccessful() = runTest {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        val dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val timeFormat = DateTimeFormatter.ofPattern("HH:mm")
        //test using MockUserRepository and MockEventRepository

        viewModel.onEventNameChanged(mockEvent.eventName, mockUiState)
        viewModel.onEventDescriptionChanged(mockEvent.description, mockUiState)
        viewModel.onStartDateChanged(mockEvent.start.format(dateFormat), mockUiState)
        viewModel.onStartTimeChanged(mockEvent.start.format(timeFormat), mockUiState)
        viewModel.onEndDateChanged(mockEvent.end.format(dateFormat), mockUiState)
        viewModel.onEndTimeChanged(mockEvent.end.format(timeFormat), mockUiState)

        viewModel.onLocationChanged(mockEvent.location.address, mockUiState)
        viewModel.onTicketNameChanged(mockEvent.ticket.name, mockUiState)
        viewModel.onTicketCapacityChanged(mockEvent.ticket.capacity.toString(), mockUiState)
        viewModel.onTicketPriceChanged(mockEvent.ticket.price.toString(), mockUiState)
        viewModel.onEventPhotoUriChanged(eventPhotoUri = Uri.EMPTY, mockUiState)

        assert(viewModel.validateFields(mockUiState))
        runBlocking {viewModel.addEvent()}


        //verify{Log.d("CreateEventViewModel", "Successfully added event")}
        //verify {Log.d("CreateEventViewModel", "Successfully updated user ${mockUser.userId} host list")}
        assert(mockUiState.value.eventName == mockEvent.eventName)
        assert(mockUiState.value.eventDescription == mockEvent.description)
        assert(mockUiState.value.startDate == mockEvent.start.format(dateFormat))
        assert(mockUiState.value.startTime == mockEvent.start.format(timeFormat))
        assert(mockUiState.value.endDate == mockEvent.end.format(dateFormat))
        assert(mockUiState.value.endTime == mockEvent.end.format(timeFormat))
        assert(mockUiState.value.location == mockEvent.location.address)
        assert(mockUiState.value.ticketCapacity.toInt() == mockEvent.ticket.capacity)
        assert(mockUiState.value.ticketPrice.toDouble() == mockEvent.ticket.price)
        assert(mockUiState.value.ticketName == mockEvent.ticket.name)
        assert(mockUiState.value.eventCategory == mockEvent.category)
        assert(mockUiState.value.eventPhotoUri == Uri.EMPTY)
        unmockkAll()
    }
}