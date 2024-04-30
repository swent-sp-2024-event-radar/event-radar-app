package com.github.se.eventradar

import android.util.Log
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.User
import com.github.se.eventradar.model.message.Message
import com.github.se.eventradar.model.repository.message.IMessageRepository
import com.github.se.eventradar.model.repository.message.MockMessageRepository
import com.github.se.eventradar.model.repository.user.IUserRepository
import com.github.se.eventradar.model.repository.user.MockUserRepository
import com.github.se.eventradar.ui.messages.MessagesUiState
import com.github.se.eventradar.ui.messages.MessagesViewModel
import com.google.firebase.auth.FirebaseAuth
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
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

  private lateinit var userRepository: IUserRepository
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
    mockUiState =
        MutableStateFlow(
            MessagesUiState(
                userId = "1",
            ))

    mockkStatic(FirebaseAuth::class)
    every { FirebaseAuth.getInstance().currentUser!!.uid } returns "1"

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
                eventsAttendeeSet = mutableSetOf(),
                eventsHostSet = mutableSetOf(),
                friendsSet = mutableSetOf(),
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
}
