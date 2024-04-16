package com.github.se.eventradar.event

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.eventradar.screens.EventDetailsScreen
import com.github.se.eventradar.ui.event.EventDetails
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.confirmVerified
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
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

  @Before
  fun testSetup() {
    composeTestRule.setContent { EventDetails(navigationActions = mockNavActions) }
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
      ticketButton { assertIsDisplayed() }
      bottomNav { assertIsDisplayed() }
      eventImage { assertIsDisplayed() }
      descriptionTitle { assertIsDisplayed() }
      descriptionContent { assertIsDisplayed() }
      distanceTitle { assertIsDisplayed() }
      distanceContent { assertIsDisplayed() }
      dateTitle { assertIsDisplayed() }
      dateContent { assertIsDisplayed() }
      categoryTitle { assertIsDisplayed() }
      categoryContent { assertIsDisplayed() }
      timeTitle { assertIsDisplayed() }
      timeStartContent { assertIsDisplayed() }
      timeEndContent { assertIsDisplayed() }
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
