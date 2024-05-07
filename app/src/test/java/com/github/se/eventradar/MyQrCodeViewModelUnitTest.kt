package com.github.se.eventradar

import android.net.Uri
import android.util.Log
import com.github.se.eventradar.model.User
import com.github.se.eventradar.model.repository.user.IUserRepository
import com.github.se.eventradar.model.repository.user.MockUserRepository
import com.github.se.eventradar.viewmodel.MyQrCodeViewModel
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import junit.framework.TestCase.assertEquals
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
import org.mockito.Mockito

@ExperimentalCoroutinesApi
class MyQrCodeViewModelUnitTest {

  private lateinit var userRepository: IUserRepository
  private lateinit var viewModel: MyQrCodeViewModel

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

  private val mockUser =
      User(
          userId = "1",
          birthDate = "01/01/2000",
          email = "test@example.com",
          firstName = "John",
          lastName = "Doe",
          phoneNumber = "1234567890",
          accountStatus = "active",
          eventsAttendeeList = mutableListOf("event1", "event2"),
          eventsHostList = mutableListOf("event3"),
          friendsList = mutableListOf(),
          profilePicUrl = "",
          qrCodeUrl = "",
          username = "johndoe")

  @Before
  fun setup() {
    userRepository = MockUserRepository()
    viewModel = MyQrCodeViewModel(userRepository)
  }

  @Test
  fun testGetUserNameFailNoUserId() = runTest {
    mockkStatic(Log::class)
    every { Log.d(any(), any()) } returns 0
    // Given
    // initialize user with no mock
    userRepository.addUser(mockUser) // user in database, but no currentUserId
    viewModel.getUsername()
    verify { Log.d("MyQrCodeViewModel", "Error fetching user ID: No user currently signed in") }
    unmockkAll()
  }

  @Test
  fun testGetUserNameNoUserFail() = runTest {
    mockkStatic(Log::class)
    every { Log.d(any(), any()) } returns 0
    // Given
    // initialize user with no mock
    val userId = mockUser.userId
    (userRepository as MockUserRepository).updateCurrentUserId(userId)
    viewModel.getUsername() // "User with id $uid not found
    verify {
      Log.d("MyQrCodeViewModel", "Error fetching username: User with id ${userId} not found")
    }
    unmockkAll()
  }

  @Test
  fun testGetUserNameSuccess() = runTest {
    // Given
    val userId = mockUser.userId
    val username = mockUser.username
    // initialize user with no mock
    userRepository.addUser(mockUser)
    (userRepository as MockUserRepository).updateCurrentUserId(userId)

    viewModel.getUsername()
    assertEquals(username, viewModel.uiState.value.username)
  }

  @Test
  fun testGetUserQrCodeSuccess() = runTest {
    // Given
    val userId = mockUser.userId
    val folderName = "QR_Codes"
    val expectedQrCodeLink = "http://example.com/QR_Codes/pic.jpg"
    // initialize user with no mock
    userRepository.addUser(mockUser)
    (userRepository as MockUserRepository).updateCurrentUserId(userId)
    userRepository.uploadImage(Mockito.mock(Uri::class.java), userId, folderName)
    viewModel.getQRCodeLink()
    assertEquals(expectedQrCodeLink, viewModel.uiState.value.qrCodeLink)
  }

  @Test
  fun testGetQrCodeNoUserIdFail() = runTest {
    mockkStatic(Log::class)
    every { Log.d(any(), any()) } returns 0
    userRepository.addUser(mockUser)
    viewModel.getQRCodeLink()
    verify { Log.d("MyQrCodeViewModel", "Error fetching user ID: No user currently signed in") }
    unmockkAll()
  }

  @Test
  fun testGetQrCodeNoImageFail() = runTest {
    mockkStatic(Log::class)
    every { Log.d(any(), any()) } returns 0
    // Given
    val userId = mockUser.userId
    val folderName = "QR_Codes"
    // initialize user with no mock

    userRepository.addUser(mockUser)
    (userRepository as MockUserRepository).updateCurrentUserId(userId)
    viewModel.getQRCodeLink()
    verify {
      Log.d(
          "MyQrCodeViewModel",
          "Error fetching image: Image from folder $folderName not found for user $userId")
    }
    unmockkAll()
  }
}
