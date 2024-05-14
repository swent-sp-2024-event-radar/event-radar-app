package com.github.se.eventradar

import android.util.Log
import com.github.se.eventradar.model.Location
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.User
import com.github.se.eventradar.model.event.Event
import com.github.se.eventradar.model.event.EventCategory
import com.github.se.eventradar.model.event.EventTicket
import com.github.se.eventradar.model.repository.event.IEventRepository
import com.github.se.eventradar.model.repository.event.MockEventRepository
import com.github.se.eventradar.model.repository.user.IUserRepository
import com.github.se.eventradar.model.repository.user.MockUserRepository
import com.github.se.eventradar.viewmodel.qrCode.QrCodeAnalyser
import com.github.se.eventradar.viewmodel.qrCode.ScanTicketQrViewModel
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import java.time.LocalDateTime
import junit.framework.TestCase
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@ExperimentalCoroutinesApi
class ScanFriendTicketViewModelTest {
    private lateinit var viewModel: ScanTicketQrViewModel
    private lateinit var userRepository: IUserRepository
    private lateinit var eventRepository: IEventRepository
    private lateinit var qrCodeAnalyser: QrCodeAnalyser

    private val myUID = "user1"

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
            attendeeList = mutableListOf("user1", "user2", "user3"),
            category = EventCategory.COMMUNITY,
            fireBaseID = "1"
        )

    private val mockUser1 =
        User(
            userId = "user1",
            birthDate = "01/01/2000",
            email = "test@example.com",
            firstName = "John",
            lastName = "Doe",
            phoneNumber = "1234567890",
            accountStatus = "active",
            eventsAttendeeList = mutableListOf("1", "2", "3"),
            eventsHostList = mutableListOf("3"),
            friendsList = mutableListOf(),
            profilePicUrl = "http://example.com/Profile_Pictures/1",
            qrCodeUrl = "http://example.com/QR_Codes/1",
            username = "johndoe"
        )

    private val mockUser2 =
        User(
            userId = "user2",
            birthDate = "01/01/2000",
            email = "test@example.com",
            firstName = "John",
            lastName = "Doe",
            phoneNumber = "1234567890",
            accountStatus = "active",
            eventsAttendeeList = mutableListOf("2", "3"),
            eventsHostList = mutableListOf("event3"),
            friendsList = mutableListOf(),
            profilePicUrl = "http://example.com/Profile_Pictures/1",
            qrCodeUrl = "http://example.com/QR_Codes/1",
            username = "johndoe"
        )

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
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        userRepository = MockUserRepository()
        eventRepository = MockEventRepository()
        qrCodeAnalyser = QrCodeAnalyser()
        viewModel = ScanTicketQrViewModel(userRepository, eventRepository, qrCodeAnalyser, "1")
    }

    @Test
    fun testDecodingSuccess() = runTest {
        userRepository.addUser(mockUser1)
        eventRepository.addEvent(mockEvent)
        val testDecodedString = "user1"
        qrCodeAnalyser.onDecoded?.invoke(testDecodedString)
        TestCase.assertEquals(testDecodedString, viewModel.uiState.value.decodedResult)
    }

    @Test
    fun testDecodingFailure() = runTest {
        userRepository.addUser(mockUser1)
        eventRepository.addEvent(mockEvent)
        qrCodeAnalyser.onDecoded?.invoke(null)
        TestCase.assertEquals(
            ScanTicketQrViewModel.Action.AnalyserError, viewModel.uiState.value.action
        )
    }

    @Test
    fun testInvokedAndUpdatedOnce() = runTest {
        userRepository.addUser(mockUser1)
        eventRepository.addEvent(mockEvent)
        qrCodeAnalyser.onDecoded?.invoke("user1")
        when (val event1 = eventRepository.getEvent("1")) {
            is Resource.Success -> {
                TestCase.assertEquals(mutableListOf("user2", "user3"), event1.data!!.attendeeList)
            }

            else -> {
                assert(false)
                println("User 2 not found or could not be fetched")
            }
        }
        when (val user1 = userRepository.getUser("user1")) {
            is Resource.Success -> {
                TestCase.assertEquals(mutableListOf("2", "3"), user1.data!!.eventsAttendeeList)
            }

            else -> {
                assert(false)
                println("User 1 not found or could not be fetched")
            }
        }
        TestCase.assertEquals(
            ScanTicketQrViewModel.Action.ApproveEntry,
            viewModel.uiState.value.action
        )
    }

    @Test
    fun testInvokedAndFetchError() = runTest {
        userRepository.addUser(mockUser1)
        eventRepository.addEvent(mockEvent)
        qrCodeAnalyser.onDecoded?.invoke("user5")
        advanceUntilIdle()
        TestCase.assertEquals(
            ScanTicketQrViewModel.Action.FirebaseFetchError, viewModel.uiState.value.action
        )
    }

    @Test
    fun testInvokedAndDenied() = runTest {
        userRepository.addUser(mockUser2)
        eventRepository.addEvent(mockEvent)
        qrCodeAnalyser.onDecoded?.invoke("user2")
        advanceUntilIdle()
        assertEquals(ScanTicketQrViewModel.Action.DenyEntry, viewModel.uiState.value.action)
    }

    @Test
    fun testGetEventWithNoDataInDataBase() = runTest {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0

        viewModel.getEventData()
        assert(viewModel.uiState.value.eventName.isEmpty())
        assert(viewModel.uiState.value.description.isEmpty())
        assert(viewModel.uiState.value.eventPhoto.isEmpty())
        assert(viewModel.uiState.value.mainOrganiser.isEmpty())

        unmockkAll()
    }

    @Test
    fun testGetEventSuccess() = runTest {
        eventRepository.addEvent(mockEvent)
        viewModel.getEventData()

        assert(viewModel.uiState.value.eventName == mockEvent.eventName)
        assert(viewModel.uiState.value.description == mockEvent.description)
        assert(viewModel.uiState.value.mainOrganiser == mockEvent.mainOrganiser)
        assert(viewModel.uiState.value.start == mockEvent.start)
        assert(viewModel.uiState.value.end == mockEvent.end)
        assert(viewModel.uiState.value.location == mockEvent.location)
        assert(viewModel.uiState.value.ticket == mockEvent.ticket)
        assert(viewModel.uiState.value.category == mockEvent.category)
    }

    @Test
    fun testGetEventWithUpdateAndFetchAgain() = runTest {
        eventRepository.addEvent(mockEvent)
        viewModel.getEventData()
        assert(viewModel.uiState.value.eventName == mockEvent.eventName)

        mockEvent.eventName = "New Name"
        assert(viewModel.uiState.value.eventName != mockEvent.eventName)

        viewModel.getEventData()
        assert(viewModel.uiState.value.eventName == mockEvent.eventName)
    }
}

