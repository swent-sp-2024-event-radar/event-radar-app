package com.github.se.eventradar

import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.message.Message
import com.github.se.eventradar.model.message.MessageHistory
import com.github.se.eventradar.model.repository.message.IMessageRepository
import com.github.se.eventradar.model.repository.message.MockMessageRepository
import java.time.LocalDateTime
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class MockMessageRepositoryUnitTest {
  private lateinit var messageRepository: IMessageRepository

  private val mockMessage =
      Message(
          sender = "1",
          content = "Hello",
          dateTimeSent = LocalDateTime.parse("2021-01-01T00:00:00"),
          id = "1")

  @Before
  fun setUp() {
    messageRepository = MockMessageRepository()
  }

  @Test
  fun testGetMessagesWhenMessageHistoryDoesNotExist() = runTest {
    // Test getMessages() method
    var messageHistory = messageRepository.getMessages("1", "2")

    assert(messageHistory is Resource.Failure)

    messageHistory = messageHistory as Resource.Failure
    assert(messageHistory.throwable.message == "No message history found between users")
  }

  @Test
  fun testGetMessagesWhenAlreadyCreatedMessageHistory() = runTest {
    // Test getMessages() method
    val expected = messageRepository.createNewMessageHistory("1", "2")

    assert(expected is Resource.Success)
    val messageHistory = messageRepository.getMessages("1", "2")

    assert(messageHistory is Resource.Success)

    assert((messageHistory as Resource.Success).data == (expected as Resource.Success).data)
  }

  @Test
  fun testAddMessage() = runTest {
    val messageHistory =
        MessageHistory(
            user1 = "1",
            user2 = "2",
            user1ReadMostRecentMessage = false,
            user2ReadMostRecentMessage = false,
            latestMessageId = "",
            messages = mutableListOf(),
            id = "")

    val addMessage = messageRepository.addMessage(mockMessage, messageHistory)

    assert(addMessage is Resource.Success)

    val updatedMessageHistory = messageRepository.getMessages("1", "2")

    assert((updatedMessageHistory as Resource.Success).data.messages[0] == mockMessage)
    assert(updatedMessageHistory.data.latestMessageId == "1")
  }

  @Test
  fun testUpdateMessage() = runTest {
    val messageHistory =
        MessageHistory(
            user1 = "1",
            user2 = "2",
            user1ReadMostRecentMessage = false,
            user2ReadMostRecentMessage = false,
            latestMessageId = "",
            messages = mutableListOf(),
            id = "")

    val addMessage = messageRepository.addMessage(mockMessage, messageHistory)

    val updatedMessageHistory = messageRepository.getMessages("1", "2")

    assert(addMessage is Resource.Success)

    val updateMessage =
        messageRepository.updateReadStateForUser(
            "1", (updatedMessageHistory as Resource.Success).data)

    assert(updateMessage is Resource.Success)

    assert(updatedMessageHistory.data.user1ReadMostRecentMessage)
    assert(!updatedMessageHistory.data.user2ReadMostRecentMessage)
  }

  @Test
  fun testCreateNewMessageHistory() = runTest {
    // Test createNewMessageHistory() method
    var messageHistory = messageRepository.createNewMessageHistory("1", "3")

    assert(messageHistory is Resource.Success)

    messageHistory = messageHistory as Resource.Success

    assert(messageHistory.data.user1 == "1")
    assert(messageHistory.data.user2 == "3")
  }

  @Test
  fun addMessageToNonExistentMessageHistory() = runTest {
    // Test addMessage() method
    val id = "50"
    val addMessage =
        messageRepository.addMessage(
            mockMessage,
            MessageHistory(
                "1",
                "2",
                "1",
                user1ReadMostRecentMessage = true,
                user2ReadMostRecentMessage = false,
                messages = mutableListOf(mockMessage),
                id = id))

    assert(addMessage is Resource.Failure)
    assert(
        (addMessage as Resource.Failure).throwable.message ==
            "MessageHistory with id $id not found")
  }

  @Test
  fun updateMessageInNonExistentMessageHistory() = runTest {
    // Test updateMessage() method
    val id = "50"
    val updateMessage =
        messageRepository.updateReadStateForUser(
            id,
            MessageHistory(
                "1",
                "2",
                "1",
                user1ReadMostRecentMessage = true,
                user2ReadMostRecentMessage = true,
                messages = mutableListOf(),
                id = id))

    assert(updateMessage is Resource.Failure)
    assert(
        (updateMessage as Resource.Failure).throwable.message ==
            "Message history with id $id not found or user with id $id not found")
  }

  @Test
  fun addMessageUpdatesMessageHistoryWhenNewMessageIsReceived() = runTest {
    val messageHistory =
        MessageHistory(
            user1 = "1",
            user2 = "2",
            user1ReadMostRecentMessage = false,
            user2ReadMostRecentMessage = false,
            latestMessageId = "",
            messages = mutableListOf(),
            id = "")

    val addMessage = messageRepository.addMessage(mockMessage, messageHistory)

    assert(addMessage is Resource.Success)

    var updatedMessageHistory = messageRepository.getMessages("1", "2")
    updatedMessageHistory = updatedMessageHistory as Resource.Success
    assert(updatedMessageHistory.data.latestMessageId == "1")

    val addSecondMessage =
        messageRepository.addMessage(mockMessage.copy(id = "2"), updatedMessageHistory.data)

    assert(addSecondMessage is Resource.Success)

    updatedMessageHistory = messageRepository.getMessages("1", "2")

    assert(updatedMessageHistory is Resource.Success)

    assert((updatedMessageHistory as Resource.Success).data.latestMessageId == "2")
  }
}
