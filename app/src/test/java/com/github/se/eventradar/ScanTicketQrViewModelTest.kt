package com.github.se.eventradar

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

  private val mockEvent1 =
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
          attendeeSet = mutableSetOf("user1", "user2", "user3"),
          category = EventCategory.COMMUNITY,
          fireBaseID = "1")

  private val mockUser1 = // access to event 1 & 2 & 3
      User(
          userId = "user1",
          birthDate = "01/01/2000",
          email = "test@example.com",
          firstName = "John",
          lastName = "Doe",
          phoneNumber = "1234567890",
          accountStatus = "active",
          eventsAttendeeSet = mutableSetOf("1", "2", "3"),
          eventsHostSet = mutableSetOf("3"),
          friendsSet = mutableSetOf(),
          profilePicUrl = "http://example.com/pic.jpg",
          qrCodeUrl = "http://example.com/qr.jpg",
          username = "john_doe")

  private val mockUser2 = // access to event 2 & 3 only
      User(
          userId = "user2",
          birthDate = "01/01/2002",
          email = "test@example2.com",
          firstName = "John2",
          lastName = "Doe2",
          phoneNumber = "12345678902",
          accountStatus = "active2",
          eventsAttendeeSet = mutableSetOf("2", "3"),
          eventsHostSet = mutableSetOf("32"),
          friendsSet = mutableSetOf(),
          profilePicUrl = "http://example.com/pic.jpg2",
          qrCodeUrl = "http://example.com/qr.jpg2",
          username = "john_doe2")

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

  @Before
  fun setUp() {
    userRepository = MockUserRepository()
    eventRepository = MockEventRepository()
    qrCodeAnalyser = QrCodeAnalyser()
    viewModel = ScanTicketQrViewModel(userRepository, eventRepository, qrCodeAnalyser)
  }

  @Test
  fun testDecodingSuccess() = runTest {
    userRepository.addUser(mockUser1)
    eventRepository.addEvent(mockEvent1)
    val testDecodedString = "user1"
    qrCodeAnalyser.onDecoded?.invoke(testDecodedString)
    TestCase.assertEquals(testDecodedString, viewModel.uiState.value.decodedResult)
  }

  @Test
  fun testDecodingFailure() = runTest {
    userRepository.addUser(mockUser1)
    eventRepository.addEvent(mockEvent1)
    qrCodeAnalyser.onDecoded?.invoke(null)
    TestCase.assertEquals(
        ScanTicketQrViewModel.Action.AnalyserError, viewModel.uiState.value.action)
  }

  @Test
  fun testInvokedAndUpdatedOnce() = runTest {
    userRepository.addUser(mockUser1)
    eventRepository.addEvent(mockEvent1)
    qrCodeAnalyser.onDecoded?.invoke("user1")
    when (val event1 = eventRepository.getEvent("1")) {
      is Resource.Success -> {
        TestCase.assertEquals(mutableSetOf("user2", "user3"), event1.data!!.attendeeSet)
      }
      else -> {
        assert(false)
        println("User 2 not found or could not be fetched")
      }
    }
    when (val user1 = userRepository.getUser("user1")) {
      is Resource.Success -> {
        TestCase.assertEquals(mutableSetOf("2", "3"), user1.data!!.eventsAttendeeSet)
      }
      else -> {
        assert(false)
        println("User 1 not found or could not be fetched")
      }
    }
    TestCase.assertEquals(ScanTicketQrViewModel.Action.ApproveEntry, viewModel.uiState.value.action)
  }

  @Test
  fun testInvokedAndFetchError() = runTest {
    userRepository.addUser(mockUser1)
    eventRepository.addEvent(mockEvent1)
    qrCodeAnalyser.onDecoded?.invoke("user5")
    advanceUntilIdle()
    TestCase.assertEquals(
        ScanTicketQrViewModel.Action.FirebaseFetchError, viewModel.uiState.value.action)
  }

  @Test
  fun testInvokedAndDenied() = runTest {
    userRepository.addUser(mockUser2)
    eventRepository.addEvent(mockEvent1)
    qrCodeAnalyser.onDecoded?.invoke("user2")
    advanceUntilIdle()
    assertEquals(ScanTicketQrViewModel.Action.DenyEntry, viewModel.uiState.value.action)
  }
}
