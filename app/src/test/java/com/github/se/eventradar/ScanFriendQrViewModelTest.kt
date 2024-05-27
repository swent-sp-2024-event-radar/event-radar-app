package com.github.se.eventradar

import android.util.Log
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.User
import com.github.se.eventradar.model.repository.user.IUserRepository
import com.github.se.eventradar.model.repository.user.MockUserRepository
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.viewmodel.qrCode.QrCodeAnalyser
import com.github.se.eventradar.viewmodel.qrCode.ScanFriendQrViewModel
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
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
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

// TODO stimulate event where update does not work on first few tries but eventually works
@ExperimentalCoroutinesApi
class ScanFriendQrViewModelTest {

  private lateinit var viewModel: ScanFriendQrViewModel
  private lateinit var userRepository: IUserRepository
  private lateinit var qrCodeAnalyser: QrCodeAnalyser

  private val myUID = "user1"

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
          profilePicUrl = "http://example.com/Profile_Pics/1",
          qrCodeUrl = "http://example.com/QR_Codes/1",
          bio = "",
          username = "johndoe")

  private val mockUser1 =
      User(
          userId = "user1",
          birthDate = "01/01/2000",
          email = "test@example.com",
          firstName = "John",
          lastName = "Doe",
          phoneNumber = "1234567890",
          accountStatus = "active",
          eventsAttendeeList = mutableListOf("1", "2"),
          eventsHostList = mutableListOf("3"),
          friendsList = mutableListOf(),
          profilePicUrl = "http://example.com/Profile_Pics/user1",
          qrCodeUrl = "http://example.com/QR_Codes/user1",
          bio = "",
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
          eventsAttendeeList = mutableListOf("12", "22"),
          eventsHostList = mutableListOf("32"),
          friendsList = mutableListOf(),
          profilePicUrl = "http://example.com/Profile_Pics/user2",
          qrCodeUrl = "http://example.com/QR_Codes/user2",
          bio = "",
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
          eventsAttendeeList = mutableListOf("1", "2"),
          eventsHostList = mutableListOf("3"),
          friendsList = mutableListOf("user2"),
          profilePicUrl = "http://example.com/Profile_Pics/user1",
          qrCodeUrl = "http://example.com/QR_Codes/user1",
          bio = "",
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
          eventsAttendeeList = mutableListOf("12", "22"),
          eventsHostList = mutableListOf("32"),
          friendsList = mutableListOf("user1"),
          profilePicUrl = "http://example.com/Profile_Pics/user2",
          qrCodeUrl = "http://example.com/QR_Codes/user2",
          bio = "",
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

  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  @get:Rule val mainDispatcherRule = MainDispatcherRule()

  @Before
  fun setUp() {
    MockKAnnotations.init(this)
    every { mockNavActions.navigateTo(any()) } just Runs
    userRepository = MockUserRepository()
    (userRepository as MockUserRepository).updateCurrentUserId(myUID)
    qrCodeAnalyser = QrCodeAnalyser()
    viewModel = ScanFriendQrViewModel(userRepository, qrCodeAnalyser, mockNavActions)
    //      waitForLoadingToComplete()
  }

  //    private fun waitForLoadingToComplete() {
  //        while (viewModel.uiState.value.isLoading) {
  //            // Small sleep to avoid busy waiting
  //            Thread.sleep(50)
  //        }
  //    }

  //    @Test
  //    fun switchesScreenWhenNavigatedToNextScreen() = run {
  //            viewModel.changeAction(ScanFriendQrViewModel.Action.NavigateToNextScreen)
  //            verify { mockNavActions.navigateTo(any()) }
  //        }

  @Test
  fun testDecodingSuccess() = runTest {
    userRepository.addUser(mockUser1)
    userRepository.addUser(mockUser2)
    val testDecodedString = "user2"
    qrCodeAnalyser.onDecoded?.invoke(testDecodedString)
    assertEquals(testDecodedString, viewModel.uiState.value.decodedResult)
  }

  @Test
  fun testDecodingFailure() = runTest {
    userRepository.addUser(mockUser1)
    userRepository.addUser(mockUser2)
    qrCodeAnalyser.onDecoded?.invoke(null)
    assertEquals(null, viewModel.uiState.value.decodedResult)
    assertEquals(ScanFriendQrViewModel.Action.AnalyserError, viewModel.uiState.value.action)
  }
  // todo should i be testing thta it is reset to none ? isnt this Ui logic?
  @Test
  fun testInvokedAndFriendListUpdated() = runTest {
    userRepository.addUser(mockUser1)
    userRepository.addUser(mockUser2)
    qrCodeAnalyser.onDecoded?.invoke("user2")
    when (val user1 = userRepository.getUser("user1")) {
      is Resource.Success -> {
        assertEquals(mutableListOf("user2"), user1.data!!.friendsList)
      }
      else -> {
        assert(false)
        println("User 1 not found or could not be fetched")
      }
    }
    when (val user2 = userRepository.getUser("user2")) {
      is Resource.Success -> {
        assertEquals(mutableListOf("user1"), user2.data!!.friendsList)
      }
      else -> {
        assert(false)
        println("User 2 not found or could not be fetched")
      }
    }
    assertEquals(ScanFriendQrViewModel.Action.None, viewModel.uiState.value.action)
  }

  @Test
  fun testInvokedWhenAlreadyFriends() = runTest {
    userRepository.addUser(mockUser1AF)
    userRepository.addUser(mockUser2AF)
    qrCodeAnalyser.onDecoded?.invoke("user2")
    when (val user1 = userRepository.getUser("user1")) {
      is Resource.Success -> {
        assertEquals(mutableListOf("user2"), user1.data!!.friendsList)
      }
      else -> {
        assert(false)
        println("User 1 not found or could not be fetched")
      }
    }
    when (val user2 = userRepository.getUser("user2")) {
      is Resource.Success -> {
        assertEquals(mutableListOf("user1"), user2.data!!.friendsList)
      }
      else -> {
        assert(false)
        println("User 2 not found or could not be fetched")
      }
    }
    assertEquals(ScanFriendQrViewModel.Action.None, viewModel.uiState.value.action)
  }

  // This is testing if the getUserDetails function (to display information in MyQRCode Tab) returns
  // an error when we are trying to get a username when no user is logged in (no userid)
  @Test
  fun testGetUserNameFailNoUserId() = runTest {
    mockkStatic(Log::class)
    every { Log.d(any(), any()) } returns 0
    // initialize user with no mock
    (userRepository as MockUserRepository).updateCurrentUserId(null)
    userRepository.addUser(mockUser) // user in database, but no currentUserId
    viewModel.getUserDetails()
    verify { Log.d("ScanFriendQrViewModel", "Error fetching user ID: No user currently signed in") }
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
    viewModel.getUserDetails() // "User with id $uid not found
    verify {
      Log.d(
          "ScanFriendQrViewModel", "Error fetching user details: User with id ${userId} not found")
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

    viewModel.getUserDetails()
    assertEquals(username, viewModel.uiState.value.username)
  }

  @Test
  fun testGetUserQrCodeSuccess() = runTest {
    // Given
    val userId = mockUser.userId
    val expectedQrCodeLink = "http://example.com/QR_Codes/$userId"
    // initialize user with no mock
    (userRepository as MockUserRepository).updateCurrentUserId(userId)
    // Mocking what happens when you add a user
    userRepository.addUser(mockUser)
    val result = userRepository.getImage(userId, "QR_Codes")
    Assert.assertTrue(result is Resource.Success)
    Assert.assertEquals(expectedQrCodeLink, (result as Resource.Success).data)

    // add use
    viewModel.getUserDetails()
    assertEquals(expectedQrCodeLink, viewModel.uiState.value.qrCodeLink)
  }

  @Test
  fun testGetQrCodeNoUserIdFail() = runTest {
    mockkStatic(Log::class)
    every { Log.d(any(), any()) } returns 0
    userRepository.addUser(mockUser)
    (userRepository as MockUserRepository).updateCurrentUserId(null)
    viewModel.getUserDetails()
    verify { Log.d("ScanFriendQrViewModel", "Error fetching user ID: No user currently signed in") }
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
    val newUser = mockUser.copy(qrCodeUrl = "")
    (userRepository as MockUserRepository).updateCurrentUserId(userId)
    userRepository.addUser(newUser)
    viewModel.getUserDetails()
    assertEquals(viewModel.uiState.value.qrCodeLink, "")
    val result = userRepository.getImage(userId, "QR_Codes")
    Assert.assertTrue(result is Resource.Failure)
    assert(
        (result as Resource.Failure).throwable.message ==
            "Image from folder $folderName not found for user $userId")
    // Get user does not automatically return this error, should i call getQrCode in getUser? I mean
    // in theory im just getting all the user fields, but it should double check!
    unmockkAll()
  }
}
