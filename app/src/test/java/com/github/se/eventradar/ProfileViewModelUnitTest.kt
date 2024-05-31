package com.github.se.eventradar

import android.util.Log
import com.github.se.eventradar.model.User
import com.github.se.eventradar.model.repository.user.IUserRepository
import com.github.se.eventradar.model.repository.user.MockUserRepository
import com.github.se.eventradar.viewmodel.ProfileViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelUnitTest {

  private lateinit var viewModelFriend: ProfileViewModel
  private lateinit var viewModelUser: ProfileViewModel
  private var userRepository = mockk<IUserRepository>()

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
          birthDate = "01.01.2000",
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
          birthDate = "02.02.2002",
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

  private val factory =
      object : ProfileViewModel.Factory {
        override fun create(userId: String?): ProfileViewModel {
          return ProfileViewModel(userRepository, userId)
        }
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setUp() {
    userRepository = MockUserRepository()
    runBlocking { userRepository.updateUser(mockUser) }
    runBlocking { userRepository.addUser(mockUser) }
    viewModelFriend = factory.create(userId = mockFriend.userId)
    viewModelUser = factory.create(null)
  }

  @Test
  fun viewFriendsProfileDetailsSuccessful() {
    runBlocking { userRepository.addUser(mockFriend) }
    viewModelFriend.getProfileDetails()
    assert(viewModelFriend.userId == mockFriend.userId)
    assert(viewModelFriend.uiState.value.profilePicUrl == mockFriend.profilePicUrl)
    assert(viewModelFriend.uiState.value.bio == mockFriend.bio)
    assert(viewModelFriend.uiState.value.username == mockFriend.username)
    assert(viewModelFriend.uiState.value.firstName == mockFriend.firstName)
    assert(viewModelFriend.uiState.value.lastName == mockFriend.lastName)
  }

  @Test
  fun viewFriendsProfileDetailsFailure() {
    mockkStatic(Log::class)
    every { Log.d(any(), any()) } returns 0
    viewModelFriend.getProfileDetails()
    verify { Log.d("ProfileViewModel", "Error getting user details for user ${mockFriend.userId}") }
    unmockkAll()
  }
}