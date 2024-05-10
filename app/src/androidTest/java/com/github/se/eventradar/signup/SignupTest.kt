package com.github.se.eventradar.signup

import android.content.Intent
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.Espresso
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.toPackage
import androidx.test.espresso.intent.rule.IntentsRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.eventradar.model.repository.user.MockUserRepository
import com.github.se.eventradar.screens.SignupScreen
import com.github.se.eventradar.ui.login.SignUpScreen
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.viewmodel.LoginViewModel
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
class SignupTest : TestCase() {
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val intentsTestRule = IntentsRule()

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
  fun allElementsAreCorrectlyDisplayed() {
    ComposeScreen.onComposeScreen<SignupScreen>(composeTestRule) {
      // Test the UI elements
      eventRadarLogo {
        performScrollTo()
        assertIsDisplayed()
      }
      profilePicture {
        performScrollTo()
        assertIsDisplayed()
      }
      usernameTextField {
        performScrollTo()
        assertIsDisplayed()
      }
      nameTextField {
        performScrollTo()
        assertIsDisplayed()
      }
      surnameTextField {
        performScrollTo()
        assertIsDisplayed()
      }
      phoneTextField {
        performScrollTo()
        assertIsDisplayed()
      }
      birthDateTextField {
        performScrollTo()
        assertIsDisplayed()
      }
      signUpButton {
        performScrollTo()
        assertIsDisplayed()
        assertHasClickAction()
      }
    }
  }

  /** This test checks that the google authentication works correctly */
  @Test
  fun googleSignInReturnsValidActivityResult() {
    ComposeScreen.onComposeScreen<SignupScreen>(composeTestRule) {
      usernameTextField {
        performScrollTo()
        assertIsDisplayed()
        performTextInput("test")
      }
      nameTextField {
        performScrollTo()
        assertIsDisplayed()
        performTextInput("test")
      }
      surnameTextField {
        performScrollTo()
        assertIsDisplayed()
        performTextInput("test")
      }
      phoneTextField {
        performScrollTo()
        assertIsDisplayed()
        performTextInput("123456789")
      }
      birthDateTextField {
        performScrollTo()
        assertIsDisplayed()
        performTextInput("01/01/2000")
      }

      signUpButton {
        performScrollTo()
        assertIsDisplayed()
        performClick()
      }

      // assert that an Intent resolving to Google Mobile Services has been sent (for sign-in)
      Espresso.onIdle()
      intended(toPackage("com.google.android.gms"))
    }
  }

  @Test
  fun profilePicClickOpensGallery() {
    ComposeScreen.onComposeScreen<SignupScreen>(composeTestRule) {
      profilePicture {
        assertIsDisplayed()
        performClick()
      }

      intended(hasAction(Intent.ACTION_GET_CONTENT))
    }
  }

  @Test
  fun notAbleToSignInIfNotAllFieldsEntered() {
    ComposeScreen.onComposeScreen<SignupScreen>(composeTestRule) {
      signUpButton {
        performScrollTo()
        assertIsDisplayed()
        performClick()
      }

      verify(exactly = 0) { mockNavActions.navigateTo(any()) }
      confirmVerified(mockNavActions)
    }
  }
}
