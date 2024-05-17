package com.github.se.eventradar

import android.util.Log
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.User
import com.github.se.eventradar.model.message.Message
import com.github.se.eventradar.model.repository.message.IMessageRepository
import com.github.se.eventradar.model.repository.message.MockMessageRepository
import com.github.se.eventradar.model.repository.user.IUserRepository
import com.github.se.eventradar.model.repository.user.MockUserRepository
import com.github.se.eventradar.viewmodel.ChatViewModel
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import java.time.LocalDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@ExperimentalCoroutinesApi
class ChatViewModelUnitTest {

  private lateinit var viewModel: ChatViewModel
  private lateinit var messageRepository: IMessageRepository
  private lateinit var userRepository: IUserRepository

  class MainDispatcherRule(
      private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
  ) : TestWatcher() {
    override fun starting(description: Description) {
      Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
      Dispatchers.resetMain()
    }
  }

  @get:Rule val mainDispatcherRule = MainDispatcherRule()

  private val mockMessage =
      Message(
          sender = "1",
          content = "Hello",
          dateTimeSent = LocalDateTime.parse("2021-01-01T00:00:00"),
          id = "1")

  private val nullUser =
      User(
          userId = "Default",
          birthDate = "Default",
          email = "Default",
          firstName = "Default",
          lastName = "Default",
          phoneNumber = "Default",
          accountStatus = "Default",
          eventsAttendeeList = mutableListOf(),
          eventsHostList = mutableListOf(),
          friendsList = mutableListOf(),
          profilePicUrl = "Default",
          qrCodeUrl = "Default",
          username = "Default")

  private val opponentId = "user2"
  private val opponent =
      User(
          userId = opponentId,
          birthDate = "01/01/2000",
          email = "test@example.com",
          firstName = "John",
          lastName = "Doe",
          phoneNumber = "1234567890",
          accountStatus = "active",
          eventsAttendeeList = mutableListOf("1", "2"),
          eventsHostList = mutableListOf("3"),
          friendsList = mutableListOf(),
          profilePicUrl = "http://example.com/pic.jpg",
          qrCodeUrl = "http://example.com/qr.jpg",
          username = "john_doe")

  @Before
  fun setUp() {
    messageRepository = MockMessageRepository()
    userRepository = MockUserRepository()
    viewModel = ChatViewModel(messageRepository, userRepository, opponentId)
  }

  @Test
  fun `init and getMessages successful`() = runTest {
    // Set up user repo

    (userRepository as MockUserRepository).updateCurrentUserId("user1")
    userRepository.addUser(opponent)

    val msg1 = mockMessage.copy(sender = "user1", id = "msg1")
    val msg2 = mockMessage.copy(sender = "user2", id = "msg2")
    val messageHistory = messageRepository.createNewMessageHistory("user1", "user2")

    messageRepository.addMessage(msg1, (messageHistory as Resource.Success).data)
    messageRepository.addMessage(msg2, messageHistory.data)

    viewModel =
        ChatViewModel(messageRepository, userRepository, opponentId) // Initialize view model
    viewModel.getMessages()

    val expectedMessages = mutableListOf(msg1, msg2)
    val uiState = viewModel.uiState.value
    assert(uiState.userId == "user1")
    assert(uiState.messagesLoadedFirstTime)
    assert(expectedMessages == uiState.messageHistory.messages)
    assert(uiState.opponentProfile == opponent)
  }

  @Test
  fun `init getCurrentUserId failure and getMessages with null UserId`() = runTest {
    mockkStatic(Log::class)
    every { Log.d(any(), any()) } returns 0
    (userRepository as MockUserRepository).updateCurrentUserId(null)

    viewModel =
        ChatViewModel(messageRepository, userRepository, opponentId) // Initialize view model

    val uiState = viewModel.uiState.value
    val userId = userRepository.getCurrentUserId()
    assert(uiState.userId == null)
    verify {
      Log.d(
          "ChatViewModel",
          "Error getting user ID: ${(userId as Resource.Failure).throwable.message}")
    }

    viewModel.getMessages()

    verify { Log.d("ChatViewModel", "Invalid state: User ID is null.") }
    unmockkAll()
  }

  @Test
  fun `initOpponent failure`() = runTest {
    mockkStatic(Log::class)
    every { Log.d(any(), any()) } returns 0
    (userRepository as MockUserRepository).updateCurrentUserId("user1")

    viewModel =
        ChatViewModel(messageRepository, userRepository, opponentId) // Initialize view model

    val uiState = viewModel.uiState.value
    val opponentResource = userRepository.getUser(opponentId)
    assert(uiState.opponentProfile == nullUser)
    verify {
      Log.d(
          "ChatViewModel",
          "Error getting opponent details: ${(opponentResource as Resource.Failure).throwable.message}")
    }
    unmockkAll()
  }

  @Test
  fun `onMessageBarInputChange test`() = runTest {
    (userRepository as MockUserRepository).updateCurrentUserId("user1")
    userRepository.addUser(opponent)

    viewModel =
        ChatViewModel(messageRepository, userRepository, opponentId) // Initialize view model

    viewModel.onMessageBarInputChange("Hey")

    val uiState = viewModel.uiState.value

    assert(uiState.messageBarInput == "Hey")
    assert(!uiState.messageInserted)
  }

  @Test
  fun `onMessageSend success test`() = runTest {
    mockkStatic(LocalDateTime::class)
    val fixedDateTime = LocalDateTime.of(2024, 5, 16, 12, 0) // A fixed point in time
    every { LocalDateTime.now() } returns fixedDateTime
    (userRepository as MockUserRepository).updateCurrentUserId("user1")
    userRepository.addUser(opponent)

    val msg1 = mockMessage.copy(sender = "user1", id = "msg1")
    val msg2 = mockMessage.copy(sender = "user2", id = "msg2")
    val messageHistory = messageRepository.createNewMessageHistory("user1", "user2")

    messageRepository.addMessage(msg1, (messageHistory as Resource.Success).data)
    messageRepository.addMessage(msg2, messageHistory.data)

    viewModel =
        ChatViewModel(messageRepository, userRepository, opponentId) // Initialize view model
    viewModel.getMessages()
    viewModel.onMessageBarInputChange("Hey")

    viewModel.onMessageSend()

    val newMsg =
        mockMessage.copy(
            sender = "user1", content = "Hey", dateTimeSent = LocalDateTime.now(), id = "")
    val expectedMessages = mutableListOf(msg1, msg2, newMsg)
    val uiState = viewModel.uiState.value

    assert(uiState.messageBarInput == "")
    assert(uiState.messageInserted)
    assert(uiState.messagesLoadedFirstTime)
    assert(expectedMessages.size == uiState.messageHistory.messages.size)
    assert(expectedMessages == uiState.messageHistory.messages)
    unmockkAll()
  }
}