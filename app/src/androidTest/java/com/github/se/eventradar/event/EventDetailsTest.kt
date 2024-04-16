package com.github.se.eventradar.event

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.eventradar.screens.EventDetailsScreen
import com.github.se.eventradar.ui.event.EventDetails
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EventDetailsTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun testSetup() {
    composeTestRule.setContent { EventDetails(NavigationActions(rememberNavController())) }
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
}
