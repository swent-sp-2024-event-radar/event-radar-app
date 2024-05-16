package com.github.se.eventradar

import android.util.Log
import com.github.se.eventradar.model.User
import com.github.se.eventradar.model.repository.user.IUserRepository
import com.github.se.eventradar.model.repository.user.MockUserRepository
import com.github.se.eventradar.viewmodel.ViewFriendsProfileViewModel
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class ViewFriendsProfileViewModelUnitTest {
  private lateinit var userRepository: IUserRepository
  private lateinit var viewModel: ViewFriendsProfileViewModel

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
          friendsList = mutableListOf("2"),
          profilePicUrl = "http://example.com/Profile_Pictures/1",
          qrCodeUrl = "http://example.com/QR_Codes/1",
          bio = "",
          username = "johndoe")
  private val mockFriend =
      User(
          userId = "2",
          birthDate = "02/02/2002",
          email = "friend@example.com",
          firstName = "Jim",
          lastName = "Smith",
          phoneNumber = "1234567890",
          accountStatus = "active",
          eventsAttendeeList = mutableListOf("event1", "event2"),
          eventsHostList = mutableListOf("event3"),
          friendsList = mutableListOf("1"),
          profilePicUrl = "http://example.com/Profile_Pictures/2",
          qrCodeUrl = "http://example.com/QR_Codes/2",
          bio = "",
          username = "jimsmith")

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

  @get:Rule val mainDispatcherRule = ViewFriendsProfileViewModelUnitTest.MainDispatcherRule()

  private val factory =
      object : ViewFriendsProfileViewModel.Factory {
        override fun create(friendUserId: String): ViewFriendsProfileViewModel {
          return ViewFriendsProfileViewModel(userRepository, friendUserId)
        }
      }

  @Before
  fun setUp() {
    userRepository = MockUserRepository()
    runBlocking { userRepository.updateUser(mockUser) }
    runBlocking { userRepository.addUser(mockUser) }
    viewModel = factory.create(friendUserId = mockFriend.userId)
  }

  @Test
  fun viewFriendsProfileDetailsSuccessful() {
    runBlocking { userRepository.addUser(mockFriend) }
    viewModel.getFriendProfileDetails()
    assert(viewModel.friendUserId == mockFriend.userId)
    assert(viewModel.uiState.value.friendProfilePicLink == mockFriend.profilePicUrl)
    assert(viewModel.uiState.value.bio == mockFriend.bio)
    assert(viewModel.uiState.value.friendUserName == mockFriend.username)
    assert(viewModel.uiState.value.friendFirstName == mockFriend.firstName)
    assert(viewModel.uiState.value.friendLastName == mockFriend.lastName)
  }

  @Test
  fun viewFriendsProfileDetailsFailureFriendIdDoesNotExist() {
    mockkStatic(Log::class)
    every { Log.d(any(), any()) } returns 0
    // does not add mockFriend as a user, so user does not exist
    viewModel.getFriendProfileDetails()
    verify {
      Log.d(
          "ViewFriendsProfileViewModel",
          "Error getting friend's user details for friendUserId ${mockFriend.userId}")
    }
    unmockkAll()
  }
}
