package com.github.se.eventradar.event

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.eventradar.model.Location
import com.github.se.eventradar.model.event.Event
import com.github.se.eventradar.model.event.EventCategory
import com.github.se.eventradar.model.event.EventDetailsViewModel
import com.github.se.eventradar.model.event.EventTicket
import com.github.se.eventradar.model.event.EventUiState
import com.github.se.eventradar.model.repository.event.IEventRepository
import com.github.se.eventradar.model.repository.event.MockEventRepository
import com.github.se.eventradar.screens.EventDetailsScreen
import com.github.se.eventradar.ui.event.EventDetails
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.ui.navigation.Route
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.time.LocalDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
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
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@OptIn(ExperimentalCoroutinesApi::class)
class UITest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()

  // This rule automatic initializes lateinit properties with @MockK, @RelaxedMockK, etc.
  @get:Rule val mockkRule = MockKRule(this)

  @get:Rule
  var hiltRule = HiltAndroidRule(this)


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


  @Inject
  lateinit var eventRepository: IEventRepository

  private lateinit var viewModel: EventDetailsViewModel


  private val factory =
    object : EventDetailsViewModel.Factory {
      override fun create(eventId: String): EventDetailsViewModel {
        return EventDetailsViewModel(eventRepository, eventId)
      }
    }

  // Relaxed mocks methods have a default implementation returning values
  @RelaxedMockK lateinit var mockNavActions: NavigationActions


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


  @Before
  fun testSetup() {

    hiltRule.inject()

    viewModel = factory.create(eventId = mockEvent.fireBaseID)

    composeTestRule.setContent {
      EventDetails(viewModel,
      navigationActions = mockNavActions) }
  }

  @Test
  fun screenDisplaysNavigationElementsCorrectly() = runTest {

    (eventRepository as MockEventRepository).addEvent(mockEvent)
    viewModel.getEventData()

    ComposeScreen.onComposeScreen<EventDetailsScreen>(composeTestRule) {
      assert(viewModel.uiState.value.eventName == mockEvent.eventName)
    }
  }

}
