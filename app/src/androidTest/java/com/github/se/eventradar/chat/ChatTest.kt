package com.github.se.eventradar.chat

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
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

    //      // TO DO: Once VM is done, re-write tests to mock the message and user repo
    //      // Create mock user who receives messages
    //      mockUserRepository.addUser(
    //          User(
    //              userId = "2",
    //              birthDate = "01/01/2000",
    //              email = "",
    //              firstName = "Test",
    //              lastName = "2",
    //              phoneNumber = "",
    //              accountStatus = "active",
    //              eventsAttendeeList = mutableListOf(),
    //              eventsHostList = mutableListOf(),
    //              friendsList = mutableListOf(),
    //              profilePicUrl =
    //
    // "https://firebasestorage.googleapis.com/v0/b/event-radar-e6a76.appspot.com/o/Profile_Pictures%2Fplaceholder.png?alt=media&token=ba4b4efb-ff45-4617-b60f-3789e8fb75b6",
    //              qrCodeUrl = "",
    //              username = "Test2"))
    //
    //      val mh = mockMessageRepository.createNewMessageHistory("1", "2")
    //
    //      // create some messages between the two users
    //      for (i in 1..5) {
    //          mockMessageRepository.addMessage(
    //          Message(
    //              sender = "$i",
    //              content = "Test Message $i",
    //              dateTimeSent = LocalDateTime.now(),
    //              id = "$i"),
    //          (mh as Resource.Success).data,
    //      )
    //    }

    mockChatViewModel = ChatViewModel(mockMessageRepository, mockUserRepository)

    composeTestRule.setContent { ChatScreen(mockChatViewModel, mockNavActions) }
  }

  @After fun testTeardown() = runTest { unmockkAll() }

  @Test
  fun screenDisplaysAllElementsCorrectlyEmptyMessages() = run {
    onComposeScreen<ChatScreen>(composeTestRule) {
      step("Check if all elements are displayed") {
        chatAppBar { assertIsDisplayed() }
        chatAppBarTitle { assertIsDisplayed() }
        chatAppBarTitleImage { assertIsDisplayed() }
        chatAppBarTitleColumn {
          assertIsDisplayed()
          assertHasClickAction()
        }
        chatAppBarBackArrow {
          assertIsDisplayed()
          assertHasClickAction()
        }

        chatScreenMessagesList { assertIsDisplayed() }
        chatInput { assertIsDisplayed() }
        bottomNav { assertIsDisplayed() }
      }
    }
  }

  //  @Test
  //  fun testSendingMessage() = run {
  //    val testMessage = "Hello Test!"
  //
  //    onComposeScreen<ChatScreen>(composeTestRule) {
  //      step("Enter message and send") {
  //        chatInputField {
  //          assertIsDisplayed()
  //          performTextInput(testMessage)
  //        }
  //        chatInputSendButtonIcon {
  //          assertIsDisplayed()
  //          performClick()
  //        }
  //      }
  //
  //      step("Verify message is displayed in list") {
  //        //                chatScreenMessagesList.onNodeWithText(testMessage).assertIsDisplayed()
  //        chatInputField.assertTextEquals("") // Check if input field is cleared
  //      }
  //    }
  //  }
}
