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
import com.github.se.eventradar.viewmodel.EventDetailsViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.spyk
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
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@ExperimentalCoroutinesApi
class EventDetailsViewmodelUnitTest {
  private lateinit var viewModel: EventDetailsViewModel
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

  val test: MutableSet<String> = mutableSetOf("Test Organiser", "Organiser2")

  private var ticketPurchases = 1

  private val mockEvent =
      Event(
          eventName = "Event 1",
          eventPhoto = "",
          start = LocalDateTime.now(),
          end = LocalDateTime.now(),
          location = Location(0.0, 0.0, "Test Location"),
          description = "Test Description",
          ticket = EventTicket("Test Ticket", 0.0, 10, ticketPurchases),
          mainOrganiser = "1",
          organiserList = mutableListOf("Test Organiser"),
          attendeeList = mutableListOf("Test Attendee"),
          category = EventCategory.COMMUNITY,
          fireBaseID = "event1")

  private val corruptedEvent =
      Event(
          eventName = mockEvent.eventName,
          eventPhoto = mockEvent.eventPhoto,
          start = mockEvent.start,
          end = mockEvent.end,
          location = mockEvent.location,
          description = mockEvent.description,
          ticket = mockEvent.ticket,
          mainOrganiser = mockEvent.mainOrganiser,
          organiserList = mockEvent.organiserList,
          attendeeList = mockEvent.attendeeList,
          category = mockEvent.category,
          fireBaseID = "corrupted_id")

  private val mockUser =
      User(
          userId = "user1",
          birthDate = "01.01.2000",
          email = "test@example.com",
          firstName = "John",
          lastName = "Doe",
          phoneNumber = "1234567890",
          accountStatus = "active",
          eventsAttendeeList = mutableListOf("event2"),
          eventsHostList = mutableListOf("event3"),
          friendsList = mutableListOf(),
          profilePicUrl = "http://example.com/Profile_Pictures/pic.jpg",
          qrCodeUrl = "http://example.com/QR_Codes/qr.jpg",
          bio = "",
          username = "johndoe")

  private val factory =
      object : EventDetailsViewModel.Factory {
        override fun create(eventId: String): EventDetailsViewModel {
          return EventDetailsViewModel(eventRepository, userRepository, eventId)
        }
      }

