package com.github.se.eventradar.messages

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.User
import com.github.se.eventradar.model.message.Message
import com.github.se.eventradar.model.message.MessageHistory
import com.github.se.eventradar.model.repository.message.IMessageRepository
import com.github.se.eventradar.model.repository.message.MockMessageRepository
import com.github.se.eventradar.model.repository.user.IUserRepository
import com.github.se.eventradar.model.repository.user.MockUserRepository
import com.github.se.eventradar.screens.MessagesScreen
import com.github.se.eventradar.ui.messages.MessagesScreen
import com.github.se.eventradar.ui.messages.MessagesUiState
import com.github.se.eventradar.ui.messages.MessagesViewModel
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.google.firebase.auth.FirebaseAuth
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import java.time.LocalDateTime
import kotlinx.coroutines.flow.MutableStateFlow
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
  private lateinit var mockUserRepository: IUserRepository
  private lateinit var mockMessagesViewModel: MessagesViewModel

  private val sampleMessagesList =
      MutableStateFlow(
          MessagesUiState(
              userId = "1",
              messageList =
                  List(10) {
                    MessageHistory(
                        user1 = "1",
                        user2 = "$it",
                        latestMessageId = "1",
                        user1ReadMostRecentMessage = false,
                        user2ReadMostRecentMessage = true,
                        messages =
                            mutableListOf(
                                Message(
                                    sender = "$it",
                                    content = "Test Message",
                                    dateTimeSent = LocalDateTime.now(),
                                    id = "1")))
                  }))

  @Before
  fun testSetup() = runTest {
    mockMessageRepository = MockMessageRepository()
    mockUserRepository = MockUserRepository()

    mockkStatic(FirebaseAuth::class)
    every { FirebaseAuth.getInstance().currentUser!!.uid } returns "1"

    for (i in 0..9) {
      mockUserRepository.addUser(
          User(
              userId = "$i",
              birthDate = "01/01/2000",
              email = "",
              firstName = "Test",
              lastName = "$i",
              phoneNumber = "",
              accountStatus = "active",
              eventsAttendeeSet = mutableSetOf(),
              eventsHostSet = mutableSetOf(),
              friendsSet = mutableSetOf(),
              profilePicUrl = "",
              qrCodeUrl = "",
              username = "Test$i"))
    }

    for (i in 0..9) {
      val mh = mockMessageRepository.createNewMessageHistory("1", "$i")

      mockMessageRepository.addMessage(
          Message(
              sender = "$i",
              content = "Test Message",
              dateTimeSent = LocalDateTime.now(),
              id = "1"),
          (mh as Resource.Success).data,
      )
    }

    mockMessagesViewModel = MessagesViewModel(mockMessageRepository, mockUserRepository)

    composeTestRule.setContent { MessagesScreen(mockMessagesViewModel, mockNavActions) }
  }

  @After fun testTeardown() = runTest { unmockkAll() }

  @Test
  fun screenDisplaysAllElementsCorrectly() = run {
    onComposeScreen<MessagesScreen>(composeTestRule) {
      step("Check if all elements are displayed") {
        logo { assertIsDisplayed() }
        tabs { assertIsDisplayed() }
        messagesTab { assertIsDisplayed() }
        contactsTab { assertIsDisplayed() }
        messagesList { assertIsDisplayed() }
        messagePreviewItem {
          assertIsDisplayed()
          assertHasClickAction()
        }
        bottomNav { assertIsDisplayed() }
      }
    }
  }
}
