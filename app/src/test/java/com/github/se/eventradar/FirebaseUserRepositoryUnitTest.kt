package com.github.se.eventradar

import android.net.Uri
import android.util.Log
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.User
import com.github.se.eventradar.model.repository.user.FirebaseUserRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.unmockkStatic
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FirebaseUserRepositoryUnitTest {

  @RelaxedMockK lateinit var userRef: CollectionReference
  @RelaxedMockK lateinit var mockDocumentSnapshot: DocumentSnapshot
  @RelaxedMockK lateinit var mockDocumentSnapshotPrivate: DocumentSnapshot
  @RelaxedMockK lateinit var mockQuerySnapshot: QuerySnapshot
  @RelaxedMockK lateinit var mockDb: FirebaseFirestore

  private lateinit var firebaseUserRepository: FirebaseUserRepository

  private val uid = "1"
  private val expectedUser =
      User(
          userId = uid,
          birthDate = "",
          email = "",
          firstName = "",
          lastName = "",
          phoneNumber = "",
          accountStatus = "active",
          eventsAttendeeSet = mutableListOf(),
          eventsHostSet = mutableListOf(),
          friendsSet = mutableListOf(),
          profilePicUrl = "",
          qrCodeUrl = "",
          username = "",
      )

  @Before
  fun setUp() {
    userRef = mockk()
    mockDocumentSnapshot = mockk()
    mockDocumentSnapshotPrivate = mockk()
    mockQuerySnapshot = mockk()

    mockDb = mockk()

    every { mockDb.collection("users") } returns userRef

    firebaseUserRepository = FirebaseUserRepository(db = mockDb)
  }

  @After
  fun tearDown() {
    unmockkAll()
  }

  @Test
  fun `test getUsers()`() = runTest {
    every { userRef.get() } returns mockTask(mockQuerySnapshot)
    every { mockQuerySnapshot.documents } returns listOf(mockDocumentSnapshot)
    every { mockDocumentSnapshot.id } returns uid
    every { mockDocumentSnapshot.data } returns
        mapOf(
            "accountStatus" to "active",
            "eventsAttendeeList" to emptyList<String>(),
            "eventsHostList" to emptyList<String>(),
            "profilePicUrl" to "",
            "qrCodeUrl" to "",
            "username" to "",
        )
    every { userRef.document(any()).collection("private").document("private").get() } returns
        mockTask(mockDocumentSnapshotPrivate)
    every { mockDocumentSnapshotPrivate["birthDate"] } returns ""
    every { mockDocumentSnapshotPrivate["email"] } returns ""
    every { mockDocumentSnapshotPrivate["firstName"] } returns ""
    every { mockDocumentSnapshotPrivate["lastName"] } returns ""
    every { mockDocumentSnapshotPrivate["phoneNumber"] } returns ""

    val result = firebaseUserRepository.getUsers()

    assert(result is Resource.Success)
    val returnedUser = (result as Resource.Success).data

    assert(returnedUser.size == 1)
    assert(returnedUser[0] == expectedUser)
  }

  @Test
  fun `test getUsers() empty case`() = runTest {
    every { userRef.get() } returns mockTask(mockQuerySnapshot)
    every { mockQuerySnapshot.documents } returns emptyList()

    val result = firebaseUserRepository.getUsers()

    assert(result is Resource.Success)
    val returnedUser = (result as Resource.Success).data

    assert(returnedUser.isEmpty())
  }

  @Test
  fun `test getUsers() exception`() = runTest {
    val message = "Exception"
    every { userRef.get() } returns mockTask(mockQuerySnapshot, Exception(message))

    val result = firebaseUserRepository.getUsers()

    assert(result is Resource.Failure)
    assert((result as Resource.Failure).throwable.message == message)
  }

  @Test
  fun `test getUser()`() = runTest {
    every { userRef.document(uid).get() } returns mockTask(mockDocumentSnapshot)
    every { mockDocumentSnapshot.data } returns
        mapOf(
            "accountStatus" to "active",
            "eventsAttendeeList" to mutableListOf<String>(),
            "eventsHostList" to mutableListOf<String>(),
            "friendsList" to mutableListOf<String>(),
            "profilePicUrl" to "",
            "qrCodeUrl" to "",
            "username" to "",
        )
    every { mockDocumentSnapshot.id } returns uid
    every { userRef.document(uid).collection("private").document("private").get() } returns
        mockTask(mockDocumentSnapshotPrivate)
    every { mockDocumentSnapshotPrivate["birthDate"] } returns ""
    every { mockDocumentSnapshotPrivate["email"] } returns ""
    every { mockDocumentSnapshotPrivate["firstName"] } returns ""
    every { mockDocumentSnapshotPrivate["lastName"] } returns ""
    every { mockDocumentSnapshotPrivate["phoneNumber"] } returns ""

    val result = firebaseUserRepository.getUser(uid)

    assert(result is Resource.Success)
    val returnedUser = (result as Resource.Success).data

    assert(returnedUser == expectedUser)
  }

  @Test
  fun `test getUser() exception`() = runTest {
    val message = "Exception"
    every { userRef.document(uid).get() } returns mockTask(mockDocumentSnapshot, Exception(message))

    val result = firebaseUserRepository.getUser(uid)

    assert(result is Resource.Failure)
    assert((result as Resource.Failure).throwable.message == message)
  }

  @Test
  fun `test addUser(User)`() = runTest {
    mockkStatic(Log::class)
    every { Log.d(any(), any()) } returns 0

    val user = expectedUser

    every { userRef.document().id } returns uid

    val capturePublic = slot<Map<String, Any>>()
    val capturePrivate = slot<Map<String, Any>>()

    every { userRef.document(uid).set(capture(capturePublic)) } returns mockTask(null)
    every {
      userRef.document(uid).collection("private").document("private").set(capture(capturePrivate))
    } returns mockTask(null)

    val result = firebaseUserRepository.addUser(user)

    assert(result is Resource.Success)
    assert(
        capturePublic.captured ==
            mapOf(
                "accountStatus" to "active",
                "eventsAttendeeList" to mutableListOf<String>(),
                "eventsHostList" to mutableListOf<String>(),
                "friendsList" to mutableListOf<String>(),
                "profilePicUrl" to "",
                "qrCodeUrl" to "",
                "username" to "",
            ))
    assert(
        capturePrivate.captured ==
            mapOf(
                "birthDate" to "",
                "email" to "",
                "firstName" to "",
                "lastName" to "",
                "phoneNumber" to "",
            ))

    unmockkStatic(Log::class)
  }

  @Test
  fun `test addUser(User) exception`() = runTest {
    val user = expectedUser

    every { userRef.document().id } returns uid

    val message = "Exception"
    every { userRef.document(uid).set(any()) } returns mockTask(null, Exception(message))

    val result = firebaseUserRepository.addUser(user)

    assert(result is Resource.Failure)
    assert((result as Resource.Failure).throwable.message == message)
  }

  @Test
  fun `test addUser(Map, String)`() = runTest {
    mockkStatic(Log::class)
    every { Log.d(any(), any()) } returns 0

    val map =
        mapOf(
            "accountStatus" to "active",
            "eventsAttendeeList" to mutableListOf<String>(),
            "eventsHostList" to mutableListOf<String>(),
            "friendsList" to mutableListOf<String>(),
            "profilePicUrl" to "",
            "qrCodeUrl" to "",
            "username" to "",
            "private/birthDate" to "",
            "private/email" to "",
            "private/firstName" to "",
            "private/lastName" to "",
            "private/phoneNumber" to "",
        )

    val capturePublic = slot<Map<String, Any>>()
    val capturePrivate = slot<Map<String, Any>>()

    every { userRef.document(uid).set(capture(capturePublic)) } returns mockTask(null)
    every {
      userRef.document(uid).collection("private").document("private").set(capture(capturePrivate))
    } returns mockTask(null)

    val result = firebaseUserRepository.addUser(map, uid)

    assert(result is Resource.Success)
    assert(
        capturePublic.captured ==
            mapOf(
                "accountStatus" to "active",
                "eventsAttendeeList" to mutableListOf<String>(),
                "eventsHostList" to mutableListOf<String>(),
                "friendsList" to mutableListOf<String>(),
                "profilePicUrl" to "",
                "qrCodeUrl" to "",
                "username" to "",
            ))
    assert(
        capturePrivate.captured ==
            mapOf(
                "birthDate" to "",
                "email" to "",
                "firstName" to "",
                "lastName" to "",
                "phoneNumber" to "",
            ))

    unmockkStatic(Log::class)
  }

  @Test
  fun `test addUser(Map, String) exception`() = runTest {
    val map =
        mapOf(
            "accountStatus" to "active",
            "eventsAttendeeList" to mutableListOf<String>(),
            "eventsHostList" to mutableListOf<String>(),
            "friendsList" to mutableListOf<String>(),
            "profilePicUrl" to "",
            "qrCodeUrl" to "",
            "username" to "",
            "private/birthDate" to "",
            "private/email" to "",
            "private/firstName" to "",
            "private/lastName" to "",
            "private/phoneNumber" to "",
        )

    val message = "Exception"
    every { userRef.document(uid).set(any()) } returns mockTask(null, Exception(message))

    val result = firebaseUserRepository.addUser(map, uid)

    assert(result is Resource.Failure)
    assert((result as Resource.Failure).throwable.message == message)
  }

  @Test
  fun `test updateUser(User)`() = runTest {
    mockkStatic(Log::class)
    every { Log.d(any(), any()) } returns 0

    val user = expectedUser

    val capturePublic = slot<Map<String, Any>>()
    val capturePrivate = slot<Map<String, Any>>()

    every { userRef.document(uid).update(capture(capturePublic)) } returns mockTask(null)
    every {
      userRef
          .document(uid)
          .collection("private")
          .document("private")
          .update(capture(capturePrivate))
    } returns mockTask(null)

    val result = firebaseUserRepository.updateUser(user)

    assert(result is Resource.Success)
    assert(
        capturePublic.captured ==
            mapOf(
                "accountStatus" to "active",
                "eventsAttendeeList" to mutableListOf<String>(),
                "eventsHostList" to mutableListOf<String>(),
                "friendsList" to mutableListOf<String>(),
                "profilePicUrl" to "",
                "qrCodeUrl" to "",
                "username" to "",
            ))
    assert(
        capturePrivate.captured ==
            mapOf(
                "birthDate" to "",
                "email" to "",
                "firstName" to "",
                "lastName" to "",
                "phoneNumber" to "",
            ))

    unmockkStatic(Log::class)
  }

  @Test
  fun `test updateUser(User) exception`() = runTest {
    val user = expectedUser

    val message = "Exception"
    every { userRef.document(uid).update(any()) } returns mockTask(null, Exception(message))

    val result = firebaseUserRepository.updateUser(user)

    assert(result is Resource.Failure)
    assert((result as Resource.Failure).throwable.message == message)
  }

  @Test
  fun `test deleteUser(User)`() = runTest {
    val user = expectedUser

    every { userRef.document(uid).delete() } returns mockTask(null)

    val result = firebaseUserRepository.deleteUser(user)

    assert(result is Resource.Success)
  }

  @Test
  fun `test deleteUser(User) exception`() = runTest {
    val user = expectedUser

    val message = "Exception"
    every { userRef.document(uid).delete() } returns mockTask(null, Exception(message))

    val result = firebaseUserRepository.deleteUser(user)

    assert(result is Resource.Failure)
    assert((result as Resource.Failure).throwable.message == message)
  }

  @Test
  fun `test isUserLoggedIn(String)`() = runTest {
    every { userRef.document(uid).get() } returns mockTask(mockDocumentSnapshot)
    every { mockDocumentSnapshot.exists() } returns true

    val result = firebaseUserRepository.doesUserExist(uid)

    assert(result is Resource.Success)
  }

  @Test
  fun `test isUserLoggedIn(String) not found`() = runTest {
    every { userRef.document(uid).get() } returns mockTask(mockDocumentSnapshot)
    every { mockDocumentSnapshot.exists() } returns false

    val result = firebaseUserRepository.doesUserExist(uid)

    assert(result is Resource.Failure)
    assert((result as Resource.Failure).throwable.message == "User not found")
  }

  @Test
  fun `test isUserLoggedIn(String) exception`() = runTest {
    val message = "Exception"
    every { userRef.document(uid).get() } returns mockTask(mockDocumentSnapshot, Exception(message))

    val result = firebaseUserRepository.doesUserExist(uid)

    assert(result is Resource.Failure)
    assert((result as Resource.Failure).throwable.message == message)
  }

}

/**
 * Mocks the simplest behaviour of a task so .await() can return task or throw exception See more on
 * [await] and inside of that on awaitImpl
 */
inline fun <reified T> mockTask(result: T?, exception: Exception? = null): Task<T> {
  val task: Task<T> = mockk(relaxed = true)
  every { task.isComplete } returns true
  every { task.exception } returns exception
  every { task.isCanceled } returns false
  val relaxedT: T = mockk(relaxed = true)
  every { task.result } returns result
  return task
}
