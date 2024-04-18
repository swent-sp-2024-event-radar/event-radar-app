package com.github.se.eventradar.event

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.eventradar.model.Location
import com.github.se.eventradar.model.event.EventCategory
import com.github.se.eventradar.model.event.EventDetailsViewModel
import com.github.se.eventradar.model.event.EventUiState
import com.github.se.eventradar.model.event.Ticket
import com.github.se.eventradar.screens.EventDetailsScreen
import com.github.se.eventradar.ui.event.EventDetails
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
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
class EventDetailsTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()

  // This rule automatic initializes lateinit properties with @MockK, @RelaxedMockK, etc.
  @get:Rule val mockkRule = MockKRule(this)

  // Relaxed mocks methods have a default implementation returning values
  @RelaxedMockK lateinit var mockNavActions: NavigationActions
  @RelaxedMockK lateinit var mockViewModel: EventDetailsViewModel

  private val sampleEventStates =
      MutableStateFlow(
          EventUiState(
              eventName = "Debugging",
              eventPhoto = "path",
              start = LocalDateTime.MIN,
              end = LocalDateTime.MAX,
              location = Location(0.0, 0.0, "base address"),
              description = "Let's debug some code together because we all enjoy kotlin !",
              ticket = Ticket("Luck", 0.0, 7),
              contact = "some.name@host.com",
              category = EventCategory.COMMUNITY,
          ))

  val eventId = "tdjWMT9Eon2ROTVakQb"

  @Before
  fun testSetup() {

    every { mockViewModel.uiState } returns sampleEventStates
    every { mockViewModel.eventId } returns eventId

    composeTestRule.setContent { EventDetails(mockViewModel, navigationActions = mockNavActions) }
  }

  @Test
  fun screenDisplaysNavigationElementsCorrectly() = run {
    ComposeScreen.onComposeScreen<EventDetailsScreen>(composeTestRule) {
      ticketButton { assertIsDisplayed() }
      goBackButton { assertIsDisplayed() }
      bottomNav { assertIsDisplayed() }
    }
  }

  @Test
  fun screenDisplaysContentElementsCorrectly() = run {
    ComposeScreen.onComposeScreen<EventDetailsScreen>(composeTestRule) {
      eventImage { assertIsDisplayed() }
      descriptionTitle { assertIsDisplayed() }
      descriptionContent {
        assertIsDisplayed()
        assertTextContains("Let's debug some code together because we all enjoy kotlin !")
      }
      distanceTitle { assertIsDisplayed() }
      distanceContent { assertIsDisplayed() }
      categoryTitle { assertIsDisplayed() }
      categoryContent {
        assertIsDisplayed()
        assertTextContains("COMMUNITY")
      }
      dateTimeTitle { assertIsDisplayed() }
      dateTimeStartContent { assertIsDisplayed() }
      dateTimeEndContent { assertIsDisplayed() }
    }
  }

  @Test
  fun goBackButtonTriggersBackNavigation() = run {
    ComposeScreen.onComposeScreen<EventDetailsScreen>(composeTestRule) {
      goBackButton {
        // arrange: verify the pre-conditions
        assertIsDisplayed()
        assertIsEnabled()

        // act: go back !
        performClick()
      }
    }
    // assert: the nav action has been called
    verify { mockNavActions.goBack() }
    confirmVerified(mockNavActions)
  }
}
