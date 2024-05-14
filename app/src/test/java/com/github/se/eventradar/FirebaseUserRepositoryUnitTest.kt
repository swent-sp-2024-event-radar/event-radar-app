package com.github.se.eventradar

import android.content.Context
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.User
import com.github.se.eventradar.model.repository.user.FirebaseUserRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.storage
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.unmockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

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
          eventsAttendeeList = mutableListOf(),
          eventsHostList = mutableListOf(),
          friendsList = mutableListOf(),
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

    val mockFirebaseApp = mockk<FirebaseApp>()
    val mockFirebaseAuth = mockk<FirebaseAuth>()
    val mockFirebaseStorage = mockk<FirebaseStorage>()

    mockkStatic(FirebaseApp::class)
    mockkStatic(FirebaseAuth::class)
    mockkStatic(FirebaseStorage::class)

    every { FirebaseApp.getInstance() } returns mockFirebaseApp
    every { FirebaseAuth.getInstance() } returns mockFirebaseAuth
    every { FirebaseStorage.getInstance() } returns mockFirebaseStorage

    // Mock Context
    val mockContext = mockk<Context>()

    // Mock static methods
    mockkStatic(FirebaseApp::class)
    mockkStatic(ApplicationProvider::class)

    // Define behavior
    every { FirebaseApp.initializeApp(any()) } returns mockFirebaseApp
    every { ApplicationProvider.getApplicationContext<Context>() } returns mockContext
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

  @Test
  fun `test GetCurrentUserId Success`() = runTest {
    mockkStatic("com.google.firebase.Firebase")
    mockkStatic("com.google.firebase.auth.FirebaseAuth")

    val mockFirebaseAuth = mockk<FirebaseAuth>()
    val mockFirebaseUser = mockk<FirebaseUser>()

    every { Firebase.auth } returns mockFirebaseAuth
    every { mockFirebaseAuth.currentUser } returns mockFirebaseUser
    every { mockFirebaseUser.uid } returns "1"

    val result = firebaseUserRepository.getCurrentUserId()
    assert(result is Resource.Success)
    assert((result as Resource.Success).data == "1")

    unmockkStatic("com.google.firebase.Firebase")
    unmockkStatic("com.google.firebase.auth.FirebaseAuth")
  }

  @Test
  fun `test GetCurrentUserId Failure`() = runTest {
    mockkStatic("com.google.firebase.Firebase")
    mockkStatic("com.google.firebase.auth.FirebaseAuth")

    val mockFirebaseAuth = mockk<FirebaseAuth>()

    every { Firebase.auth } returns mockFirebaseAuth
    every { mockFirebaseAuth.currentUser } returns null

    val result = firebaseUserRepository.getCurrentUserId()
    assert(result is Resource.Failure)
    val failureResult = result as Resource.Failure
    assert(failureResult.throwable is Exception)
    assert(failureResult.throwable.message == "No user currently signed in")

    unmockkStatic("com.google.firebase.Firebase")
    unmockkStatic("com.google.firebase.auth.FirebaseAuth")
  }

  @Test
  fun `test uploadImage Success`() = runTest {
    val selectedImageUri = mockk<Uri>()
    every { selectedImageUri.path } returns "/path/to/image"
    val uid = "1"
    val folderName = "folderName"
    val storageRef = mockk<StorageReference>()
    every { Firebase.storage.reference } returns storageRef
    every { storageRef.child("$folderName/$uid") } returns storageRef
    every { storageRef.putFile(selectedImageUri) } returns
        mockUploadTask(selectedImageUri, null, isSuccessful = true)
    val result = firebaseUserRepository.uploadImage(selectedImageUri, uid, folderName)
    assert(result is Resource.Success)
  }

  @Test
  fun `test uploadImage Generic Error Failure`() = runTest {
    val selectedImageUri = mockk<Uri>()
    every { selectedImageUri.path } returns "/path/to/image"
    val uid = "1"
    val folderName = "folderName"
    val storageRef = mockk<StorageReference>()
    val genericException = Exception("Generic Error") // get coverage?
    every { Firebase.storage.reference } returns storageRef
    every { storageRef.child("$folderName/$uid") } returns storageRef
    every { storageRef.putFile(selectedImageUri) } returns
        mockUploadTask(null, genericException, isSuccessful = false)
    val result = firebaseUserRepository.uploadImage(selectedImageUri, uid, folderName)
    assert(result is Resource.Failure)
    assert(
        (result as Resource.Failure).throwable.message ==
            "Error during upload image: ${genericException.message}")
  }

  @Test
  fun `test uploadImage Failure From Task`() = runTest {
    val selectedImageUri = mockk<Uri>()
    every { selectedImageUri.path } returns "/path/to/image"
    val uid = "1"
    val folderName = "folderName"
    val storageRef = mockk<StorageReference>()
    val exception = Exception("Upload failed without a specific error")
    every { Firebase.storage.reference } returns storageRef
    every { storageRef.child("$folderName/$uid") } returns storageRef
    every { storageRef.putFile(selectedImageUri) } returns
        mockUploadTask(selectedImageUri, null, isSuccessful = false)
    val result = firebaseUserRepository.uploadImage(selectedImageUri, uid, folderName)
    assert(result is Resource.Failure)
    assert((result as Resource.Failure).throwable.message == exception.message)
  }

  @Test
  fun `test getImage Success`() = runTest {
    val uid = "1"
    val folderName = "folderName"
    val selectedImageUri = mockk<Uri>()
    val expectedUrl = "http://example.com/image.png"
    every { selectedImageUri.toString() } returns expectedUrl
    val storageRef = mockk<StorageReference>()
    every { Firebase.storage.reference.child("$folderName/$uid") } returns storageRef
    every { storageRef.downloadUrl } returns mockDownloadUrlTask(selectedImageUri)

    val result = firebaseUserRepository.getImage(uid, folderName)

    assert(result is Resource.Success)
    assert((result as Resource.Success).data == expectedUrl)
  }

  @Test
  fun `test getImage Generic Failure`() = runTest {
    val uid = "1"
    val folderName = "folderName"
    val genericException = Exception("Generic Error")
    val storageRef = mockk<StorageReference>()

    every { Firebase.storage.reference.child("$folderName/$uid") } returns storageRef
    every { storageRef.downloadUrl } throws genericException

    val result = firebaseUserRepository.getImage(uid, folderName)

    assert(result is Resource.Failure)
    assert(
        (result as Resource.Failure).throwable.message ==
            "Error while getting image: ${genericException.message}")
  }

  @Test
  fun `test uploadQRCode Success`() = runTest {
    val uid = "1"
    val folderName = "QR_Codes"
    val storageRef = mockk<StorageReference>()
    val mockData = ByteArray(1)
    every { FirebaseStorage.getInstance().reference } returns storageRef
    every { storageRef.child("$folderName/$uid") } returns storageRef
    every { storageRef.putBytes(any()) } returns mockUploadTaskQrCode(isSuccessful = true)

    val result = firebaseUserRepository.uploadQRCode(mockData, uid)
    assert(result is Resource.Success)
  }

  @Test
  fun `test uploadQRCode Failure From Task`() = runTest {
    val uid = "1"
    val folderName = "QR_Codes"
    val storageRef = mockk<StorageReference>()
    val mockData = ByteArray(1)
    val exception = Exception("Upload QR Code failed without a specific error")
    every { FirebaseStorage.getInstance().reference } returns storageRef
    every { storageRef.child("$folderName/$uid") } returns storageRef
    every { storageRef.putBytes(any()) } returns mockUploadTaskQrCode(null, isSuccessful = false)

    val result = firebaseUserRepository.uploadQRCode(mockData, uid)
    assert(result is Resource.Failure)
    assert((result as Resource.Failure).throwable.message == exception.message)
  }

  @Test
  fun `test uploadQRCode Generic Error Failure`() = runTest {
    val uid = "1"
    val folderName = "QR_Codes"
    val genericException = Exception("Generic Error")
    val storageRef = mockk<StorageReference>()
    val mockData = ByteArray(1)
    every { FirebaseStorage.getInstance().reference } returns storageRef
    every { storageRef.child("$folderName/$uid") } returns storageRef
    every { storageRef.putBytes(any()) } returns
        mockUploadTaskQrCode(exception = genericException, isSuccessful = true)

    val result = firebaseUserRepository.uploadQRCode(mockData, uid)
    assert(result is Resource.Failure)
    assert(
        (result as Resource.Failure).throwable.message ==
            "Error during QR code upload: ${genericException.message}")
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

fun mockUploadTask(result: Uri?, exception: Exception? = null, isSuccessful: Boolean): UploadTask {
  val task: UploadTask = mockk(relaxed = true)
  val snapshot: UploadTask.TaskSnapshot = mockk(relaxed = true)
  every { snapshot.task } returns task
  every { snapshot.metadata } returns mockk { every { path } returns result?.path.toString() }
  every { task.isComplete } returns true
  every { task.exception } returns exception
  every { task.isCanceled } returns false
  every { task.isSuccessful } returns isSuccessful
  every { task.result } returns snapshot
  return task
}

fun mockUploadTaskQrCode(exception: Exception? = null, isSuccessful: Boolean): UploadTask {
  val task: UploadTask = mockk(relaxed = true)
  val snapshot: UploadTask.TaskSnapshot = mockk(relaxed = true)
  every { snapshot.task } returns task
  // every { snapshot.metadata } returns mockk { every { path } returns result?.path.toString() }
  every { task.isComplete } returns true
  every { task.exception } returns exception
  every { task.isCanceled } returns false
  every { task.isSuccessful } returns isSuccessful
  every { task.result } returns snapshot
  return task
}

fun mockDownloadUrlTask(result: Uri?, exception: Exception? = null): Task<Uri> {

  val task: Task<Uri> = mockk(relaxed = true)
  every { task.isComplete } returns true
  every { task.isCanceled } returns false
  every { task.isSuccessful } returns (exception == null)
  every { task.exception } returns exception
  every { task.result } returns result
  return task
}
