package com.github.se.eventradar.hosting

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.eventradar.model.Location
import com.github.se.eventradar.model.event.Event
import com.github.se.eventradar.model.event.EventCategory
import com.github.se.eventradar.model.event.EventList
import com.github.se.eventradar.model.event.EventTicket
import com.github.se.eventradar.screens.HostingScreen
import com.github.se.eventradar.ui.hosting.HostingScreen
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.viewmodel.HostedEventsUiState
import com.github.se.eventradar.viewmodel.HostedEventsViewModel
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
class HostingTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()
  // This rule automatic initializes lateinit properties with @MockK, @RelaxedMockK, etc.
  @get:Rule val mockkRule = MockKRule(this)
  // Relaxed mocks methods have a default implementation returning values
  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  @RelaxedMockK lateinit var mockHostedEventsViewModel: HostedEventsViewModel

  private val sampleEventList =
      MutableStateFlow(
          HostedEventsUiState(
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
                            ticket = EventTicket("Test Ticket", 0.0, 1, 0),
                            mainOrganiser = "1",
                            organiserList = mutableListOf("Test Organiser"),
                            attendeeList = mutableListOf("Test Attendee"),
                            category = EventCategory.COMMUNITY,
                            fireBaseID = "$it")
                      })))

  @Before
  fun testSetup() {
    every { mockHostedEventsViewModel.getHostedEvents() } returns Unit
    every { mockHostedEventsViewModel.uiState } returns sampleEventList
    composeTestRule.setContent {
      HostingScreen(viewModel = mockHostedEventsViewModel, navigationActions = mockNavActions)
    }
  }

  @Test
  fun screenDisplaysAllElementsCorrectly() = run {
    onComposeScreen<HostingScreen>(composeTestRule) {
      logo { assertIsDisplayed() }
      myHostedEventsTitle { assertIsDisplayed() }
      eventCard { assertIsDisplayed() }
      bottomNav { assertIsDisplayed() }
      floatingActionButtons { assertIsDisplayed() }
      createEventFab { assertIsDisplayed() }
      viewToggleFab { assertIsDisplayed() }
      eventList { assertIsDisplayed() }
      searchBarAndFilter { assertIsDisplayed() }
      filterPopUp { assertIsNotDisplayed() }
    }
  }

  @Test
  fun mapDisplaysOnceViewToggleFabIsClicked() = run {
    onComposeScreen<HostingScreen>(composeTestRule) {
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
      verify(exactly = 2) { mockHostedEventsViewModel.onViewListStatusChanged() }

      // Update the UI state to reflect the change
      sampleEventList.value = sampleEventList.value.copy(viewList = true)
      step("Check if map is hidden") {
        map { assertDoesNotExist() }
        eventCard { assertIsDisplayed() }
      }
    }
  }

  @Test
  fun getHostedEventsIsCalledOnLaunch() = run {
    onComposeScreen<HostingScreen>(composeTestRule) {
      verify(exactly = 1) { mockHostedEventsViewModel.getHostedEvents() }
      verify(exactly = 1) { mockHostedEventsViewModel.uiState }
      confirmVerified(mockHostedEventsViewModel)
    }
  }

  @Test
  fun filterPopUpDisplaysOnceFilterClicked() = run {
    onComposeScreen<HostingScreen>(composeTestRule) {
      step("Click on filter button") {
        filterButton {
          assertIsDisplayed()
          performClick()
        }
      }

      // Update the UI state to reflect the change
      sampleEventList.value = sampleEventList.value.copy(isFilterDialogOpen = true)
      step("Check if filter pop up is displayed") { filterPopUp { assertIsDisplayed() } }

      step("Check if category checkboxes are displayed") {
        composeTestRule.onAllNodesWithTag("checkbox").assertCountEquals(EventCategory.entries.size)
      }

      step("Check if category checkbox text is displayed") {
        composeTestRule
            .onAllNodesWithTag("checkboxText")
            .assertCountEquals(EventCategory.entries.size)
      }

      step("Click on filter button again") {
        filterButton {
          assertIsDisplayed()
          performClick()
        }
      }
      verify(exactly = 2) { mockHostedEventsViewModel.onFilterDialogOpen() }

      // Update the UI state to reflect the change
      sampleEventList.value = sampleEventList.value.copy(isFilterDialogOpen = false)
      step("Check if filter pop up is hidden") { filterPopUp { assertDoesNotExist() } }
    }
  }

  @Test
  fun filterClickedShowsResults() = run {
    onComposeScreen<HostingScreen>(composeTestRule) {
      // 1. Open filter pop up
      step("Click on filter button") {
        filterButton {
          assertIsDisplayed()
          performClick()
        }
      }
      // Update the UI state to reflect the change
      sampleEventList.value = sampleEventList.value.copy(isFilterDialogOpen = true)
      step("Check if filter pop up is displayed") { filterPopUp { assertIsDisplayed() } }
      verify { mockHostedEventsViewModel.onFilterDialogOpen() }

      // 2. Enter radius filter
      radiusLabel { assertIsDisplayed() }
      radiusInput {
        assertIsDisplayed()
        performClick()
        performTextInput("100000000")
      }
      verify { mockHostedEventsViewModel.onRadiusQueryChanged("100000000") }
      kmLabel { assertIsDisplayed() }

      // 3. Change free event filter
      freeSwitchLabel { assertIsDisplayed() }
      freeSwitch {
        assertIsDisplayed()
        performClick()
      }
      sampleEventList.value = sampleEventList.value.copy(isFreeSwitchOn = true)
      verify { mockHostedEventsViewModel.onFreeSwitchChanged() }

      // 4. Check category filter
      categoryLabel { assertIsDisplayed() }
      categoryOptionsColumn { assertIsDisplayed() }
      categoryOptionRow { assertIsDisplayed() }

      // 5. Apply filter
      step("Click on filter apply button") {
        filterApplyButton {
          assertIsDisplayed()
          performClick()
        }
      }
      // Update the UI state to reflect the change
      sampleEventList.value = sampleEventList.value.copy(isFilterActive = true)
      verify { mockHostedEventsViewModel.onFilterApply() }

      // 6. Check filtered events are displayed
      eventList { assertIsDisplayed() }
      eventCard { assertIsDisplayed() }
      verify { mockHostedEventsViewModel.uiState }
      verify { mockHostedEventsViewModel.filterHostedEvents() }
    }
  }

  @Test
  fun typingInSearchBarShowsResults() = run {
    onComposeScreen<HostingScreen>(composeTestRule) {
      searchBar {
        assertIsDisplayed()
        performClick()
        performTextInput("Event 0")
      }

      // Update the UI state to reflect the change
      sampleEventList.value = sampleEventList.value.copy(isSearchActive = true)

      eventList { assertIsDisplayed() }
      eventCard { assertIsDisplayed() }

      verify { mockHostedEventsViewModel.onSearchQueryChanged("Event 0") }
      verify { mockHostedEventsViewModel.onSearchActiveChanged(true) }
      verify { mockHostedEventsViewModel.uiState }
      verify { mockHostedEventsViewModel.filterHostedEvents() }

      searchBar { performTextClearance() }

      verify { mockHostedEventsViewModel.onSearchQueryChanged("") }
      verify { mockHostedEventsViewModel.onSearchActiveChanged(false) }
      verify { mockHostedEventsViewModel.getHostedEvents() }

      confirmVerified(mockHostedEventsViewModel)
    }
  }

  @Test
  fun typingInSearchBarShowsResultsInMap() = run {
    onComposeScreen<HostingScreen>(composeTestRule) {
      // 1. Open the map view
      step("Click on view toggle fab") {
        viewToggleFab {
          assertIsDisplayed()
          performClick()
        }
      }

      // Update the UI state to reflect the change
      sampleEventList.value = sampleEventList.value.copy(viewList = false)
      step("Check if map is displayed") { map { assertIsDisplayed() } }

      // 2. Activate search
      searchBar {
        assertIsDisplayed()
        performClick()
        performTextInput("Event 0")
      }

      // Update the UI state to reflect the change
      sampleEventList.value = sampleEventList.value.copy(isSearchActive = true)

      verify { mockHostedEventsViewModel.onSearchQueryChanged("Event 0") }
      verify { mockHostedEventsViewModel.onSearchActiveChanged(true) }
      verify { mockHostedEventsViewModel.uiState }

      // 3. Check filtered map is displayed
      map { assertIsDisplayed() }
      verify { mockHostedEventsViewModel.filterHostedEvents() }

      // 4. Clear search
      searchBar { performTextClearance() }
      verify { mockHostedEventsViewModel.onSearchQueryChanged("") }
      verify { mockHostedEventsViewModel.onSearchActiveChanged(false) }
      verify { mockHostedEventsViewModel.getHostedEvents() }
    }
  }
}
