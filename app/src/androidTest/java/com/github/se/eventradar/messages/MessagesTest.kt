package com.github.se.eventradar.messages

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.User
import com.github.se.eventradar.model.message.Message
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
import java.time.LocalDateTime
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MessagesTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

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

    mockUserRepository.updateCurrentUserId("1")

    // Starting at 2 to avoid conflicts with the current user
    for (i in 1..9) {
      mockUserRepository.addUser(
          User(
              userId = "$i",
              birthDate = "01/01/2000",
              email = "",
              firstName = "Test",
              lastName = "$i",
              phoneNumber = "TestPhone$i",
              accountStatus = "active",
              eventsAttendeeList = mutableListOf(),
              eventsHostList = mutableListOf(),
              friendsList = if (i > 1) mutableListOf() else List(8) { "${it + 2}" }.toMutableList(),
              profilePicUrl =
                  "https://firebasestorage.googleapis.com/v0/b/event-radar-e6a76.appspot.com/o/Profile_Pictures%2Fplaceholder.png?alt=media&token=ba4b4efb-ff45-4617-b60f-3789e8fb75b6",
              qrCodeUrl = "",
              username = "Test$i"))

      // Add messages to the mock repository (except for the first user)
      if (i > 1) {
        val mh = mockMessageRepository.createNewMessageHistory("1", "$i")

        mockMessageRepository.addMessage(
            Message(
                sender = "$i",
                content = "Test Message",
                dateTimeSent = LocalDateTime.parse("2021-01-01T00:00:00"),
                id = "1"),
            (mh as Resource.Success).data,
        )
      }
    }

    mockMessagesViewModel = MessagesViewModel(mockMessageRepository, mockUserRepository)

    composeTestRule.setContent { MessagesScreen(mockMessagesViewModel, mockNavActions) }
  }

  @After fun testTeardown() = runTest { unmockkAll() }

  @Test
  fun messagesScreenDisplaysAllElementsCorrectly() = run {
    onComposeScreen<MessagesScreen>(composeTestRule) {
      step("Check if all elements are displayed") {
        logo { assertIsDisplayed() }
        tabs { assertIsDisplayed() }
        messagesTab { assertIsDisplayed() }
        friendsTab { assertIsDisplayed() }
        messagesList { assertIsDisplayed() }
        messagePreviewItem {
          assertIsDisplayed()
          assertHasClickAction()
        }
        bottomNav { assertIsDisplayed() }
        profilePic { assertIsDisplayed() }
        recipientName { assertIsDisplayed() }
        messageContent { assertIsDisplayed() }
        messageTime { assertIsDisplayed() }
      }
    }
  }

  @Test
  fun friendsScreenDisplaysAllElementsCorrectly() = run {
    onComposeScreen<MessagesScreen>(composeTestRule) {
      step("Navigate to friends tab") { friendsTab { performClick() } }
      step("Check if all elements are displayed") {
        friendsList { assertIsDisplayed() }
        friendPreviewItem {
          assertIsDisplayed()
          assertHasClickAction()
        }
        friendProfilePic { assertIsDisplayed() }
        friendName { assertIsDisplayed() }
        friendPhoneNumber { assertIsDisplayed() }
      }
    }
  }
}
