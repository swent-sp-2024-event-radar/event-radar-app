package com.github.se.eventradar.signup

import android.content.Intent
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.toPackage
import androidx.test.espresso.intent.rule.IntentsRule
import com.github.se.eventradar.model.repository.user.IUserRepository
import com.github.se.eventradar.screens.SignupScreen
import com.github.se.eventradar.ui.login.SignUpScreen
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.viewmodel.LoginViewModel
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.confirmVerified
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import javax.inject.Inject
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class SignupTest : TestCase() {
  @get:Rule(order = 1) val composeTestRule = createComposeRule()

  @get:Rule(order = 0) val hiltRule = HiltAndroidRule(this)

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
    composeTestRule.setContent { SignUpScreen(LoginViewModel(userRepository), mockNavActions) }
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

      verify(exactly = 0) { mockNavActions.navController.navigate(any() as String) }
      confirmVerified(mockNavActions)
    }
  }
}
