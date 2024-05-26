package com.github.se.eventradar.chat

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.eventradar.model.User
import com.github.se.eventradar.model.message.Message
import com.github.se.eventradar.model.message.MessageHistory
import com.github.se.eventradar.screens.ChatScreen
import com.github.se.eventradar.ui.chat.ChatScreen
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.ui.navigation.Route
import com.github.se.eventradar.viewmodel.ChatUiState
import com.github.se.eventradar.viewmodel.ChatViewModel
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.just
import io.mockk.unmockkAll
import io.mockk.verify
import java.time.LocalDateTime
import kotlinx.coroutines.flow.MutableStateFlow
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
  @RelaxedMockK lateinit var chatViewModel: ChatViewModel

  private val opponentUser =
      User(
          userId = "2",
          birthDate = "01/01/2000",
          email = "",
          firstName = "Test",
          lastName = "2",
          phoneNumber = "",
          accountStatus = "active",
          eventsAttendeeList = mutableListOf(),
          eventsHostList = mutableListOf(),
          friendsList = mutableListOf(),
          profilePicUrl =
              "https://firebasestorage.googleapis.com/v0/b/event-radar-e6a76.appspot.com/o/Profile_Pictures%2Fplaceholder.png?alt=media&token=ba4b4efb-ff45-4617-b60f-3789e8fb75b6",
          qrCodeUrl = "",
          bio = "",
          username = "Test2")

  private val chatUIState =
      MutableStateFlow(
          ChatUiState(
              userId = "1",
              messageHistory =
                  MessageHistory(
                      "1",
                      "2",
                      "2",
                      true,
                      false,
                      mutableListOf(
                          Message(
                              sender = "1",
                              content = "Test Message 1",
                              dateTimeSent = LocalDateTime.now(),
                              id = "1"),
                          Message(
                              sender = "2",
                              content = "Test Message 2",
                              dateTimeSent = LocalDateTime.now(),
                              id = "2"),
                      ),
                      "1"),
              opponentUser,
          ))

  @Before
  fun testSetup() {
    every { chatViewModel.uiState } returns chatUIState
    every { chatViewModel.onMessageBarInputChange(any()) } answers
        {
          chatUIState.value = chatUIState.value.copy(messageBarInput = firstArg())
        }
    every { chatViewModel.onMessageSend() } answers
        {
          val newMessages = chatUIState.value.messageHistory.messages
          newMessages.add(
              Message(
                  sender = "1",
                  content = chatUIState.value.messageBarInput,
                  dateTimeSent = LocalDateTime.now(),
                  id = "3"))
          chatUIState.value =
              chatUIState.value.copy(
                  messageHistory = chatUIState.value.messageHistory.copy(messages = newMessages))
          chatUIState.value = chatUIState.value.copy(messageBarInput = "")
        }
    coEvery { chatViewModel.getMessages() } just Runs

    composeTestRule.setContent { ChatScreen(chatViewModel, mockNavActions) }
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
        chatInputField { assertIsDisplayed() }
        chatInputPlaceholder { assertIsDisplayed() }
        chatInputSendButton { assertIsDisplayed() }
        chatInputSendButtonIcon { assertIsDisplayed() }

        // Check chat bubbles
        receivedChatBubble { assertIsDisplayed() }
        receivedChatBubbleText { assertIsDisplayed() }
        sentChatBubble { assertIsDisplayed() }
        sentChatBubbleText { assertIsDisplayed() }
      }
    }
  }

  @Test
  fun insertNewMessageWorks() = run {
    onComposeScreen<ChatScreen>(composeTestRule) {
      step("Type message") {
        chatInputField {
          assertIsDisplayed()
          performTextInput("Test Message 3")
        }
        chatViewModel.onMessageBarInputChange("Test Message 3")
        chatInputSendButton { performClick() }
        chatViewModel.onMessageSend()
      }
      step("Check if message is displayed") {
        chatScreenMessagesList { assertIsDisplayed() }
        onNode { hasText("Test Message 3") }.assertIsDisplayed()
      }
    }
  }

  @Test
  fun goBackButtonWorks() = run {
    onComposeScreen<ChatScreen>(composeTestRule) {
      step("Click back button") { chatAppBarBackArrow { performClick() } }
      step("Check if navigation action is called") {
        verify { mockNavActions.goBack() }
        verify { mockNavActions.navController }
        confirmVerified(mockNavActions)
      }
    }
  }

  @Test
  fun clickOpponentUserNameWorks() = run {
    onComposeScreen<ChatScreen>(composeTestRule) {
      step("Click opponent profile picture") { chatAppBarTitleColumn { performClick() } }
      step("Check if navigation action is called") {
        verify { mockNavActions.navController.navigate("${Route.PROFILE}/${opponentUser.userId}") }
        confirmVerified(mockNavActions)
      }
    }
  }
}
