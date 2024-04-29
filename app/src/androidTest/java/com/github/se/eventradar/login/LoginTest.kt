package com.github.se.eventradar.login

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.toPackage
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.eventradar.model.repository.user.MockUserRepository
import com.github.se.eventradar.screens.LoginScreen
import com.github.se.eventradar.ui.login.LoginScreen
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.viewmodel.LoginViewModel
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginTest : TestCase() {
  @get:Rule val composeTestRule = createComposeRule()

  // This rule automatic initializes lateinit properties with @MockK, @RelaxedMockK, etc.
  @get:Rule val mockkRule = MockKRule(this)

  // Relaxed mocks methods have a default implementation returning values
  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  private var mockUserRepository: MockUserRepository = MockUserRepository()

  @Before
  fun setUp() {
    // Launch the Signup screen
    composeTestRule.setContent { LoginScreen(LoginViewModel(mockUserRepository), mockNavActions) }
  }

  @Test
  fun allElementsAreCorrectlyDisplayed() = run {
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
    Intents.init()
    ComposeScreen.onComposeScreen<LoginScreen>(composeTestRule) {
      loginButton {
        assertIsDisplayed()
        performClick()
      }

      // assert that an Intent resolving to Google Mobile Services has been sent (for sign-in)
      intended(toPackage("com.google.android.gms"))
    }
    Intents.release()
  }
}
