package com.github.se.eventradar.login

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.toPackage
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.eventradar.screens.LoginScreen
import com.github.se.eventradar.ui.MainActivity
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginTest : TestCase() {
  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  @Test
  fun titleAndButtonAreCorrectlyDisplayed() {
    ComposeScreen.onComposeScreen<LoginScreen>(composeTestRule) {
      // Test the UI elements
      loginLogo { assertIsDisplayed() }
      loginTitle {
        assertIsDisplayed()
        assertTextEquals("Event Radar")
      }
      loginButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
    }
  }

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

  // TODO: Add test for failed log-in
  /*
  @Test
  fun signInFailureShowsErrorDialog() {
    composeTestRule.activity.runOnUiThread {
      composeTestRule.activity.setContent {
        // ActivityResultRegistry is responsible for handling the
        // contracts and launching the activity
        val registryOwner =
            object : ActivityResultRegistryOwner {
              override val activityResultRegistry =
                  object : ActivityResultRegistry() {
                    override fun <I : Any?, O : Any?> onLaunch(
                        requestCode: Int,
                        contract: ActivityResultContract<I, O>,
                        input: I,
                        options: ActivityOptionsCompat?
                    ) {
                      this.dispatchResult(requestCode, Activity.RESULT_OK)
                    }
                  }
            }

        CompositionLocalProvider(LocalActivityResultRegistryOwner provides registryOwner) {
          LoginScreen(NavigationActions(rememberNavController()))
        }
      }
    }

    // Verify that the error dialog is displayed
    ComposeScreen.onComposeScreen<LoginScreen>(composeTestRule) {
      loginButton {
        assertIsDisplayed()
        performClick()
      }

      loginErrorDisplayText {
        assertIsDisplayed()
        assertTextEquals("Sign in Failed. Please try again.")
      }

      loginErrorTitle {
        assertIsDisplayed()
        assertTextEquals("Sign in Failed")
      }
    }
  }
  */
}
