package com.github.se.eventradar

import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.message.Message
import com.github.se.eventradar.model.message.MessageHistory
import com.github.se.eventradar.model.repository.message.FirebaseMessageRepository
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.unmockkAll
import java.time.LocalDateTime
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class FirebaseMessageRepositoryUnitTest {

  @RelaxedMockK lateinit var messagesRef: CollectionReference
  @RelaxedMockK lateinit var mockDocumentSnapshot: DocumentSnapshot
  @RelaxedMockK lateinit var mockDocumentSnapshotMessages: DocumentSnapshot
  @RelaxedMockK lateinit var mockQuerySnapshot: QuerySnapshot
  @RelaxedMockK lateinit var mockQuerySnapshotMessages: QuerySnapshot
  @RelaxedMockK lateinit var mockDocumentReference: DocumentReference
  @RelaxedMockK lateinit var mockDb: FirebaseFirestore

  private lateinit var firebaseMessageRepository: FirebaseMessageRepository

  private val uid = "1"
  private val expectedMessageHistory =
      MessageHistory(
          user1 = "1",
          user2 = "2",
          latestMessageId = "1",
          user1ReadMostRecentMessage = false,
          user2ReadMostRecentMessage = false,
          messages =
              mutableListOf(
                  Message(
                      sender = "1",
                      content = "",
                      dateTimeSent = LocalDateTime.parse("2021-01-01T00:00:00"),
                      id = "1",
                  )),
          id = "1")

  @Before
  fun setUp() {
    messagesRef = mockk()
    mockDocumentSnapshot = mockk()
    mockDocumentSnapshotMessages = mockk()
    mockQuerySnapshot = mockk()
    mockQuerySnapshotMessages = mockk()
    mockDocumentReference = mockk()

    mockDb = mockk()

    every { mockDb.collection("messages") } returns messagesRef

    firebaseMessageRepository = FirebaseMessageRepository(db = mockDb)
  }

  @After
  fun tearDown() {
    unmockkAll()
  }

  @Test
  fun `test getMessages()`() = runTest {
    every { messagesRef.where(any()).get() } returns mockTask(mockQuerySnapshot)
    every { mockQuerySnapshot.isEmpty } returns false
    every { mockQuerySnapshot.documents } returns listOf(mockDocumentSnapshot)
    every { mockDocumentSnapshot.id } returns uid
    every { mockDocumentSnapshot.data } returns
        mapOf(
            "from_user" to "1",
            "to_user" to "2",
            "latest_message_id" to "1",
            "from_user_read" to false,
            "to_user_read" to false,
        )
    every { messagesRef.document(any()).collection("messages_list").get() } returns
        mockTask(mockQuerySnapshotMessages)
    every { mockQuerySnapshotMessages.documents } returns listOf(mockDocumentSnapshotMessages)
    every { mockDocumentSnapshotMessages.id } returns uid
    every { mockDocumentSnapshotMessages["content"] } returns ""
    every { mockDocumentSnapshotMessages["sender"] } returns "1"
    every { mockDocumentSnapshotMessages["date_time_sent"] } returns "2021-01-01T00:00:00"

    val result = firebaseMessageRepository.getMessages(uid)

    assert(result is Resource.Success)
    val returnedMessageHistory = (result as Resource.Success).data

    assert(returnedMessageHistory.size == 1)
    assert(returnedMessageHistory[0] == expectedMessageHistory)
  }

  @Test
  fun `test getMessages for specific users`() = runTest {
    val user1 = "1"
    val user2 = "2"

    every { messagesRef.where(any()).limit(1).get() } returns mockTask(mockQuerySnapshot)
    every { mockQuerySnapshot.isEmpty } returns false
    every { mockQuerySnapshot.documents } returns listOf(mockDocumentSnapshot)
    every { mockDocumentSnapshot.id } returns uid
    every { mockDocumentSnapshot.data } returns
        mapOf(
            "from_user" to user1,
            "to_user" to user2,
            "latest_message_id" to "1",
            "from_user_read" to false,
            "to_user_read" to false)

    every { messagesRef.document(uid).collection("messages_list").get() } returns
        mockTask(mockQuerySnapshotMessages)
    every { mockQuerySnapshotMessages.documents } returns listOf(mockDocumentSnapshotMessages)
    every { mockDocumentSnapshotMessages.id } returns uid
    every { mockDocumentSnapshotMessages["content"] } returns ""
    every { mockDocumentSnapshotMessages["sender"] } returns user1
    every { mockDocumentSnapshotMessages["date_time_sent"] } returns "2021-01-01T00:00:00"

    val result = firebaseMessageRepository.getMessages(user1, user2)

    assert(result is Resource.Success)
    val messageHistory = (result as Resource.Success).data

    assert(messageHistory.id == uid)
    assert(messageHistory.messages.size == 1)
    val message = messageHistory.messages.first()
    assert(message.sender == user1)
    assert(message.content == "")
    assert(message.dateTimeSent == LocalDateTime.parse("2021-01-01T00:00:00"))
    assert(message.id == uid)
    assert(messageHistory == expectedMessageHistory)
  }

  @Test
  fun `test getMessages() with empty result`() = runTest {
    every { messagesRef.where(any()).get() } returns mockTask(mockQuerySnapshot)
    every { mockQuerySnapshot.isEmpty } returns true

    val result = firebaseMessageRepository.getMessages(uid)

    assert(result is Resource.Success)
    val returnedMessageHistory = (result as Resource.Success).data

    assert(returnedMessageHistory.isEmpty())
  }

  @Test
  fun `test getMessages() with empty messages`() = runTest {
    every { messagesRef.where(any()).get() } returns mockTask(mockQuerySnapshot)
    every { mockQuerySnapshot.isEmpty } returns false
    every { mockQuerySnapshot.documents } returns listOf(mockDocumentSnapshot)
    every { mockDocumentSnapshot.id } returns uid
    every { mockDocumentSnapshot.data } returns
        mapOf(
            "from_user" to "1",
            "to_user" to "2",
            "latest_message_id" to "1",
            "from_user_read" to false,
            "to_user_read" to false,
        )
    every { messagesRef.document(any()).collection("messages_list").get() } returns
        mockTask(mockQuerySnapshotMessages)
    every { mockQuerySnapshotMessages.documents } returns emptyList()

    val result = firebaseMessageRepository.getMessages(uid)

    assert(result is Resource.Success)
    val returnedMessageHistory = (result as Resource.Success).data

    assert(returnedMessageHistory.size == 1)
    assert(returnedMessageHistory[0].messages.isEmpty())
  }

  @Test
  fun `test getMessages() with exception`() = runTest {
    val exceptionMessage = "Exception"
    every { messagesRef.where(any()).get() } throws Exception(exceptionMessage)

    val result = firebaseMessageRepository.getMessages(uid)

    assert(result is Resource.Failure)
    assert((result as Resource.Failure).throwable.message == exceptionMessage)
  }

  @Test
  fun `test addMessage()`() = runTest {
    val message =
        Message(
            sender = "1",
            content = "",
            dateTimeSent = LocalDateTime.parse("2021-01-01T00:00:00"),
            id = "1",
        )
    val messageHistory = expectedMessageHistory

    every { messagesRef.document(any()).collection("messages_list").add(any()) } returns
        mockTask(mockDocumentReference)
    every { mockDocumentReference.id } returns "1"

    val captureUpdate = slot<Map<String, Any?>>()

    val expectedUpdateValues =
        mapOf(
            "latest_message_id" to "1",
            "from_user_read" to true,
            "to_user_read" to false,
        )

    every { messagesRef.document(any()).update(capture(captureUpdate)) } returns mockTask(null)

    val result = firebaseMessageRepository.addMessage(message, messageHistory)

    assert(result is Resource.Success)
    assert(captureUpdate.captured == expectedUpdateValues)
  }

  @Test
  fun `test addMessage() with exception`() = runTest {
    val message =
        Message(
            sender = "1",
            content = "",
            dateTimeSent = LocalDateTime.parse("2021-01-01T00:00:00"),
            id = "1",
        )
    val messageHistory = expectedMessageHistory

    every { messagesRef.document(any()).collection("messages_list").add(any()) } returns
        mockTask(mockDocumentReference)
    every { mockDocumentReference.id } returns "1"

    val exceptionMessage = "Exception"
    every { messagesRef.document(any()).update(any()) } throws Exception(exceptionMessage)

    val result = firebaseMessageRepository.addMessage(message, messageHistory)

    assert(result is Resource.Failure)
    assert((result as Resource.Failure).throwable.message == exceptionMessage)
  }

  @Test
  fun `test updateReadStateForUser() when from user updates`() = runTest {
    val messageHistory = expectedMessageHistory
    val user = "1"

    val captureUpdate = slot<Map<String, Any?>>()
    val expectedUpdateValues = mapOf("from_user_read" to true)

    every { messagesRef.document(any()).update(capture(captureUpdate)) } returns mockTask(null)

    val result = firebaseMessageRepository.updateReadStateForUser(user, messageHistory)

    assert(result is Resource.Success)
    assert(captureUpdate.captured == expectedUpdateValues)
  }

  @Test
  fun `test updateReadStateForUser() when to user updates`() = runTest {
    val messageHistory = expectedMessageHistory
    val user = "2"

    val captureUpdate = slot<Map<String, Any?>>()
    val expectedUpdateValues = mapOf("to_user_read" to true)

    every { messagesRef.document(any()).update(capture(captureUpdate)) } returns mockTask(null)

    val result = firebaseMessageRepository.updateReadStateForUser(user, messageHistory)

    assert(result is Resource.Success)
    assert(captureUpdate.captured == expectedUpdateValues)
  }

  @Test
  fun `test updateReadStateForUser() with exception`() = runTest {
    val messageHistory = expectedMessageHistory
    val user = "1"

    val exceptionMessage = "Exception"
    every { messagesRef.document(any()).update(any()) } throws Exception(exceptionMessage)

    val result = firebaseMessageRepository.updateReadStateForUser(user, messageHistory)

    assert(result is Resource.Failure)
    assert((result as Resource.Failure).throwable.message == exceptionMessage)
  }

  @Test
  fun `test createNewMessageHistory()`() = runTest {
    val user1 = "1"
    val user2 = "2"

    val captureAdd = slot<Map<String, Any?>>()
    val expectedAddValues =
        mapOf(
            "from_user" to user1,
            "to_user" to user2,
            "latest_message_id" to "",
            "from_user_read" to false,
            "to_user_read" to false,
        )

    every { messagesRef.add(capture(captureAdd)) } returns mockTask(mockDocumentReference)
    every { mockDocumentReference.id } returns "1"

    val result = firebaseMessageRepository.createNewMessageHistory(user1, user2)

    assert(result is Resource.Success)
    assert(captureAdd.captured == expectedAddValues)
  }

  @Test
  fun `test createNewMessageHistory() with exception`() = runTest {
    val user1 = "1"
    val user2 = "2"

    val exceptionMessage = "Exception"
    every { messagesRef.add(any()) } throws Exception(exceptionMessage)

    val result = firebaseMessageRepository.createNewMessageHistory(user1, user2)

    assert(result is Resource.Failure)
    assert((result as Resource.Failure).throwable.message == exceptionMessage)
  }
}