  @Before
  fun setUp() {
    eventRepository = MockEventRepository()

    userRepository = MockUserRepository()
    (userRepository as MockUserRepository).updateCurrentUserId(mockUser.userId)

    viewModel = factory.create(eventId = mockEvent.fireBaseID)
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

  @Test
  fun testTicketIsFree() = runTest {
    eventRepository.addEvent(mockEvent)
    mockEvent.ticket = EventTicket("Paid", 0.0, 10, 0)
    viewModel.getEventData()
    assert(viewModel.isTicketFree())
  }

  @Test
  fun testTicketIsNotFree() = runTest {
    eventRepository.addEvent(mockEvent)
    val randomPrice: Double = kotlin.random.Random.nextDouble(0.001, Double.MAX_VALUE)
    mockEvent.ticket = EventTicket("Paid", randomPrice, 10, 0)
    viewModel.getEventData()
    assert(!viewModel.isTicketFree())
  }

  @Test
  fun testJoinEventNoUser() = runTest {
    mockkStatic(Log::class)

    eventRepository.addEvent(mockEvent)
    // userRepository.addUser(mockUser)

    viewModel.getEventData()

    viewModel.buyTicketForEvent()

    assert(viewModel.errorOccurred.value)

    unmockkAll()
  }

  @Test
  fun testJoinEventNoEvent() = runTest {
    mockkStatic(Log::class)

    // eventRepository.addEvent(mockEvent)
    userRepository.addUser(mockUser)

    viewModel.getEventData()
    viewModel.buyTicketForEvent()

    assert(viewModel.errorOccurred.value)

    unmockkAll()
  }

  @Test
  fun testCorruptEventIdInDb() = runTest {
    mockkStatic(Log::class)

    eventRepository.addEvent(mockEvent)
    userRepository.addUser(mockUser)

    viewModel.getEventData()

    // corrupt db
    eventRepository.deleteEvent(mockEvent)
    eventRepository.addEvent(corruptedEvent)

    // assert error
    viewModel.buyTicketForEvent()
    assert(viewModel.errorOccurred.value)

    unmockkAll()
  }

  @Test
  fun testJoinAnEvent() = runTest {
    mockkStatic(Log::class)
    every { Log.d(any(), any()) } returns 0

    eventRepository.addEvent(mockEvent)
    userRepository.addUser(mockUser)

    viewModel.getEventData()

    viewModel.buyTicketForEvent()

    assert(mockUser.eventsAttendeeList.contains(mockEvent.fireBaseID))
    assert(mockEvent.attendeeList.contains(mockUser.userId))
    assert(mockEvent.ticket.purchases == ticketPurchases + 1)
    assert(viewModel.registrationSuccessful.value)

    unmockkAll()
  }

  @Test
  fun testJoinAnEventWithNoMoreTickets() = runTest {
    mockkStatic(Log::class)
    every { Log.d(any(), any()) } returns 0

    // no more tickets, purchases = capacity
    mockEvent.ticket =
        EventTicket(
            mockEvent.ticket.name,
            mockEvent.ticket.price,
            mockEvent.ticket.capacity,
            mockEvent.ticket.capacity)

    eventRepository.addEvent(mockEvent)
    userRepository.addUser(mockUser)

    viewModel.getEventData()

    viewModel.buyTicketForEvent()

    assert(viewModel.errorOccurred.value)

    unmockkAll()
  }

  @Test
  fun testRemoveUserFromEvent() = runTest {
    mockkStatic(Log::class)
    every { Log.d(any(), any()) } returns 0

    eventRepository.addEvent(mockEvent)
    userRepository.addUser(mockUser)

    viewModel.getEventData()

    viewModel.buyTicketForEvent()

    assert(mockUser.eventsAttendeeList.contains(mockEvent.fireBaseID))
    assert(mockEvent.attendeeList.contains(mockUser.userId))
    assert(mockEvent.ticket.purchases == ticketPurchases + 1)
    assert(viewModel.registrationSuccessful.value)

    viewModel.removeUserFromEvent()

    assert(!mockUser.eventsAttendeeList.contains(mockEvent.fireBaseID))
    assert(!mockEvent.attendeeList.contains(mockUser.userId))
    assert(mockEvent.ticket.purchases == ticketPurchases)

    unmockkAll()
  }

  @Test
  fun testRemoveUserFromEventUpdateEventFailure() = runTest {
    mockkStatic(Log::class)
    every { Log.d(any(), any()) } returns 0

    eventRepository.addEvent(mockEvent)
    userRepository.addUser(mockUser)

    viewModel.getEventData()

    viewModel.buyTicketForEvent()

    assert(mockUser.eventsAttendeeList.contains(mockEvent.fireBaseID))
    assert(mockEvent.attendeeList.contains(mockUser.userId))
    assert(mockEvent.ticket.purchases == ticketPurchases + 1)
    assert(viewModel.registrationSuccessful.value)

    // corrupt db
    eventRepository.deleteEvent(mockEvent)

    viewModel.removeUserFromEvent()

    assert(viewModel.errorOccurred.value)

    verify {
      Log.d(
          "EventDetailsViewModel",
          "Error removing attendee in event: Event with id ${mockEvent.fireBaseID} not found")
    }

    unmockkAll()
  }

  @Test
  fun testRemoveUserFromEventGetUserFailure() = runTest {
    mockkStatic(Log::class)
    every { Log.d(any(), any()) } returns 0

    eventRepository.addEvent(mockEvent)
    userRepository.addUser(mockUser)

    viewModel.getEventData()

    viewModel.buyTicketForEvent()

    assert(mockUser.eventsAttendeeList.contains(mockEvent.fireBaseID))
    assert(mockEvent.attendeeList.contains(mockUser.userId))
    assert(mockEvent.ticket.purchases == ticketPurchases + 1)
    assert(viewModel.registrationSuccessful.value)

    // corrupt db
    userRepository.deleteUser(mockUser)

    viewModel.removeUserFromEvent()

    assert(viewModel.errorOccurred.value)

    verify {
      Log.d(
          "EventDetailsViewModel",
          "Error removing attendance in user: User with id ${mockUser.userId} not found")
    }

    unmockkAll()
  }

  @Test
  fun testRemoveUserFromEventUpdateUserFailure() = runTest {
    mockkStatic(Log::class)

    userRepository = spyk(MockUserRepository())
    (userRepository as MockUserRepository).updateCurrentUserId(mockUser.userId)

    viewModel = factory.create(eventId = mockEvent.fireBaseID)

    every { Log.d(any(), any()) } returns 0

    eventRepository.addEvent(mockEvent)
    userRepository.addUser(mockUser)

    viewModel.getEventData()

    viewModel.buyTicketForEvent()

    assert(mockUser.eventsAttendeeList.contains(mockEvent.fireBaseID))
    assert(mockEvent.attendeeList.contains(mockUser.userId))
    assert(mockEvent.ticket.purchases == ticketPurchases + 1)
    assert(viewModel.registrationSuccessful.value)

    // must mock the method here to avoid mocking it during the call to `buyTicketForEvent()`
    coEvery { userRepository.getUser(any()) } returns
        Resource.Failure(Exception("User with id ${mockUser.userId} not found"))

    viewModel.removeUserFromEvent()

    assert(viewModel.errorOccurred.value)

    verify {
      Log.d(
          "EventDetailsViewModel",
          "Error removing attendance in user: User with id ${mockUser.userId} not found")
    }

    unmockkAll()
  }
}
