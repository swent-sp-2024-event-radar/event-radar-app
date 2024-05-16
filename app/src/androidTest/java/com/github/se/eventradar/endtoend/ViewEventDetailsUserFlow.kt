package com.github.se.eventradar.endtoend

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.eventradar.model.Location
import com.github.se.eventradar.model.event.Event
import com.github.se.eventradar.model.event.EventCategory
import com.github.se.eventradar.model.event.EventTicket
import com.github.se.eventradar.model.repository.event.IEventRepository
import com.github.se.eventradar.model.repository.event.MockEventRepository
import com.github.se.eventradar.model.repository.user.IUserRepository
import com.github.se.eventradar.model.repository.user.MockUserRepository
import com.github.se.eventradar.screens.EventDetailsScreen
import com.github.se.eventradar.screens.HomeScreen
import com.github.se.eventradar.ui.event.EventDetails
import com.github.se.eventradar.ui.home.HomeScreen
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.ui.navigation.Route
import com.github.se.eventradar.ui.theme.MyApplicationTheme
import com.github.se.eventradar.viewmodel.EventDetailsViewModel
import com.github.se.eventradar.viewmodel.EventsOverviewViewModel
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import java.time.LocalDateTime
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ViewEventDetailsUserFlow : TestCase() {
  @get:Rule val composeTestRule = createComposeRule()

  // This rule automatic initializes lateinit properties with @MockK, @RelaxedMockK, etc.
  @get:Rule val mockkRule = MockKRule(this)

  // Relaxed mocks methods have a default implementation returning values
  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  private var userRepository: IUserRepository = MockUserRepository()
  private var eventRepository: IEventRepository = MockEventRepository()

  private val mockEvent =
      Event(
          "Test 1",
          "",
          LocalDateTime.parse("2021-12-31T09:00:00"),
          LocalDateTime.parse("2022-01-01T00:00:00"),
          Location(0.0, 0.0, "EPFL"),
          "Test Description",
          EventTicket("Test Ticket", 0.0, 100, 0),
          "",
          mutableListOf(),
          mutableListOf(),
          EventCategory.SOCIAL,
          "1")

  @Before
  fun setUp() = runBlocking {
    for (i in 0..2) {
      eventRepository.addEvent(mockEvent.copy(eventName = "Test $i", fireBaseID = "$i"))
    }

    // Launch the Signup screen
    composeTestRule.setContent {
      val navController = rememberNavController()
      mockNavActions = NavigationActions(navController)
      MyApplicationTheme {
        NavHost(navController = navController, startDestination = Route.HOME) {
          composable(Route.HOME) {
            HomeScreen(EventsOverviewViewModel(eventRepository, userRepository), mockNavActions)
          }
          composable(
              "${Route.EVENT_DETAILS}/{eventId}",
              arguments = listOf(navArgument("eventId") { type = NavType.StringType })) {
                val eventId = it.arguments!!.getString("eventId")!!
                //            val viewModel = EventDetailsViewModel.create(eventId = eventId)
                EventDetails(EventDetailsViewModel(eventRepository, eventId), mockNavActions)
              }
        }
      }
    }
  }

  @Test
  fun homeScreenToEventDetailsScreen() = run {
    ComposeScreen.onComposeScreen<HomeScreen>(composeTestRule) {
      step("Check if all events are present at the start") {
        // Test the UI elements
        eventList { assertIsDisplayed() }

        for (i in 0..2) {
          val card = onNode { hasText("Test $i") }
          card { assertIsDisplayed() }
        }
      }

      step("Filter to only show one event") {
        // Test the UI elements
        searchBarAndFilter { assertIsDisplayed() }
        searchBar { performTextInput("Test 1") }

        filteredEventList { assertIsDisplayed() }

        // Check if only one event is displayed
        for (i in 0..2) {
          val card = onNode { hasText("Test $i") }
          card {
            if (i == 1) {
              assertIsDisplayed()
            } else {
              assertDoesNotExist()
            }
          }
        }
      }

      step("Click on the event card") { eventCard { performClick() } }
    }

    ComposeScreen.onComposeScreen<EventDetailsScreen>(composeTestRule) {
      step("Check if the event details are displayed") {
        // Test the UI elements
        eventTitle { assertIsDisplayed() }
        eventImage { assertIsDisplayed() }
        descriptionTitle { assertIsDisplayed() }
        descriptionContent {
          assertIsDisplayed()
          assertTextContains("Test Description")
        }
        distanceTitle { assertIsDisplayed() }
        distanceContent { assertIsDisplayed() }
        categoryTitle { assertIsDisplayed() }
        categoryContent {
          assertIsDisplayed()
          assertTextContains("Social")
        }
        dateTimeTitle { assertIsDisplayed() }
        dateTimeStartContent { assertIsDisplayed() }
        dateTimeEndContent { assertIsDisplayed() }
      }
    }
  }
}
