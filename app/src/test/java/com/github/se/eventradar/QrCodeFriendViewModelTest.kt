package com.github.se.eventradar

import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.User
import com.github.se.eventradar.model.repository.user.IUserRepository
import com.github.se.eventradar.model.repository.user.MockUserRepository
import com.github.se.eventradar.viewmodel.qrCode.QrCodeAnalyser
import com.github.se.eventradar.viewmodel.qrCode.QrCodeFriendViewModel
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
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
class QrCodeFriendViewModelTest {

  private lateinit var viewModel: QrCodeFriendViewModel
  private lateinit var userRepository: IUserRepository
  private lateinit var qrCodeAnalyser: QrCodeAnalyser

  private val myUID = "user1"

  private val mockUser1 =
      User(
          userId = "user1",
          birthDate = "01/01/2000",
          email = "test@example.com",
          firstName = "John",
          lastName = "Doe",
          phoneNumber = "1234567890",
          accountStatus = "active",
          eventsAttendeeSet = mutableSetOf("1", "2"),
          eventsHostSet = mutableSetOf("3"),
          friendsSet = mutableSetOf(),
          profilePicUrl = "http://example.com/pic.jpg",
          qrCodeUrl = "http://example.com/qr.jpg",
          username = "john_doe")

  private val mockUser2 =
      User(
          userId = "user2",
          birthDate = "01/01/2002",
          email = "test@example2.com",
          firstName = "John2",
          lastName = "Doe2",
          phoneNumber = "12345678902",
          accountStatus = "active2",
          eventsAttendeeSet = mutableSetOf("12", "22"),
          eventsHostSet = mutableSetOf("32"),
          friendsSet = mutableSetOf(),
          profilePicUrl = "http://example.com/pic.jpg2",
          qrCodeUrl = "http://example.com/qr.jpg2",
          username = "john_doe2")

  private val mockUser1AF =
      User(
          userId = "user1",
          birthDate = "01/01/2000",
          email = "test@example.com",
          firstName = "John",
          lastName = "Doe",
          phoneNumber = "1234567890",
          accountStatus = "active",
          eventsAttendeeSet = mutableSetOf("1", "2"),
          eventsHostSet = mutableSetOf("3"),
          friendsSet = mutableSetOf("user2"),
          profilePicUrl = "http://example.com/pic.jpg",
          qrCodeUrl = "http://example.com/qr.jpg",
          username = "john_doe")

  private val mockUser2AF =
      User(
          userId = "user2",
          birthDate = "01/01/2002",
          email = "test@example2.com",
          firstName = "John2",
          lastName = "Doe2",
          phoneNumber = "12345678902",
          accountStatus = "active2",
          eventsAttendeeSet = mutableSetOf("12", "22"),
          eventsHostSet = mutableSetOf("32"),
          friendsSet = mutableSetOf("user1"),
          profilePicUrl = "http://example.com/pic.jpg2",
          qrCodeUrl = "http://example.com/qr.jpg2",
          username = "john_doe2")

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
    (userRepository as MockUserRepository).updateCurrentUserId(myUID)
    qrCodeAnalyser = mockk<QrCodeAnalyser>(relaxed = true)
    viewModel = QrCodeFriendViewModel(userRepository, qrCodeAnalyser)
  }

  @Test
  fun testDecodingSuccess() = runTest {
    val testDecodedString = "user2"
    qrCodeAnalyser.onDecoded?.invoke(testDecodedString)
    assertEquals(testDecodedString, viewModel.decodedResult.value)
  }

  @Test
  fun testUpdateFriendListCalled() = runTest {
    qrCodeAnalyser.onDecoded?.invoke("user2")
    verify { viewModel.updateFriendList("user2") }
  }

  @Test
  fun testDecodingFailure() = runTest {
    qrCodeAnalyser.onDecoded?.invoke(null)
    assertEquals("Failed to decode QR Code", viewModel.decodedResult.value)
    assertEquals(
        viewModel.action.take(2).toList(),
        listOf(QrCodeFriendViewModel.Action.None, QrCodeFriendViewModel.Action.AnalyserError))
  }
  // todo should i be testing thta it is reset to none ? isnt this Ui logic?
  @Test
  fun testInvokedAndFriendListUpdated() = runTest {
    userRepository.addUser(mockUser1)
    userRepository.addUser(mockUser2)
    qrCodeAnalyser.onDecoded?.invoke("user2")
    when (val user1 = userRepository.getUser("user1")) {
      is Resource.Success -> {
        assertEquals(user1.data!!.friendsSet, mutableSetOf("user2"))
      }
      else -> {
        assert(false)
        println("User 1 not found or could not be fetched")
      }
    }
    when (val user2 = userRepository.getUser("user2")) {
      is Resource.Success -> {
        assertEquals(user2.data!!.friendsSet, mutableSetOf("user1"))
      }
      else -> {
        assert(false)
        println("User 2 not found or could not be fetched")
      }
    }
    delay(3000L)
    assertEquals(
        viewModel.action.take(2).toList(),
        listOf(
            QrCodeFriendViewModel.Action.None, QrCodeFriendViewModel.Action.NavigateToNextScreen))
  }

  @Test
  fun testInvokedWhenAlreadyFriends() = runTest {
    userRepository.addUser(mockUser1AF)
    userRepository.addUser(mockUser2AF)
    qrCodeAnalyser.onDecoded?.invoke("user2")
    when (val user1 = userRepository.getUser("user1")) {
      is Resource.Success -> {
        assertEquals(user1.data!!.friendsSet, mutableSetOf("user2"))
      }
      else -> {
        assert(false)
        println("User 1 not found or could not be fetched")
      }
    }
    when (val user2 = userRepository.getUser("user2")) {
      is Resource.Success -> {
        assertEquals(user2.data!!.friendsSet, mutableSetOf("user1"))
      }
      else -> {
        assert(false)
        println("User 2 not found or could not be fetched")
      }
    }
    delay(3000L)
    assertEquals(
        viewModel.action.take(2).toList(),
        listOf(
            QrCodeFriendViewModel.Action.None, QrCodeFriendViewModel.Action.NavigateToNextScreen))
  }

  // this case should provoke a timeout hence why i delayed the test before
  // checking the navigation event
  @Test
  fun testInvokedWithFakeUID() = runTest {
    userRepository.addUser(mockUser1)
    userRepository.addUser(mockUser2)
    qrCodeAnalyser.onDecoded?.invoke("user3")
    delay(15000L)
    assertEquals(
        viewModel.action.take(2).toList(),
        listOf(QrCodeFriendViewModel.Action.None, QrCodeFriendViewModel.Action.FirebaseFetchError))
  }

  @Test
  fun changeTabTest() = runTest {
    val expectedTabState = QrCodeFriendViewModel.TAB.ScanQR
    viewModel.changeTabState(expectedTabState)
    val actualTabState = viewModel.tabState.value
    assertEquals(expectedTabState, actualTabState)
  }
  // TODO stimulate event where update does not work on first few tries but eventually works
  // TODO stimulate event where update does not work and timeout occurs
}
