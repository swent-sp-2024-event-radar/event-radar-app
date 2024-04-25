package com.github.se.eventradar.hosting

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.eventradar.model.EventsOverviewUiState
import com.github.se.eventradar.model.EventsOverviewViewModel
import com.github.se.eventradar.model.Location
import com.github.se.eventradar.model.event.Event
import com.github.se.eventradar.model.event.EventCategory
import com.github.se.eventradar.model.event.EventList
import com.github.se.eventradar.model.event.EventTicket
import com.github.se.eventradar.screens.HostingScreen
import com.github.se.eventradar.ui.hosting.HostingScreen
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import java.time.LocalDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HostingTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()
  // This rule automatic initializes lateinit properties with @MockK, @RelaxedMockK, etc.
  @get:Rule val mockkRule = MockKRule(this)

  // Relaxed mocks methods have a default implementation returning values
  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  @RelaxedMockK lateinit var mockEventsOverviewViewModel: EventsOverviewViewModel

  private val sampleEventList =
      MutableStateFlow(
          EventsOverviewUiState(
              eventList =
                  EventList(
                      List(20) {
                        Event(
                            eventName = "Event $it",
                            eventPhoto = "",
                            start = LocalDateTime.now(),
                            end = LocalDateTime.now(),
                            location = Location(0.0, 0.0, "Test Location"),
                            description = "Test Description",
                            ticket = EventTicket("Test Ticket", 0.0, 1),
                            contact = "Test Contact Email",
                            organiserList = setOf("Test Organiser"),
                            attendeeList = setOf("Test Attendee"),
                            category = EventCategory.COMMUNITY,
                            fireBaseID = "$it")
                      })))

  @Before
  fun testSetup() {
    every { mockEventsOverviewViewModel.getEvents() } returns Unit
    every { mockEventsOverviewViewModel.uiState } returns sampleEventList
    composeTestRule.setContent {
      HostingScreen(viewModel = mockEventsOverviewViewModel, navigationActions = mockNavActions)
    }
  }

  @Test
  fun screenDisplaysAllElementsCorrectly() = run {
    ComposeScreen.onComposeScreen<HostingScreen>(composeTestRule) {
      logo { assertIsDisplayed() }
      myHostedEventsTitle { assertIsDisplayed() }
      eventCard { assertIsDisplayed() }
      bottomNav { assertIsDisplayed() }
      floatingActionButtons { assertIsDisplayed() }
      createEventFab { assertIsDisplayed() }
      viewToggleFab { assertIsDisplayed() }
    }
  }

  @Test
  fun mapDisplaysOnceViewToggleFabIsClicked() = run {
    ComposeScreen.onComposeScreen<HostingScreen>(composeTestRule) {
      step("Click on view toggle fab") {
        viewToggleFab {
          assertIsDisplayed()
          performClick()
        }
      }

      step("Check if map is displayed") { map { assertIsDisplayed() } }

      step("Click on view toggle fab again") {
        viewToggleFab {
          assertIsDisplayed()
          performClick()
        }
      }

      step("Check if map is hidden") {
        map { assertDoesNotExist() }
        eventCard { assertIsDisplayed() }
      }
    }
  }
}
