package com.github.se.eventradar.signup

import android.content.Intent
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.toPackage
import com.github.se.eventradar.model.repository.user.MockUserRepository
import com.github.se.eventradar.screens.SignupScreen
import com.github.se.eventradar.ui.login.LoginViewModel
import com.github.se.eventradar.ui.login.SignUpScreen
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import dagger.hilt.android.testing.HiltAndroidTest
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.confirmVerified
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
// @Config(application = HiltTestApplication::class)
class SignupTest : TestCase() {
  @get:Rule val composeTestRule = createComposeRule()

  // This rule automatic initializes lateinit properties with @MockK, @RelaxedMockK, etc.
  @get:Rule val mockkRule = MockKRule(this)

  // Relaxed mocks methods have a default implementation returning values
  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  private var mockUserRepository: MockUserRepository = MockUserRepository()

  @Before
  fun setUp() {
    // Launch the Signup screen
    composeTestRule.setContent { SignUpScreen(LoginViewModel(mockUserRepository), mockNavActions) }
  }

  @Test
  fun allElementsAreCorrectlyDisplayed() = run {
    ComposeScreen.onComposeScreen<SignupScreen>(composeTestRule) {
      // Test the UI elements
      eventRadarLogo { assertIsDisplayed() }
      profilePicture { assertIsDisplayed() }
      usernameTextField { assertIsDisplayed() }
      nameTextField { assertIsDisplayed() }
      surnameTextField { assertIsDisplayed() }
      phoneTextField { assertIsDisplayed() }
      birthDateTextField { assertIsDisplayed() }
      signUpButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
    }
  }

  /** This test checks that the google authentication works correctly */
  @Test
  fun googleSignInReturnsValidActivityResult() = run {
    Intents.init()
    ComposeScreen.onComposeScreen<SignupScreen>(composeTestRule) {
      step("fill in all fields") {
        usernameTextField {
          assertIsDisplayed()
          performTextInput("test")
        }
        nameTextField {
          assertIsDisplayed()
          performTextInput("test")
        }
        surnameTextField {
          assertIsDisplayed()
          performTextInput("test")
        }
        phoneTextField {
          assertIsDisplayed()
          performTextInput("123456789")
        }
        birthDateTextField {
          assertIsDisplayed()
          performTextInput("01/01/2000")
        }
      }

      step("click on sign up button") {
        signUpButton {
          assertIsDisplayed()
          performClick()
        }
      }

      // assert that an Intent resolving to Google Mobile Services has been sent (for sign-in)
      intended(toPackage("com.google.android.gms"))
    }
    Intents.release()
  }

  @Test
  fun profilePicClickOpensGallery() = run {
    Intents.init()
    ComposeScreen.onComposeScreen<SignupScreen>(composeTestRule) {
      profilePicture {
        assertIsDisplayed()
        performClick()
      }

      intended(hasAction(Intent.ACTION_GET_CONTENT))
    }
    Intents.release()
  }

  @Test
  fun notAbleToSignInIfNotAllFieldsEntered() {
    ComposeScreen.onComposeScreen<SignupScreen>(composeTestRule) {
      signUpButton {
        assertIsDisplayed()
        performClick()
      }

      verify(exactly = 0) { mockNavActions.navigateTo(any()) }
      confirmVerified(mockNavActions)
    }
  }
}
