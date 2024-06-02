package com.github.se.eventradar.viewProfile

import android.content.Intent
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.eventradar.model.User
import com.github.se.eventradar.model.repository.user.MockUserRepository
import com.github.se.eventradar.screens.ProfileScreen
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.ui.viewProfile.ProfileUi
import com.github.se.eventradar.viewmodel.ProfileViewModel
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.confirmVerified
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()
  // This rule automatic initializes lateinit properties with @MockK, @RelaxedMockK, etc.
  @get:Rule val mockkRule = MockKRule(this)
  // Relaxed mocks methods have a default implementation returning values
  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  private lateinit var mockUserRepository: MockUserRepository
  private lateinit var mockProfileViewModel: ProfileViewModel

  @Before
  fun testSetup() = runTest {
    mockUserRepository = MockUserRepository()

    mockUserRepository.updateCurrentUserId("1")

    mockUserRepository.addUser(
        User(
            userId = "Test",
            birthDate = "01/01/2000",
            email = "",
            firstName = "Test",
            lastName = "Test",
            phoneNumber = "123456789",
            accountStatus = "active",
            eventsAttendeeList = mutableListOf(),
            eventsHostList = mutableListOf(),
            friendsList = mutableListOf(),
            profilePicUrl =
                "https://firebasestorage.googleapis.com/v0/b/event-radar-e6a76.appspot.com/o/Profile_Pictures%2Fplaceholder.png?alt=media&token=ba4b4efb-ff45-4617-b60f-3789e8fb75b6",
            qrCodeUrl = "",
            bio = "",
            username = "1"))

    mockProfileViewModel = ProfileViewModel(mockUserRepository, "1")

    mockProfileViewModel.getProfileDetails()
  }

  @After fun testTeardown() = runTest { unmockkAll() }

  @Test
  fun screenDisplaysAllElementsCorrectlyWhenPublic() = run {
    composeTestRule.setContent {
      ProfileUi(
          isPublicView = true, viewModel = mockProfileViewModel, navigationActions = mockNavActions)
    }

    ComposeScreen.onComposeScreen<ProfileScreen>(composeTestRule) {
      chatButton { assertIsDisplayed() }
      goBackButton { assertIsDisplayed() }
      bottomNav { assertIsDisplayed() }
      centeredViewProfileColumn { assertIsDisplayed() }
      profilePic { assertIsDisplayed() }
      name { assertIsDisplayed() }
      username { assertIsDisplayed() }
      leftAlignedViewProfileColumn { assertIsDisplayed() }
      bioLabelText { assertIsDisplayed() }
      // In public view, phone number and birth date should not be displayed
      phoneNumberBirthDateRow { assertDoesNotExist() }
    }
  }

  @Test
  fun screenDisplaysAllElementsCorrectlyWhenPrivateNotInEditMode() = run {
    composeTestRule.setContent {
      ProfileUi(
          isPublicView = false,
          viewModel = mockProfileViewModel,
          navigationActions = mockNavActions)
    }

    ComposeScreen.onComposeScreen<ProfileScreen>(composeTestRule) {
      editButton { assertIsDisplayed() }
      logo { assertIsDisplayed() }
      profilePic { assertIsDisplayed() }
      name { assertIsDisplayed() }
      username { assertIsDisplayed() }
      bioLabelText { assertIsDisplayed() }
      phoneNumberBirthDateRow { assertIsDisplayed() }
      phoneNumberColumn { assertIsDisplayed() }
      birthDateColumn { assertIsDisplayed() }
    }
  }

  @Test
  fun screenDisplaysAllElementsCorrectlyWhenPrivateInEditMode() = run {
    composeTestRule.setContent {
      ProfileUi(
          isPublicView = false,
          viewModel = mockProfileViewModel,
          navigationActions = mockNavActions)
    }

    ComposeScreen.onComposeScreen<ProfileScreen>(composeTestRule) {
      editButton {
        // arrange: verify the pre-conditions
        assertIsDisplayed()

        // act: click the edit button
        performClick()
      }
      goBackButton { assertIsDisplayed() }
      editProfile { assertIsDisplayed() }
      profilePic {
        assertIsDisplayed()
        assertHasClickAction()
      }
      firstNameTextField { assertIsDisplayed() }
      lastNameTextField { assertIsDisplayed() }
      usernameTextField { assertIsDisplayed() }
      bioTextField { assertIsDisplayed() }
      phoneNumberTextField { assertIsDisplayed() }
      birthDateTextField { assertIsDisplayed() }
      saveButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
    }
  }

  @Test
  fun goBackButtonTriggersBackNavigation() = run {
    composeTestRule.setContent {
      ProfileUi(
          isPublicView = true, viewModel = mockProfileViewModel, navigationActions = mockNavActions)
    }

    ComposeScreen.onComposeScreen<ProfileScreen>(composeTestRule) {
      goBackButton {
        // arrange: verify the pre-conditions
        assertIsDisplayed()
        assertIsEnabled()

        // act: go back !
        performClick()
      }
    }
    // assert: the nav action has been called
    verify { mockNavActions.goBack() }
    confirmVerified(mockNavActions)
  }

  @Test
  fun profilePicClickOpensGallery() = run {
    composeTestRule.setContent {
      ProfileUi(
          isPublicView = false,
          viewModel = mockProfileViewModel,
          navigationActions = mockNavActions)
    }

    Intents.init()
    ComposeScreen.onComposeScreen<ProfileScreen>(composeTestRule) {
      editButton {
        assertIsDisplayed()
        performClick()
      }
      profilePic {
        assertIsDisplayed()
        performClick()
      }

      Intents.intended(IntentMatchers.hasAction(Intent.ACTION_GET_CONTENT))
    }
    Intents.release()
  }
}
