package com.github.se.eventradar.viewProfile

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.eventradar.model.User
import com.github.se.eventradar.screens.ViewFriendsProfileScreen
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.ui.viewProfile.ViewFriendsProfileUi
import com.github.se.eventradar.viewmodel.ViewFriendsProfileUiState
import com.github.se.eventradar.viewmodel.ViewFriendsProfileViewModel
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
class ViewFriendsProfileTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()
  // This rule automatic initializes lateinit properties with @MockK, @RelaxedMockK, etc.
  @get:Rule val mockkRule = MockKRule(this)
  // Relaxed mocks methods have a default implementation returning values
  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  @RelaxedMockK lateinit var mockViewFriendsProfileViewModel: ViewFriendsProfileViewModel
  private val mockUser =
      User(
          userId = "1",
          birthDate = "01/01/2000",
          email = "test@example.com",
          firstName = "John",
          lastName = "Doe",
          phoneNumber = "1234567890",
          accountStatus = "active",
          eventsAttendeeList = mutableListOf("event1", "event2"),
          eventsHostList = mutableListOf("event3"),
          friendsList = mutableListOf("2"),
          profilePicUrl = "http://example.com/Profile_Pictures/1",
          qrCodeUrl = "http://example.com/QR_Codes/1",
          bio = "",
          username = "johndoe")
  private val mockFriend =
      User(
          userId = "2",
          birthDate = "02/02/2002",
          email = "friend@example.com",
          firstName = "Jim",
          lastName = "Smith",
          phoneNumber = "1234567890",
          accountStatus = "active",
          eventsAttendeeList = mutableListOf("event1", "event2"),
          eventsHostList = mutableListOf("event3"),
          friendsList = mutableListOf("1"),
          profilePicUrl = "http://example.com/Profile_Pictures/2",
          qrCodeUrl = "http://example.com/QR_Codes/2",
          bio = "",
          username = "jimsmith")
  private val sampleUiState =
      MutableStateFlow(
          ViewFriendsProfileUiState(
              friendProfilePicLink = "",
              friendFirstName = "Jim",
              friendUserName = "Smith",
              bio = "I am Jim Smith and I love the Smiths (the band)."))

  @Before
  fun testSetup() {
    every { mockViewFriendsProfileViewModel.getFriendProfileDetails() } returns Unit
    every { mockViewFriendsProfileViewModel.uiState } returns sampleUiState
    composeTestRule.setContent {
      ViewFriendsProfileUi(
          viewModel = mockViewFriendsProfileViewModel, navigationActions = mockNavActions)
    }
  }

  @Test
  fun screenDisplaysAllElementsCorrectly() = run {
    ComposeScreen.onComposeScreen<ViewFriendsProfileScreen>(composeTestRule) {
      chatButton { assertIsDisplayed() }
      goBackButton { assertIsDisplayed() }
      bottomNav { assertIsDisplayed() }
      centeredViewProfileColumn { assertIsDisplayed() }
      friendProfilePic { assertIsDisplayed() }
      friendName { assertIsDisplayed() }
      friendUserName { assertIsDisplayed() }
      leftAlignedViewProfileColumn { assertIsDisplayed() }
      bioLabelText { assertIsDisplayed() }
      bioInfoText { assertIsDisplayed() }
    }
  }

  @Test
  fun goBackButtonTriggersBackNavigation() = run {
    ComposeScreen.onComposeScreen<ViewFriendsProfileScreen>(composeTestRule) {
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
}
