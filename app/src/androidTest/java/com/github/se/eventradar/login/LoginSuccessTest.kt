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
import com.github.se.eventradar.model.User
import com.github.se.eventradar.model.repository.user.MockUserRepository
import com.github.se.eventradar.screens.LoginScreen
import com.github.se.eventradar.ui.login.LoginScreen
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.ui.navigation.Route
import com.github.se.eventradar.viewmodel.LoginViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import dagger.hilt.android.testing.HiltAndroidTest
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class LoginSuccessTest : TestCase() {
  @get:Rule val composeTestRule = createComposeRule()

  // This rule automatic initializes lateinit properties with @MockK, @RelaxedMockK, etc.
  @get:Rule val mockkRule = MockKRule(this)

  // Relaxed mocks methods have a default implementation returning values
  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  private var mockUserRepository: MockUserRepository = MockUserRepository()

  @Before
  fun setUp() {
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
                    this.dispatchResult(requestCode, Activity.RESULT_OK, intent)
                  }
                }
          }

      CompositionLocalProvider(LocalActivityResultRegistryOwner provides registryOwner) {
        // any composable inside this block will now use our mock ActivityResultRegistry
        LoginScreen(LoginViewModel(mockUserRepository), mockNavActions)
      }
    }
  }

  @Test
  fun homeScreenOpensOnSuccessfulLogin() = runTest {
    mockUserRepository.addUser(
        User(
            userId = "1",
            birthDate = "01/01/2000",
            email = "",
            firstName = "John",
            lastName = "Doe",
            phoneNumber = "1234567890",
            accountStatus = "active",
            eventsAttendeeSet = mutableSetOf(),
            eventsHostSet = mutableSetOf(),
            friendsSet = mutableSetOf(),
            profilePicUrl = "",
            qrCodeUrl = "",
            username = "johndoe"))
    mockkStatic(FirebaseAuth::class)
    every { FirebaseAuth.getInstance() } returns mockk<FirebaseAuth>(relaxed = true)
    every { FirebaseAuth.getInstance().currentUser } returns mockk<FirebaseUser>(relaxed = true)
    every { FirebaseAuth.getInstance().currentUser?.uid } returns "1"
    ComposeScreen.onComposeScreen<LoginScreen>(composeTestRule) {
      loginButton {
        assertIsDisplayed()
        performClick()
      }

      verify { mockNavActions.navController.navigate(Route.HOME) }
      confirmVerified(mockNavActions)
    }
    unmockkAll()
  }

  @Test
  fun signUpScreenOpensIfUserDoesNotExist() = runTest {
    mockkStatic(FirebaseAuth::class)
    every { FirebaseAuth.getInstance() } returns mockk<FirebaseAuth>(relaxed = true)
    every { FirebaseAuth.getInstance().currentUser } returns mockk<FirebaseUser>(relaxed = true)
    every { FirebaseAuth.getInstance().currentUser?.uid } returns "2"
    ComposeScreen.onComposeScreen<LoginScreen>(composeTestRule) {
      loginButton {
        assertIsDisplayed()
        performClick()
      }

      verify { mockNavActions.navController.navigate(Route.SIGNUP) }
      confirmVerified(mockNavActions)
    }
    unmockkAll()
  }

  @Test
  fun errorDialogBoxShowsWhenLoginFails() = runTest {
    mockkStatic(FirebaseAuth::class)
    every { FirebaseAuth.getInstance() } returns mockk<FirebaseAuth>(relaxed = true)
    every { FirebaseAuth.getInstance().currentUser } returns null
    ComposeScreen.onComposeScreen<LoginScreen>(composeTestRule) {
      loginButton {
        assertIsDisplayed()
        performClick()
      }

      errorDialog { assertIsDisplayed() }
    }
    unmockkAll()
  }

  @Test
  fun clickingSignupTextNavigatesToSignupScreen() = runTest {
    ComposeScreen.onComposeScreen<LoginScreen>(composeTestRule) {
      signUpButton {
        assertIsDisplayed()
        performClick()
      }

      verify { mockNavActions.navController.navigate(Route.SIGNUP) }
      confirmVerified(mockNavActions)
    }
  }
}
