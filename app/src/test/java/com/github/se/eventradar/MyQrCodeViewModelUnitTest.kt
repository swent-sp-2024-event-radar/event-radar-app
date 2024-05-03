package com.github.se.eventradar

import com.github.se.eventradar.model.User
import com.github.se.eventradar.model.repository.user.IUserRepository
import com.github.se.eventradar.model.repository.user.MockUserRepository
import com.github.se.eventradar.viewmodel.MyQrCodeViewModel
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
          eventsAttendeeSet = mutableSetOf("event1", "event2"),
          eventsHostSet = mutableSetOf("event3"),
          friendsSet = mutableSetOf(),
          profilePicUrl = "http://example.com/Profile_Pictures/pic.jpg",
          qrCodeUrl = "http://example.com/QR_Codes/qr.jpg",
          username = "johndoe")

  @Before
  fun setup() {
    userRepository = MockUserRepository()
    viewModel = MyQrCodeViewModel(userRepository)
  }

  @Test
  fun testGetUserNameSuccess() = runTest {
    // Given
    val userId = mockUser.userId
    val username = mockUser.username
    // initialize user with no mock
    userRepository.addUser(mockUser)
    viewModel.getUsername(userId)
    assertEquals(username, viewModel.uiState.value.username)
  }

  @Test
  fun testGetUserQrCodeSuccess() = runTest {
    // Given
    val userId = mockUser.userId
    val expectedQrCodeLink = "http://example.com/QR_Codes/qr.jpg"
    // initialize user with no mock
    userRepository.addUser(mockUser)
    viewModel.getQRCodeLink(userId)
    assertEquals(expectedQrCodeLink, viewModel.uiState.value.qrCodeLink)
  }
}
