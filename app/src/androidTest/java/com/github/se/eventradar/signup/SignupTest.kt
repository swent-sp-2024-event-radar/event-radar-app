package com.github.se.eventradar.signup

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.eventradar.screens.SignupScreen
import com.github.se.eventradar.ui.MainActivity
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignupTest : TestCase() {
  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  // The IntentsTestRule simply calls Intents.init() before the @Test block
  // and Intents.release() after the @Test block is completed. IntentsTestRule
  // is deprecated, but it was MUCH faster than using IntentsRule in our tests
  // @get:Rule val intentsTestRule = IntentsTestRule(MainActivity::class.java)

  /** This test checks that the logo is correctly displayed on the screen */
  @Test
  fun logoIsCorrectlyDisplayed() {
    ComposeScreen.onComposeScreen<SignupScreen>(composeTestRule) {
      // Test the UI elements
      eventRadarLogo { assertIsDisplayed() }
    }
  }

  /*
    /** This test checks that the profile picture is correctly displayed */
    @Test
    fun profilePictureIsCorrectlyDisplayed() {
      ComposeScreen.onComposeScreen<SignupScreen>(composeTestRule) {
        // Test the UI elements
        profilePicture { assertIsDisplayed() }
      }
    }

    /** This test checks that the username text field is correctly displayed */
    @Test
    fun usernameTextFieldIsCorrectlyDisplayed() {
      ComposeScreen.onComposeScreen<SignupScreen>(composeTestRule) {
        // Test the UI elements
        usernameTextField { assertIsDisplayed() }
      }
    }

    /** This test checks that the name text field is correctly displayed */
    @Test
    fun nameTextFieldIsCorrectlyDisplayed() {
      ComposeScreen.onComposeScreen<SignupScreen>(composeTestRule) {
        // Test the UI elements
        nameTextField { assertIsDisplayed() }
      }
    }

    /** This test checks that the surname text field is correctly displayed */
    @Test
    fun surnameTextFieldIsCorrectlyDisplayed() {
      ComposeScreen.onComposeScreen<SignupScreen>(composeTestRule) {
        // Test the UI elements
        surnameTextField { assertIsDisplayed() }
      }
    }

    /** This test checks that the phone text field is correctly displayed */
    @Test
    fun phoneTextFieldIsCorrectlyDisplayed() {
      ComposeScreen.onComposeScreen<SignupScreen>(composeTestRule) {
        // Test the UI elements
        phoneTextField { assertIsDisplayed() }
      }
    }

    /** This test checks that the birth date text field is correctly displayed */
    @Test
    fun birthDateTextFieldIsCorrectlyDisplayed() {
      ComposeScreen.onComposeScreen<SignupScreen>(composeTestRule) {
        // Test the UI elements
        birthDateTextField { assertIsDisplayed() }
      }
    }

    /** This test checks that the sign up button is correctly displayed */
    @Test
    fun signUpButtonIsCorrectlyDisplayed() {
      ComposeScreen.onComposeScreen<SignupScreen>(composeTestRule) {
        // Test the UI elements
        signUpButton {
          assertIsDisplayed()
          assertHasClickAction()
        }
      }
    }

    /** This test checks that the google authentication works correctly */
    @Test
    fun googleSignInReturnsValidActivityResult() {
      Intents.init()
      ComposeScreen.onComposeScreen<SignupScreen>(composeTestRule) {
        signUpButton {
          assertIsDisplayed()
          performClick()
        }

        // assert that an Intent resolving to Google Mobile Services has been sent (for sign-in)
        intended(toPackage("com.google.android.gms"))
      }
      Intents.release()
    }
  }

  //    @Test
  //    fun signInFailureShowsErrorDialog() {
  ////        // Mock the result of the sign-in operation to simulate failure
  ////        val mockLauncher = mockk<ActivityResultLauncher<Intent>>()
  ////        val mockResult = ActivityResult(Activity.RESULT_CANCELED, null)
  ////        every { mockLauncher.launch(any()) } answers { mockResult }
  //
  //        composeTestRule.setContent {
  //            // ActivityResultRegistry is responsible for handling the
  //            // contracts and launching the activity
  //            val registryOwner = object : ActivityResultRegistryOwner {
  //
  //                override val activityResultRegistry = object : ActivityResultRegistry() {
  //                    override fun <I : Any?, O : Any?> onLaunch(
  //                        requestCode: Int,
  //                        contract: ActivityResultContract<I, O>,
  //                        input: I,
  //                        options: ActivityOptionsCompat?
  //                    ) {
  //                        // don't launch an activity, just respond with the test Uri
  //                        val intent = Intent()
  //                        this.dispatchResult(requestCode, Activity.RESULT_CANCELED, intent)
  //                    }
  //                }
  //            }
  //
  //            var launcher : ActivityResultLauncher<Intent>? = null
  //            composeTestRule.setContent {
  //                CompositionLocalProvider(LocalActivityResultRegistryOwner provides registryOwner)
  // {
  //                    SignUpScreen()
  //                }
  //            }
  //
  //            // Verify that the error dialog is displayed
  //            ComposeScreen.onComposeScreen<SignupScreen>(composeTestRule) {
  //                signupErrorDisplayText {
  //                    assertIsDisplayed()
  //                    assertTextEquals("Sign in Failed. Please try again.")
  //                }
  //
  //                signupErrorTitle {
  //                    assertIsDisplayed()
  //                    assertTextEquals("Sign in Failed")
  //                }
  //            }

     */
}
