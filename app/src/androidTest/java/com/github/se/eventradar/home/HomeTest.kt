package com.github.se.eventradar.home

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
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
                            ticket = EventTicket("Test Ticket", 0.0, 1, 0),
                            mainOrganiser = "1",
                            organiserList = mutableListOf("Test Organiser"),
                            attendeeList = mutableListOf("Test Attendee"),
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
          ticket = EventTicket("Test Ticket", 0.0, 1, 0),
          mainOrganiser = "1",
          organiserList = mutableListOf("Test Organiser"),
          attendeeList = mutableListOf("Test Attendee"),
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
      verify(exactly = 2) { mockEventsOverviewViewModel.onFilterDialogOpen() }

      // Update the UI state to reflect the change
      sampleEventList.value = sampleEventList.value.copy(isFilterDialogOpen = false)
      step("Check if filter pop up is hidden") { filterPopUp { assertDoesNotExist() } }
    }
  }

  @Test
  fun filterClickedShowsResults() = run {
    onComposeScreen<HomeScreen>(composeTestRule) {
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
      verify { mockEventsOverviewViewModel.onFilterDialogOpen() }

      // 2. Enter radius filter
      radiusLabel { assertIsDisplayed() }
      radiusInput {
        assertIsDisplayed()
        performClick()
        performTextInput("100000000")
      }
      verify { mockEventsOverviewViewModel.onRadiusQueryChanged("100000000") }
      kmLabel { assertIsDisplayed() }

      // 3. Change free event filter
      freeSwitchLabel { assertIsDisplayed() }
      freeSwitch {
        assertIsDisplayed()
        performClick()
      }
      sampleEventList.value = sampleEventList.value.copy(isFreeSwitchOn = true)
      verify { mockEventsOverviewViewModel.onFreeSwitchChanged() }

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
      verify { mockEventsOverviewViewModel.onFilterApply() }

      // 6. Check filtered events are displayed
      eventList { assertIsDisplayed() }
      eventCard { assertIsDisplayed() }
      verify { mockEventsOverviewViewModel.uiState }
      verify { mockEventsOverviewViewModel.filterEvents() }
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

      eventList { assertIsDisplayed() }
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
      map { assertIsDisplayed() }
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

  @Test
  fun getUpcomingEventsIsCalledOnRecomposition() = run {
    sampleEventList.value = sampleEventList.value.copy(tab = Tab.BROWSE)
    onComposeScreen<HomeScreen>(composeTestRule) {
      step("Select 'Upcoming' tab") {
        upcomingTab {
          assertIsDisplayed()
          performClick()
        }
      }
      sampleEventList.value = sampleEventList.value.copy(tab = Tab.UPCOMING)
    }
    onComposeScreen<HomeScreen>(composeTestRule) {
      verify(exactly = 1) { mockEventsOverviewViewModel.getUpcomingEvents() }
      verify(exactly = 1) { mockEventsOverviewViewModel.uiState }
    }
  }

  @Test
  fun testTriggeredOnTabSelect() = run {
    onComposeScreen<HomeScreen>(composeTestRule) {
      step("Select 'Upcoming' tab") {
        upcomingTab {
          assertIsDisplayed()
          performClick()
        }
      }
      // Update the UI state to reflect the change
      sampleEventList.value = sampleEventList.value.copy(tab = Tab.UPCOMING)

      // Verify if getEvents is called upon init
      verify { mockEventsOverviewViewModel.getEvents() }

      // Verify that the tab change is handled correctly
      verify { mockEventsOverviewViewModel.onTabChanged(Tab.UPCOMING, any()) }

      // Verify that the upcoming events are fetched once
      verify(exactly = 1) { mockEventsOverviewViewModel.getUpcomingEvents() }

      // Check that uiState is accessed as expected
      verify { mockEventsOverviewViewModel.uiState }

      step("Select 'Browse' tab") {
        browseTab {
          assertIsDisplayed()
          performClick()
        }
      }
      sampleEventList.value = sampleEventList.value.copy(tab = Tab.BROWSE)

      // Verify that the tab change is handled correctly
      verify { mockEventsOverviewViewModel.onTabChanged(Tab.BROWSE, any()) }

      // Verify if getEvents is called upon init
      verify { mockEventsOverviewViewModel.getEvents() }

      // Check that uiState is accessed as expected
      verify { mockEventsOverviewViewModel.uiState }

      // Confirm that no unexpected interactions have occurred
      confirmVerified(mockEventsOverviewViewModel)
    }
  }

  @Test
  fun testDisplayUpcomingEventsList() = run {
    val upcomingEvents = listOf(mockEvent, mockEvent.copy(fireBaseID = "2"))
    sampleEventList.value =
        sampleEventList.value.copy(
            upcomingEventList = EventList(allEvents = upcomingEvents), userLoggedIn = true)

    onComposeScreen<HomeScreen>(composeTestRule) {
      step("Trigger loading of upcoming events") {
        upcomingTab {
          assertIsDisplayed()
          performClick()
        }
      }
      sampleEventList.value = sampleEventList.value.copy(tab = Tab.UPCOMING, viewList = true)
      step("view list") { eventListUpcoming.assertIsDisplayed() }
      // Verify that the tab change is handled correctly
      verify(exactly = 1) { mockEventsOverviewViewModel.onTabChanged(Tab.UPCOMING, any()) }

      // Verify that the upcoming events are fetched once
      verify(exactly = 1) { mockEventsOverviewViewModel.getUpcomingEvents() }
    }
  }

  @Test
  fun testDisplayUpcomingEventsListFiltered() = run {
    val upcomingEvents = listOf(mockEvent, mockEvent.copy(eventName = "Event 2", fireBaseID = "2"))
    sampleEventList.value =
        sampleEventList.value.copy(
            upcomingEventList = EventList(allEvents = upcomingEvents), userLoggedIn = true)

    onComposeScreen<HomeScreen>(composeTestRule) {
      // 1. Open the upcoming tab
      step("Trigger loading of upcoming events") {
        upcomingTab {
          assertIsDisplayed()
          performClick()
        }
      }
      sampleEventList.value = sampleEventList.value.copy(tab = Tab.UPCOMING, viewList = true)
      step("view list") { eventListUpcoming.assertIsDisplayed() }
      // Verify that the tab change is handled correctly
      verify(exactly = 1) { mockEventsOverviewViewModel.onTabChanged(Tab.UPCOMING, any()) }

      // Verify that the upcoming events are fetched once
      verify(exactly = 1) { mockEventsOverviewViewModel.getUpcomingEvents() }

      // 2. Search in the upcoming tab
      searchBar {
        assertIsDisplayed()
        performClick()
        performTextInput("Event 1")
      }

      // Update the UI state to reflect the change
      sampleEventList.value = sampleEventList.value.copy(isSearchActive = true)

      eventListUpcoming { assertIsDisplayed() }
      eventCard { assertIsDisplayed() }

      verify { mockEventsOverviewViewModel.onSearchQueryChanged("Event 1") }
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
  fun testDisplayUpcomingEventsMapFiltered() = run {
    val upcomingEvents = listOf(mockEvent, mockEvent.copy(eventName = "Event 2", fireBaseID = "2"))
    sampleEventList.value =
        sampleEventList.value.copy(
            upcomingEventList = EventList(allEvents = upcomingEvents), userLoggedIn = true)

    onComposeScreen<HomeScreen>(composeTestRule) {
      // 1. Open the upcoming tab
      step("Trigger loading of upcoming events") {
        upcomingTab {
          assertIsDisplayed()
          performClick()
        }
      }
      sampleEventList.value = sampleEventList.value.copy(tab = Tab.UPCOMING, viewList = false)
      step("view map") { mapUpcoming.assertIsDisplayed() }
      // Verify that the tab change is handled correctly
      verify(exactly = 1) { mockEventsOverviewViewModel.onTabChanged(Tab.UPCOMING, any()) }

      // Verify that the upcoming events are fetched once
      verify(exactly = 1) { mockEventsOverviewViewModel.getUpcomingEvents() }

      // 2. Search in the upcoming tab
      searchBar {
        assertIsDisplayed()
        performClick()
        performTextInput("Event 1")
      }

      // Update the UI state to reflect the change
      sampleEventList.value = sampleEventList.value.copy(isSearchActive = true)

      mapUpcoming { assertIsDisplayed() }

      verify { mockEventsOverviewViewModel.onSearchQueryChanged("Event 1") }
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
  fun testDisplayUpcomingEventsMap() = run {
    val upcomingEvents = listOf(mockEvent, mockEvent.copy(fireBaseID = "2"))
    sampleEventList.value =
        sampleEventList.value.copy(
            upcomingEventList = EventList(allEvents = upcomingEvents), userLoggedIn = true)

    onComposeScreen<HomeScreen>(composeTestRule) {
      step("Trigger loading of upcoming events") {
        upcomingTab {
          assertIsDisplayed()
          performClick()
        }
      }
      sampleEventList.value = sampleEventList.value.copy(tab = Tab.UPCOMING, viewList = false)
      step("view map") { mapUpcoming.assertIsDisplayed() }
      // Verify that the tab change is handled correctly
      verify(exactly = 1) { mockEventsOverviewViewModel.onTabChanged(Tab.UPCOMING, any()) }

      // Verify that the upcoming events are fetched once
      verify(exactly = 1) { mockEventsOverviewViewModel.getUpcomingEvents() }
    }
  }

  @Test
  fun testPleaseLogInMessageDisplayed() = run {
    val upcomingEvents = listOf(mockEvent, mockEvent.copy(fireBaseID = "2"))
    sampleEventList.value =
        sampleEventList.value.copy(
            upcomingEventList = EventList(allEvents = upcomingEvents), userLoggedIn = false)

    onComposeScreen<HomeScreen>(composeTestRule) {
      step("Trigger loading of upcoming events") {
        upcomingTab {
          assertIsDisplayed()
          performClick()
        }
      }
      sampleEventList.value = sampleEventList.value.copy(tab = Tab.UPCOMING)
      step("Verify that the 'Please Login' message is displayed") {
        pleaseLogInText.assertIsDisplayed()
      }
      // Verify that the tab change is handled correctly
      verify(exactly = 1) { mockEventsOverviewViewModel.onTabChanged(Tab.UPCOMING, any()) }

      // Verify that the upcoming events are fetched once
      verify(exactly = 1) { mockEventsOverviewViewModel.getUpcomingEvents() }
    }
  }

  @Test
  fun testNoUpcomingEventsMessageDisplayed() = run {
    sampleEventList.value =
        sampleEventList.value.copy(
            upcomingEventList = EventList(emptyList(), emptyList(), null),
            viewList = true,
            userLoggedIn = true)

    onComposeScreen<HomeScreen>(composeTestRule) {
      step("Trigger loading of upcoming events") {
        upcomingTab {
          assertIsDisplayed()
          performClick()
        }
      }
      sampleEventList.value = sampleEventList.value.copy(tab = Tab.UPCOMING)
      step("Verify that the 'No upcoming events' message is displayed") {
        noUpcomingEventsText.assertIsDisplayed()
      }
      // Verify that the tab change is handled correctly
      verify(exactly = 1) { mockEventsOverviewViewModel.onTabChanged(Tab.UPCOMING, any()) }

      // Verify that the upcoming events are fetched once
      verify(exactly = 1) { mockEventsOverviewViewModel.getUpcomingEvents() }
    }
  }
}
