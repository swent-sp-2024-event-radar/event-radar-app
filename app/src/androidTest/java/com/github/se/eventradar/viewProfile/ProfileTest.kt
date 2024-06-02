package com.github.se.eventradar.viewProfile

import android.content.Intent
import android.net.Uri
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.eventradar.screens.ProfileScreen
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.ui.viewProfile.ProfileUi
import com.github.se.eventradar.viewmodel.ProfileUiState
import com.github.se.eventradar.viewmodel.ProfileViewModel
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
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

  @RelaxedMockK lateinit var mockProfileViewModel: ProfileViewModel
  private val sampleUiState =
      MutableStateFlow(
          ProfileUiState(
              profilePicUri = Uri.EMPTY,
              firstName = "Jim",
              lastName = "Smith",
              username = "jimsmith",
              bio = "I am Jim Smith and I love the Smiths (the band).",
              phoneNumber = "123456789",
              birthDate = "02/02/2002"))

  @Before
  fun testSetup() {
    every { mockProfileViewModel.getProfileDetails() } returns Unit
    every { mockProfileViewModel.uiState } returns sampleUiState
  }

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
      bioInfoText { assertIsDisplayed() }
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
      bioInfoText { assertIsDisplayed() }
      phoneNumberBirthDateRow { assertIsDisplayed() }
      phoneNumberColumn { assertIsDisplayed() }
      // phoneNumberLabelText { assertIsDisplayed() }
      // phoneNumberInfoText { assertIsDisplayed() }
      birthDateColumn { assertIsDisplayed() }
      // birthDateLabelText { assertIsDisplayed() }
      // birthDateInfoText { assertIsDisplayed() }
      // phoneNumberBirthDateSpacer { assertIsDisplayed() }
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
        assertIsEnabled()

        // act: click the edit button
        performClick()
      }
      goBackButton { assertIsDisplayed() }
      editProfile { assertIsDisplayed() }
      profilePic {
        assertIsDisplayed()
        assertHasClickAction()
      }
      firstNameTextField {
        assertIsDisplayed()
        performTextInput("Jim")
      }
      lastNameTextField {
        assertIsDisplayed()
        performTextInput("Smith")
      }
      usernameTextField {
        assertIsDisplayed()
        performTextInput("jimsmith")
      }
      bioTextField {
        assertIsDisplayed()
        performTextInput("I am Jim Smith and I love the Smiths (the band).")
      }
      phoneNumberTextField {
        assertIsDisplayed()
        performTextInput("123456789")
      }
      birthDateTextField {
        assertIsDisplayed()
        performTextInput("02/02/2002")
      }
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

  @Test
  fun saveButtonWorks() = run {
    composeTestRule.setContent {
      ProfileUi(
          isPublicView = false,
          viewModel = mockProfileViewModel,
          navigationActions = mockNavActions)
    }

    ComposeScreen.onComposeScreen<ProfileScreen>(composeTestRule) {
      editButton {
        assertIsDisplayed()
        performClick()
      }
      firstNameTextField {
        assertIsDisplayed()
        performTextInput("Jimmy")
      }
      saveButton {
        assertIsDisplayed()
        performClick()
      }

      verify { mockProfileViewModel.validateFields() }
      verify { mockProfileViewModel.getProfileDetails() }
      verify { mockProfileViewModel.getUiState() }
      verify { mockProfileViewModel.onFirstNameChanged("Jim") }
      verify { mockProfileViewModel.onFirstNameChanged("JimmyJim") }
      confirmVerified(mockProfileViewModel)
    }
  }
}
