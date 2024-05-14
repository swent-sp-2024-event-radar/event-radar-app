package com.github.se.eventradar.chat

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.User
import com.github.se.eventradar.model.message.Message
import com.github.se.eventradar.model.repository.message.IMessageRepository
import com.github.se.eventradar.model.repository.message.MockMessageRepository
import com.github.se.eventradar.model.repository.user.MockUserRepository
import com.github.se.eventradar.screens.ChatScreen
import com.github.se.eventradar.ui.chat.ChatScreen
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.viewmodel.ChatViewModel
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
class ChatTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()

  // This rule automatic initializes lateinit properties with @MockK, @RelaxedMockK, etc.
  @get:Rule val mockkRule = MockKRule(this)

  // Relaxed mocks methods have a default implementation returning values
  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  private lateinit var mockMessageRepository: IMessageRepository
  private lateinit var mockUserRepository: MockUserRepository
  private lateinit var mockChatViewModel: ChatViewModel

  @Before
  fun testSetup() = runTest {
    mockMessageRepository = MockMessageRepository()
    mockUserRepository = MockUserRepository()

    mockUserRepository.updateCurrentUserId("1")

    // Starting at 2 to avoid conflicts with the current user
    for (i in 2..9) {
      mockUserRepository.addUser(
          User(
              userId = "$i",
              birthDate = "01/01/2000",
              email = "",
              firstName = "Test",
              lastName = "$i",
              phoneNumber = "",
              accountStatus = "active",
              eventsAttendeeList = mutableListOf(),
              eventsHostList = mutableListOf(),
              friendsList = mutableListOf(),
              profilePicUrl =
                  "https://firebasestorage.googleapis.com/v0/b/event-radar-e6a76.appspot.com/o/Profile_Pictures%2Fplaceholder.png?alt=media&token=ba4b4efb-ff45-4617-b60f-3789e8fb75b6",
              qrCodeUrl = "",
              username = "Test$i"))

      val mh = mockMessageRepository.createNewMessageHistory("1", "$i")

      mockMessageRepository.addMessage(
          Message(
              sender = "$i",
              content = "Test Message",
              dateTimeSent = LocalDateTime.now(),
              id = "$i"),
          (mh as Resource.Success).data,
      )
    }

    mockChatViewModel = ChatViewModel(mockMessageRepository, mockUserRepository)

    composeTestRule.setContent { ChatScreen(mockChatViewModel, mockNavActions) }
  }

  @After fun testTeardown() = runTest { unmockkAll() }

  @Test
  fun screenDisplaysAllElementsCorrectly() = run {
    onComposeScreen<ChatScreen>(composeTestRule) {
      step("Check if all elements are displayed") {
        chatAppBar { assertIsDisplayed() }
        chatAppBarTitle { assertIsDisplayed() }
        chatAppBarTitleSurface { assertIsDisplayed() }
        chatAppBarTitleImage { assertIsDisplayed() }
        chatAppBarTitleColumn {
          assertIsDisplayed()
          assertHasClickAction()
        }

        chatAppBarBackArrow {
          assertIsDisplayed()
          assertHasClickAction()
        }

        bottomNav { assertIsDisplayed() }
        chatScreenMessagesList { assertIsDisplayed() }

        //        receivedMessageText { assertIsDisplayed() }
        //        messageTimeRowText { assertIsDisplayed() }
        //        messageTimeRowIcon { assertIsDisplayed() }
        //
        //        chatInputField { assertIsDisplayed() }
        //        chatInputPlaceholder { assertIsDisplayed() }
        //        chatInputSendButtonIcon { assertIsDisplayed() }
        //        chatInputCameraButtonIcon { assertIsDisplayed() }
      }
    }
  }
}
