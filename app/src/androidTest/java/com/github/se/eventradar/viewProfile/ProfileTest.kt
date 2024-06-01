package com.github.se.eventradar.viewProfile

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.eventradar.model.User
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
            ProfileUiState(
                profilePicUrl = "",
                firstName = "Jim",
                username = "Smith",
                bio = "I am Jim Smith and I love the Smiths (the band)."))

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
            phoneNumberColumn { assertDoesNotExist() }
            phoneNumberLabelText { assertDoesNotExist() }
            phoneNumberInfoText { assertDoesNotExist() }
            birthDateColumn { assertDoesNotExist() }
            birthDateLabelText { assertDoesNotExist() }
            birthDateInfoText { assertDoesNotExist() }
        }
    }

    @Test
    fun screenDisplaysAllElementsCorrectlyWhenPrivate() = run {
        composeTestRule.setContent {
            ProfileUi(
                isPublicView = false, viewModel = mockProfileViewModel, navigationActions = mockNavActions)
        }

        composeTestRule.waitForIdle()

    ComposeScreen.onComposeScreen<ProfileScreen>(composeTestRule) {
      chatButton { assertDoesNotExist() } // Chat button should not be displayed in private view
      goBackButton {
        assertDoesNotExist()
      } // Go back button should not be displayed in private view
      bottomNav { assertIsDisplayed() }
      centeredViewProfileColumn { assertIsDisplayed() }
      profilePic { assertIsDisplayed() }
      name { assertIsDisplayed() }
      username { assertIsDisplayed() }
      leftAlignedViewProfileColumn { assertIsDisplayed() }
      bioLabelText { assertIsDisplayed() }
      bioInfoText { assertIsDisplayed() }
      phoneNumberBirthDateRow { assertIsDisplayed() }
      phoneNumberColumn { assertIsDisplayed() }
      phoneNumberLabelText { assertIsDisplayed() }
      birthDateColumn { assertIsDisplayed() }
      birthDateLabelText { assertIsDisplayed() }
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
}
