package com.github.se.eventradar.home

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.eventradar.model.Location
import com.github.se.eventradar.model.event.Event
import com.github.se.eventradar.model.event.EventCategory
import com.github.se.eventradar.model.event.EventList
import com.github.se.eventradar.model.event.EventTicket
import com.github.se.eventradar.screens.HomeScreen
import com.github.se.eventradar.ui.home.HomeScreen
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.viewmodel.EventsOverviewUiState
import com.github.se.eventradar.viewmodel.EventsOverviewViewModel
import com.github.se.eventradar.viewmodel.Tab
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import java.time.LocalDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

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
                            mainOrganiser = "1",
                            organiserSet = mutableSetOf("Test Organiser"),
                            attendeeSet = mutableSetOf("Test Attendee"),
                            category = EventCategory.COMMUNITY,
                            fireBaseID = "$it")
                      })))
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

  @Before
  fun testSetup() {
    every { mockEventsOverviewViewModel.getEvents() } returns Unit
    every { mockEventsOverviewViewModel.getUpcomingEvents() } returns Unit
    every { mockEventsOverviewViewModel.uiState } returns sampleEventList
    composeTestRule.setContent { HomeScreen(mockEventsOverviewViewModel, mockNavActions) }
  }

  @Test
  fun screenDisplaysAllElementsCorrectly() = run {
    onComposeScreen<HomeScreen>(composeTestRule) {
      logo { assertIsDisplayed() }
      tabs { assertIsDisplayed() }
      upcomingTab { assertIsDisplayed() }
      browseTab { assertIsDisplayed() }
      eventCard { assertIsDisplayed() }
      bottomNav { assertIsDisplayed() }
      viewToggleFab { assertIsDisplayed() }
      searchBarAndFilter { assertIsDisplayed() }
      filterPopUp { assertIsDisplayed() }
      eventList { assertIsDisplayed() }
    }
  }

  @Test
  fun mapDisplaysOnceViewToggleFabIsClicked() = run {
    onComposeScreen<HomeScreen>(composeTestRule) {
      step("Click on view toggle fab") {
        viewToggleFab {
          assertIsDisplayed()
          performClick()
        }
      }

      // Update the UI state to reflect the change
      sampleEventList.value = sampleEventList.value.copy(viewList = false)
      step("Check if map is displayed") { map { assertIsDisplayed() } }

      step("Click on view toggle fab again") {
        viewToggleFab {
          assertIsDisplayed()
          performClick()
        }
      }
      verify(exactly = 2) { mockEventsOverviewViewModel.onViewListStatusChanged() }

      // Update the UI state to reflect the change
      sampleEventList.value = sampleEventList.value.copy(viewList = true)
      step("Check if map is hidden") {
        map { assertDoesNotExist() }
        eventCard { assertIsDisplayed() }
      }
    }
  }

  @Test
  fun getEventsIsCalledOnLaunch() = run {
    onComposeScreen<HomeScreen>(composeTestRule) {
      verify(exactly = 1) { mockEventsOverviewViewModel.getEvents() }
      verify(exactly = 1) { mockEventsOverviewViewModel.uiState }
      confirmVerified(mockEventsOverviewViewModel)
    }
  }

  @Test
  fun testUpcomingEventsTriggeredOnTabSelect() = run {
    onComposeScreen<HomeScreen>(composeTestRule) {
      step("Select 'Upcoming' tab") {
        upcomingTab {
          assertIsDisplayed()
          performClick()
        }
      }
    }
    // Update the UI state to reflect the change
    mockEventsOverviewViewModel.onTabChanged(Tab.UPCOMING, sampleEventList)

    // Verify if getEvents is called upon init
    verify { mockEventsOverviewViewModel.getEvents() }

    // Verify that the tab change is handled correctly
    verify { mockEventsOverviewViewModel.onTabChanged(Tab.UPCOMING, any()) }

    // Verify that the upcoming events are fetched once
    verify(exactly = 1) { mockEventsOverviewViewModel.getUpcomingEvents() }

    // Check that uiState is accessed as expected
    verify { mockEventsOverviewViewModel.uiState }

    // Confirm that no unexpected interactions have occurred
    confirmVerified(mockEventsOverviewViewModel)
  }

  @Test
  fun testDisplayUpcomingEventsList() = run {
    val upcomingEvents = listOf(mockEvent, mockEvent.copy(fireBaseID = "2"))
    every { mockEventsOverviewViewModel.uiState } returns
        MutableStateFlow(EventsOverviewUiState(viewList = true))
    every { mockEventsOverviewViewModel.getUpcomingEvents() } answers
        {
          sampleEventList.value =
              sampleEventList.value.copy(eventList = EventList(allEvents = upcomingEvents))
        }

    onComposeScreen<HomeScreen>(composeTestRule) {
      step("Select 'Upcoming' tab") {
        upcomingTab.performClick()
        eventList.assertIsDisplayed()
      }
    }
  }

  @Test
  fun testDisplayUpcomingEventsMap() = run {
    onComposeScreen<HomeScreen>(composeTestRule) {
      step("Trigger loading of upcoming events") {
        upcomingTab {
          assertIsDisplayed()
          performClick()
        }
      }
      mockEventsOverviewViewModel.onTabChanged(Tab.UPCOMING, sampleEventList)
    }
    onComposeScreen<HomeScreen>(composeTestRule) {
      step("Click on view toggle fab") {
        viewToggleFab {
          assertIsDisplayed()
          performClick()
        }
      }

      // Update the UI state to reflect the change
      sampleEventList.value = sampleEventList.value.copy(viewList = false)
      step("Check if map is displayed") { map { assertIsDisplayed() } }
    }
  }
}
