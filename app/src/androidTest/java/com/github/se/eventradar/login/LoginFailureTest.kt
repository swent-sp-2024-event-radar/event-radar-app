package com.github.se.eventradar.login

import android.app.Activity
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.core.app.ActivityOptionsCompat
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
class LoginFailureTest : TestCase() {
  @get:Rule(order = 1) val composeTestRule = createComposeRule()

  @get:Rule(order = 0) val hiltRule = HiltAndroidRule(this)

  // This rule automatic initializes lateinit properties with @MockK, @RelaxedMockK, etc.
  @get:Rule val mockkRule = MockKRule(this)

  // Relaxed mocks methods have a default implementation returning values
  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  @Inject lateinit var userRepository: IUserRepository

  @Before
  fun setUp() {
    hiltRule.inject()

    // Launch the Login screen
    composeTestRule.setContent {
      val context = LocalContext.current
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
                    val intent = contract.createIntent(context, input)
                    this.dispatchResult(requestCode, Activity.RESULT_CANCELED, intent)
                  }
                }
          }

      CompositionLocalProvider(LocalActivityResultRegistryOwner provides registryOwner) {
        // any composable inside this block will now use our mock ActivityResultRegistry
        LoginScreen(LoginViewModel(userRepository), mockNavActions)
      }
    }
  }

  @Test
  fun dialogErrorBoxOpensWhenLoginFails() = run {
    ComposeScreen.onComposeScreen<LoginScreen>(composeTestRule) {
      step("Click on the Sign Up button") {
        loginButton {
          assertIsDisplayed()
          performClick()
        }
      }

      step("Check if the error dialog is displayed") {
        errorDialog { assertIsDisplayed() }
        errorDialogTitle { assertIsDisplayed() }
        errorDialogText { assertIsDisplayed() }
        errorDialogIcon { assertIsDisplayed() }

        errorDialogButton {
          assertIsDisplayed()
          assertHasClickAction()
        }
      }

      step("Click on the error dialog button") {
        errorDialogButton { performClick() }

        errorDialog { assertDoesNotExist() }
      }
    }
  }
}
