package com.github.se.eventradar

import android.util.Log
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.User
import com.github.se.eventradar.model.message.Message
import com.github.se.eventradar.model.repository.message.IMessageRepository
import com.github.se.eventradar.model.repository.message.MockMessageRepository
import com.github.se.eventradar.model.repository.user.MockUserRepository
import com.github.se.eventradar.viewmodel.MessagesUiState
import com.github.se.eventradar.viewmodel.MessagesViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import java.time.LocalDateTime
import junit.framework.TestCase.fail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MessagesViewModelTest {

  @RelaxedMockK lateinit var viewModel: MessagesViewModel

  private lateinit var userRepository: MockUserRepository
  private lateinit var messagesRepository: IMessageRepository
  private lateinit var mockUiState: MutableStateFlow<MessagesUiState>

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

  @Before
  fun setUp() {
    userRepository = MockUserRepository()
    messagesRepository = MockMessageRepository()
    mockUiState = MutableStateFlow(MessagesUiState())

    userRepository.updateCurrentUserId("1")

    viewModel = MessagesViewModel(messagesRepository, userRepository)
  }

  @After
  fun tearDown() {
    unmockkAll()
  }

  @Test
  fun testGetMessages() = runTest {
    val mh = messagesRepository.createNewMessageHistory("1", "2")

    assert(mh is Resource.Success)

    val expectedMessage = Message("1", "Hello", LocalDateTime.now(), "1")

    val m = messagesRepository.addMessage(expectedMessage, (mh as Resource.Success).data)

    assert(m is Resource.Success)

    viewModel = MessagesViewModel(messagesRepository, userRepository)

    assert(viewModel.uiState.value.messageList.size == 1)
    assert(viewModel.uiState.value.messageList[0].messages[0] == expectedMessage)
  }

  @Test
  fun testOnSearchQueryChange() = runTest {
    viewModel.onSearchQueryChange("Hello", mockUiState)

    assert(mockUiState.value.searchQuery == "Hello")
  }

  @Test
  fun testOnSelectedTabIndexChange() = runTest {
    viewModel.onSelectedTabIndexChange(1, mockUiState)

    assert(mockUiState.value.selectedTabIndex == 1)
  }

  @Test
  fun testGetUser() = runTest {
    val addUser =
        userRepository.addUser(
            User(
                userId = "1",
                birthDate = "01/01/2000",
                email = "",
                firstName = "",
                lastName = "",
                phoneNumber = "",
                accountStatus = "",
                eventsAttendeeList = mutableListOf(),
                eventsHostList = mutableListOf(),
                friendsList = mutableListOf(),
                profilePicUrl = "",
                qrCodeUrl = "",
                username = ""))

    assert(addUser is Resource.Success)

    val user = viewModel.getUser("1")

    assert(user.userId == "1")
  }

  @Test
  fun testGetUserWithValueNotInDatabase() = runTest {
    // this will never be the case, but it is a good test to have
    mockkStatic(Log::class)
    every { Log.d(any(), any()) } returns 0
    try {
      viewModel.getUser("1")
    } catch (e: Exception) {
      verify { Log.d("MessagesViewModel", "Error getting user: User with id 1 not found") }
      assert(e is NullPointerException)
      return@runTest
    }
    fail("Expected exception to be thrown")
  }

  @Test
  fun testGetMessagesWithEmptyMessages() = runTest {
    viewModel.getMessages()

    assert(viewModel.uiState.value.messageList.isEmpty())
  }

  @Test
  fun testGetMessagesFailure() = runTest {
    mockkStatic(Log::class)
    messagesRepository = mockk()

    val exception = "Test exception"

    coEvery { messagesRepository.getMessages(any()) } returns Resource.Failure(Exception(exception))
    every { Log.d(any(), any()) } returns 0

    viewModel = MessagesViewModel(messagesRepository, userRepository)

    viewModel.getMessages()

    verify { Log.d("MessagesViewModel", "Error getting messages: $exception") }
  }

  @Test
  fun testGetFriends() = runTest {
    val user =
        User(
            userId = "1",
            birthDate = "01/01/2000",
            email = "",
            firstName = "",
            lastName = "",
            phoneNumber = "",
            accountStatus = "",
            eventsAttendeeList = mutableListOf(),
            eventsHostList = mutableListOf(),
            friendsList = mutableListOf(),
            profilePicUrl = "",
            qrCodeUrl = "",
            username = "")

    val addUser = userRepository.addUser(user)

    assert(addUser is Resource.Success)

    val addUser2 = userRepository.addUser(user.copy(userId = "2"))

    assert(addUser2 is Resource.Success)

    val addUser3 = userRepository.addUser(user.copy(userId = "3"))

    assert(addUser3 is Resource.Success)

    val addUser4 = userRepository.addUser(user.copy(userId = "4"))

    assert(addUser4 is Resource.Success)

    val addUser5 = userRepository.addUser(user.copy(userId = "5"))

    assert(addUser5 is Resource.Success)

    userRepository.updateUser(user.copy(friendsList = mutableListOf("2", "3", "4", "5")))

    viewModel.getFriends()

    assert(viewModel.uiState.value.friendsList.size == 4)
  }

  @Test
  fun testGetFriendsWithNoFriends() = runTest {
    val user =
        User(
            userId = "1",
            birthDate = "01/01/2000",
            email = "",
            firstName = "",
            lastName = "",
            phoneNumber = "",
            accountStatus = "",
            eventsAttendeeList = mutableListOf(),
            eventsHostList = mutableListOf(),
            friendsList = mutableListOf(),
            profilePicUrl = "",
            qrCodeUrl = "",
            username = "")

    val addUser = userRepository.addUser(user)

    assert(addUser is Resource.Success)

    viewModel.getFriends()

    assert(viewModel.uiState.value.friendsList.isEmpty())
  }

  @Test
  fun testGetFriendsWithUserNotInDatabase() = runTest {
    // this will never be the case, but it is a good test to have
    mockkStatic(Log::class)
    every { Log.d(any(), any()) } returns 0

    userRepository.updateCurrentUserId("2")

    viewModel.getFriends()

    verify { Log.d("MessagesViewModel", "Error getting friends: User with id 1 not found") }
  }

  @Test
  fun testNotAllFriendsInDatabase() = runTest {
    mockkStatic(Log::class)
    every { Log.d(any(), any()) } returns 0

    val user =
        User(
            userId = "1",
            birthDate = "01/01/2000",
            email = "",
            firstName = "",
            lastName = "",
            phoneNumber = "",
            accountStatus = "",
            eventsAttendeeList = mutableListOf(),
            eventsHostList = mutableListOf(),
            friendsList = mutableListOf(),
            profilePicUrl = "",
            qrCodeUrl = "",
            username = "")

    val addUser = userRepository.addUser(user)

    assert(addUser is Resource.Success)

    val addUser2 = userRepository.addUser(user.copy(userId = "2"))

    assert(addUser2 is Resource.Success)

    val addUser3 = userRepository.addUser(user.copy(userId = "3"))

    assert(addUser3 is Resource.Success)

    val addUser4 = userRepository.addUser(user.copy(userId = "4"))

    assert(addUser4 is Resource.Success)

    val addUser5 = userRepository.addUser(user.copy(userId = "5"))

    assert(addUser5 is Resource.Success)

    userRepository.updateUser(user.copy(friendsList = mutableListOf("2", "3", "4", "5")))

    userRepository.deleteUser(user.copy(userId = "4"))

    viewModel.getFriends()

    verify { Log.d("MessagesViewModel", "Error getting friend: User with id 4 not found") }
    assert(viewModel.uiState.value.friendsList.size == 3)
  }

  @Test
  fun testGetMessagesWithNoUserId() = runTest {
    mockkStatic(Log::class)
    every { Log.d(any(), any()) } returns 0

    userRepository.updateCurrentUserId(null)

    viewModel = MessagesViewModel(messagesRepository, userRepository)

    verify { Log.d("MessagesViewModel", "Error getting user ID: No user currently signed in") }
    assert(viewModel.uiState.value.userId == null)
  }

  @Test
  fun testOnSearchQueryChanged() = runTest {
    viewModel.onSearchQueryChange("Hello", mockUiState)

    assert(mockUiState.value.searchQuery == "Hello")
  }

  @Test
  fun testOnSelectedTabIndexChanged() = runTest {
    viewModel.onSelectedTabIndexChange(1, mockUiState)

    assert(mockUiState.value.selectedTabIndex == 1)
  }
}
