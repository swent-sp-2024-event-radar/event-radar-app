package com.github.se.eventradar.messages

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.eventradar.model.User
import com.github.se.eventradar.model.repository.message.IMessageRepository
import com.github.se.eventradar.model.repository.message.MockMessageRepository
import com.github.se.eventradar.model.repository.user.MockUserRepository
import com.github.se.eventradar.screens.MessagesScreen
import com.github.se.eventradar.ui.messages.MessagesScreen
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.viewmodel.MessagesViewModel
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EmptyMessagesTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()

  // This rule automatic initializes lateinit properties with @MockK, @RelaxedMockK, etc.
  @get:Rule val mockkRule = MockKRule(this)

  // Relaxed mocks methods have a default implementation returning values
  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  private lateinit var mockMessageRepository: IMessageRepository
  private lateinit var mockUserRepository: MockUserRepository
  private lateinit var mockMessagesViewModel: MessagesViewModel

  @Before
  fun testSetup() = runTest {
    mockMessageRepository = MockMessageRepository()
    mockUserRepository = MockUserRepository()

    mockUserRepository.updateCurrentUserId("new")

    // Add user that has no friends
    mockUserRepository.addUser(
        User(
            userId = "new",
            birthDate = "01/01/2000",
            email = "",
            firstName = "Test",
            lastName = "New",
            phoneNumber = "TestPhoneNew",
            accountStatus = "active",
            eventsAttendeeList = mutableListOf(),
            eventsHostList = mutableListOf(),
            friendsList = mutableListOf(),
            profilePicUrl =
                "https://firebasestorage.googleapis.com/v0/b/event-radar-e6a76.appspot.com/o/Profile_Pictures%2Fplaceholder.png?alt=media&token=ba4b4efb-ff45-4617-b60f-3789e8fb75b6",
            qrCodeUrl = "",
            username = "TestNew"))

    mockMessagesViewModel = MessagesViewModel(mockMessageRepository, mockUserRepository)

    composeTestRule.setContent { MessagesScreen(mockMessagesViewModel, mockNavActions) }
  }

  @After fun testTeardown() = runTest { unmockkAll() }

  @Test
  fun noMessagesFoundDisplaysCorrectly() = run {
    onComposeScreen<MessagesScreen>(composeTestRule) {
      step("Check that no message found text is shown") { noMessagesFound { assertIsDisplayed() } }
    }
  }

  @Test
  fun noFriendsFoundDisplaysCorrectly() = run {
    onComposeScreen<MessagesScreen>(composeTestRule) {
      step("Navigate to friends tab") { friendsTab { performClick() } }
      step("Check that no friends found text is shown") { noFriendsFound { assertIsDisplayed() } }
    }
  }
}
