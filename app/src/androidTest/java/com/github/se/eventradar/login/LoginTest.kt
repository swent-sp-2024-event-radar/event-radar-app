package com.github.se.eventradar.login

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.toPackage
import androidx.test.espresso.intent.rule.IntentsRule
import com.github.se.eventradar.model.repository.user.IUserRepository
import com.github.se.eventradar.screens.LoginScreen
import com.github.se.eventradar.ui.login.LoginScreen
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.viewmodel.LoginViewModel
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import javax.inject.Inject
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class LoginTest : TestCase() {
  @get:Rule(order = 1) val composeTestRule = createComposeRule()

  @get:Rule(order = 0) var hiltRule = HiltAndroidRule(this)

  // This rule automatic initializes lateinit properties with @MockK, @RelaxedMockK, etc.
  @get:Rule val mockkRule = MockKRule(this)

  @get:Rule val intentsRule = IntentsRule()

  @Inject lateinit var userRepository: IUserRepository

  // Relaxed mocks methods have a default implementation returning values
  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  @Before
  fun setUp() {
    hiltRule.inject()
    // Launch the Signup screen
    composeTestRule.setContent { LoginScreen(LoginViewModel(userRepository), mockNavActions) }
  }

  @Test
  fun allElementsAreCorrectlyDisplayed() {
    ComposeScreen.onComposeScreen<LoginScreen>(composeTestRule) {
      // Test the UI elements
      eventRadarLogo { assertIsDisplayed() }
      loginButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
    }
  }

  /** This test checks that the google authentication works correctly */
  @Test
  fun googleSignInReturnsValidActivityResult() {
    ComposeScreen.onComposeScreen<LoginScreen>(composeTestRule) {
      loginButton {
        assertIsDisplayed()
        performClick()
      }

      // assert that an Intent resolving to Google Mobile Services has been sent (for sign-in)
      intended(toPackage("com.google.android.gms"))
    }
  }
}
