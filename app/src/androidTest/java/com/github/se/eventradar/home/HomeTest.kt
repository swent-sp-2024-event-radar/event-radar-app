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
import com.github.se.eventradar.ui.navigation.Route
import com.github.se.eventradar.viewmodel.EventsOverviewUiState
import com.github.se.eventradar.viewmodel.EventsOverviewViewModel
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
                      }),
          ))

  @Before
  fun testSetup() {
    every { mockEventsOverviewViewModel.getEvents() } returns Unit
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
      eventList { assertIsDisplayed() }
      filterPopUp { assertIsNotDisplayed() }
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
  fun filterPopUpDisplaysOnceFilterClicked() = run {
    onComposeScreen<HomeScreen>(composeTestRule) {
      step("Click on filter button") {
        filterButton {
          assertIsDisplayed()
          performClick()
        }
      }

      // Update the UI state to reflect the change
      sampleEventList.value = sampleEventList.value.copy(isFilterDialogOpen = true)
      step("Check if filter pop up is displayed") { filterPopUp { assertIsDisplayed() } }

      step("Click on filter button again") {
        filterButton {
          assertIsDisplayed()
          performClick()
        }
      }
      verify(exactly = 2) { mockEventsOverviewViewModel.onFilterDialogOpen() }

      // Update the UI state to reflect the change
      sampleEventList.value = sampleEventList.value.copy(isFilterDialogOpen = false)
      step("Check if filter pop up is hidden") { filterPopUp { assertDoesNotExist() } }
    }
  }

  @Test
  fun filterClickedShowsResults() = run {
    onComposeScreen<HomeScreen>(composeTestRule) {
      step("Click on filter button") {
        filterButton {
          assertIsDisplayed()
          performClick()
        }
      }
      // Update the UI state to reflect the change
      sampleEventList.value = sampleEventList.value.copy(isFilterDialogOpen = true)
      step("Check if filter pop up is displayed") { filterPopUp { assertIsDisplayed() } }
      verify { mockEventsOverviewViewModel.onFilterDialogOpen() }

//      radiusInput {
//        assertIsDisplayed()
//        performClick()
//        performTextInput("10")
//      }
//      verify { mockEventsOverviewViewModel.onRadiusQueryChanged("10") }

//      freeSwitch {
//        assertIsDisplayed()
//        performClick()
//      }
//      sampleEventList.value = sampleEventList.value.copy(isFreeSwitchOn = false)
//      verify { mockEventsOverviewViewModel.onFreeSwitchChanged() }
//
//      step("Click on filter apply button") {
//        filterApplyButton {
//          assertIsDisplayed()
//          performClick()
//        }
//      }
//
//      filteredEventList { assertIsDisplayed() }
//      eventCard { assertIsDisplayed() }
//
//      // Update the UI state to reflect the change
//      sampleEventList.value = sampleEventList.value.copy(isFilterActive = true)
//      verify { mockEventsOverviewViewModel.onFilterApply() }
//      verify { mockEventsOverviewViewModel.uiState }
//      verify { mockEventsOverviewViewModel.filterEvents() }
//      confirmVerified(mockEventsOverviewViewModel)
    }
  }

  @Test
  fun typingInSearchBarShowsResults() = run {
    onComposeScreen<HomeScreen>(composeTestRule) {
      searchBar {
        assertIsDisplayed()
        performClick()
        performTextInput("Event 0")
      }

      // Update the UI state to reflect the change
      sampleEventList.value = sampleEventList.value.copy(isSearchActive = true)

      filteredEventList { assertIsDisplayed() }
      eventCard { assertIsDisplayed() }

      verify { mockEventsOverviewViewModel.onSearchQueryChanged("Event 0") }
      verify { mockEventsOverviewViewModel.onSearchActiveChanged(true) }
      verify { mockEventsOverviewViewModel.uiState }
      verify { mockEventsOverviewViewModel.filterEvents() }

      searchBar { performTextClearance() }

      verify { mockEventsOverviewViewModel.onSearchQueryChanged("") }
      verify { mockEventsOverviewViewModel.onSearchActiveChanged(false) }
      verify { mockEventsOverviewViewModel.getEvents() }

      confirmVerified(mockEventsOverviewViewModel)
    }
  }

  @Test
  fun typingInSearchBarShowsResultsInMap() = run {
    onComposeScreen<HomeScreen>(composeTestRule) {
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

      verify { mockEventsOverviewViewModel.onSearchQueryChanged("Event 0") }
      verify { mockEventsOverviewViewModel.onSearchActiveChanged(true) }
      verify { mockEventsOverviewViewModel.uiState }

      // 3. Check filtered map is displayed
      filteredMap { assertIsDisplayed() }
      verify { mockEventsOverviewViewModel.filterEvents() }

      // 4. Clear search
      searchBar { performTextClearance() }
      verify { mockEventsOverviewViewModel.onSearchQueryChanged("") }
      verify { mockEventsOverviewViewModel.onSearchActiveChanged(false) }
      verify { mockEventsOverviewViewModel.getEvents() }
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
}
