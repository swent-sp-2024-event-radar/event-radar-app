package com.github.se.eventradar.login

import android.app.Activity
import android.content.Intent
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.toPackage
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.eventradar.screens.LoginScreen
import com.github.se.eventradar.ui.MainActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.app.ActivityOptionsCompat
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import io.mockk.every
import io.mockk.mockk
import android.net.Uri
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@RunWith(AndroidJUnit4::class)
class LoginTest : TestCase() {
    @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

    // The IntentsTestRule simply calls Intents.init() before the @Test block
    // and Intents.release() after the @Test block is completed. IntentsTestRule
    // is deprecated, but it was MUCH faster than using IntentsRule in our tests
    @get:Rule val intentsTestRule = IntentsTestRule(MainActivity::class.java)

    @Test
    fun titleAndButtonAreCorrectlyDisplayed() {
        ComposeScreen.onComposeScreen<LoginScreen>(composeTestRule) {
            // Test the UI elements
            loginLogo {
                assertIsDisplayed()
            }
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
        ComposeScreen.onComposeScreen<LoginScreen>(composeTestRule) {
            loginButton {
                assertIsDisplayed()
                performClick()
            }

            // assert that an Intent resolving to Google Mobile Services has been sent (for sign-in)
            intended(toPackage("com.google.android.gms"))
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
//                CompositionLocalProvider(LocalActivityResultRegistryOwner provides registryOwner) {
//                    LoginScreen()
//                }
//            }
//
//            // Verify that the error dialog is displayed
//            ComposeScreen.onComposeScreen<LoginScreen>(composeTestRule) {
//                loginErrorDisplayText {
//                    assertIsDisplayed()
//                    assertTextEquals("Sign in Failed. Please try again.")
//                }
//
//                loginErrorTitle {
//                    assertIsDisplayed()
//                    assertTextEquals("Sign in Failed")
//                }
//            }
//        }
//    }
}