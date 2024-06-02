package com.github.se.eventradar

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.github.se.eventradar.model.User
import com.github.se.eventradar.model.repository.user.MockUserRepository
import com.github.se.eventradar.viewmodel.CountryCode
import com.github.se.eventradar.viewmodel.ProfileUiState
import com.github.se.eventradar.viewmodel.ProfileViewModel
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
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

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelUnitTest {

  @RelaxedMockK private lateinit var viewModelFriend: ProfileViewModel
  @RelaxedMockK private lateinit var viewModelUser: ProfileViewModel
  private lateinit var userRepository: MockUserRepository
  private lateinit var mockUiState: MutableStateFlow<ProfileUiState>

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
          bio = "This is my bio",
          username = "jimsmith")

  private val factory =
      object : ProfileViewModel.Factory {
        override fun create(userId: String?): ProfileViewModel {
          return ProfileViewModel(userRepository, userId)
        }
      }

  @Before
  fun setUp() {
    userRepository = MockUserRepository()
    mockUiState = MutableStateFlow(ProfileUiState())
    userRepository.updateCurrentUserId("1")
    viewModelFriend = factory.create(userId = mockFriend.userId)
    viewModelUser = factory.create(null)
  }

  @After
  fun tearDown() {
    unmockkAll()
  }

  @Test
  fun viewFriendsProfileDetailsSuccessful() {
    runBlocking { userRepository.addUser(mockFriend) }
    viewModelFriend.getProfileDetails()
    assert(viewModelFriend.userId == mockFriend.userId)
    assert(viewModelFriend.uiState.value.profilePicUri == mockFriend.profilePicUrl.toUri())
    assert(viewModelFriend.uiState.value.bio == mockFriend.bio)
    assert(viewModelFriend.uiState.value.username == mockFriend.username)
    assert(viewModelFriend.uiState.value.firstName == mockFriend.firstName)
    assert(viewModelFriend.uiState.value.lastName == mockFriend.lastName)
    tearDown()
  }

  @Test
  fun viewFriendsProfileDetailsFailure() {
    mockkStatic(Log::class)
    every { Log.d(any(), any()) } returns 0
    viewModelFriend.getProfileDetails()
    verify { Log.d("ProfileViewModel", "Error getting user details for user ${mockFriend.userId}") }
    unmockkAll()
  }

  @Test
  fun viewMyProfileDetailsSuccessful() {
    runBlocking { userRepository.addUser(mockUser) }
    viewModelUser.getProfileDetails()
    assert(viewModelUser.userId == mockUser.userId)
    assert(viewModelUser.uiState.value.profilePicUri == mockUser.profilePicUrl.toUri())
    assert(viewModelUser.uiState.value.bio == mockUser.bio)
    assert(viewModelUser.uiState.value.username == mockUser.username)
    assert(viewModelUser.uiState.value.firstName == mockUser.firstName)
    assert(viewModelUser.uiState.value.lastName == mockUser.lastName)
    tearDown()
  }

  /*
  @Test
  fun viewMyProfileDetailsFailure() {
    val mockUserRepository = mockk<IUserRepository>()
    val mockViewModel = ProfileViewModel(mockUserRepository, null)
    mockkStatic(Log::class)
    every { Log.d(any(), any()) } returns 0
    coEvery { mockUserRepository.getCurrentUserId() } returns
        Resource.Failure(Exception("Error getting current user id"))
    mockViewModel.getProfileDetails()
    verify { Log.d("ProfileViewModel", "Error getting current user id") }
    unmockkAll()
  }

   */

  @Test
  fun updateMyUserInfoSuccessful() {
    runBlocking { userRepository.addUser(mockUser) }
    viewModelUser.getProfileDetails()
    viewModelUser.onFirstNameChanged("Jane")
    viewModelUser.onLastNameChanged("Doe")
    viewModelUser.onUsernameChanged("janedoe")
    viewModelUser.onBirthDateChanged("01.01.2000")
    viewModelUser.onCountryCodeChanged(CountryCode.CH)
    viewModelUser.onPhoneNumberChanged("123456789")
    viewModelUser.onSelectedImageUriChanged(Uri.EMPTY)
    viewModelUser.onBioChanged("This is my bio")
    viewModelUser.updateUserInfo()
    assert(viewModelUser.uiState.value.firstName == "Jane")
    assert(viewModelUser.uiState.value.lastName == "Doe")
    assert(viewModelUser.uiState.value.username == "janedoe")
    assert(viewModelUser.uiState.value.birthDate == "01.01.2000")
    assert(viewModelUser.uiState.value.selectedCountryCode == CountryCode.CH)
    assert(viewModelUser.uiState.value.phoneNumber == "123456789")
    assert(viewModelUser.uiState.value.profilePicUri == Uri.EMPTY)
    assert(viewModelUser.uiState.value.bio == "This is my bio")
    tearDown()
  }

  @Test
  fun updateMyUserInfoGettingUserFailure() {
    mockkStatic(Log::class)
    every { Log.d(any(), any()) } returns 0
    viewModelUser.updateUserInfo()
    verify { Log.d("ProfileViewModel", "Error getting User ${mockUser.userId}") }
    unmockkAll()
  }

  @Test
  fun testOnFirstNameChange() = runTest {
    mockkStatic(Log::class)
    every { Log.d(any(), any()) } returns 0
    viewModelUser.onFirstNameChanged(mockUser.firstName, mockUiState)
    assert(mockUiState.value.firstName == mockUser.firstName)
    unmockkAll()
  }

  @Test
  fun testOnLastNameChange() = runTest {
    viewModelUser.onLastNameChanged(mockUser.lastName, mockUiState)
    assert(mockUiState.value.lastName == mockUser.lastName)
  }

  @Test
  fun testOnBirthDateChange() = runTest {
    viewModelUser.onBirthDateChanged(mockUser.birthDate, mockUiState)
    assert(mockUiState.value.birthDate == mockUser.birthDate)
  }

  @Test
  fun testOnSelectedImageUriChange() = runTest {
    viewModelUser.onSelectedImageUriChanged(Uri.EMPTY, mockUiState)
    assert(mockUiState.value.profilePicUri == Uri.EMPTY)
  }

  @Test
  fun testOnBioChange() = runTest {
    viewModelUser.onBioChanged(mockFriend.bio, mockUiState)
    assert(mockUiState.value.bio == mockFriend.bio)
  }

  @Test
  fun testValidateFieldsForCorrectFields() = runTest {
    val swissPhoneNumber = "123456789"

    viewModelUser.onFirstNameChanged(mockUser.firstName, mockUiState)
    viewModelUser.onLastNameChanged(mockUser.lastName, mockUiState)
    viewModelUser.onUsernameChanged(mockUser.username, mockUiState)
    viewModelUser.onBirthDateChanged(mockUser.birthDate, mockUiState)
    viewModelUser.onCountryCodeChanged(CountryCode.CH, mockUiState)
    viewModelUser.onPhoneNumberChanged(swissPhoneNumber, mockUiState)
    viewModelUser.onSelectedImageUriChanged(Uri.EMPTY, mockUiState)

    assert(viewModelUser.validateFields(mockUiState))
  }

  @Test
  fun testValidateFieldTooOldBirthdate() = runTest {

    // set valid initial state
    val swissPhoneNumber = "123456789"
    viewModelUser.onFirstNameChanged(mockUser.firstName, mockUiState)
    viewModelUser.onLastNameChanged(mockUser.lastName, mockUiState)
    viewModelUser.onUsernameChanged(mockUser.username, mockUiState)
    viewModelUser.onBirthDateChanged(mockUser.birthDate, mockUiState)
    viewModelUser.onCountryCodeChanged(CountryCode.CH, mockUiState)
    viewModelUser.onPhoneNumberChanged(swissPhoneNumber, mockUiState)
    viewModelUser.onSelectedImageUriChanged(Uri.EMPTY, mockUiState)
    // assert state is valid
    assert(viewModelUser.validateFields(mockUiState))

    // change the birthdate with invalid
    viewModelUser.onBirthDateChanged("02.09.1000", mockUiState)

    assert(!viewModelUser.validateFields())
  }

  @Test
  fun testValidateFieldForEmptyFields() = runTest {
    assert(!viewModelUser.validateFields(mockUiState))
  }

  @Test
  fun testValidateForPartiallyFilledFields() = runTest {
    viewModelUser.onFirstNameChanged(mockUser.firstName, mockUiState)
    viewModelUser.onLastNameChanged(mockUser.lastName, mockUiState)
    viewModelUser.onUsernameChanged(mockUser.username, mockUiState)
    viewModelUser.onBirthDateChanged(mockUser.birthDate, mockUiState)
    viewModelUser.onCountryCodeChanged(CountryCode.AU, mockUiState)

    assert(!viewModelUser.validateFields(mockUiState))
  }

  @Test
  fun getUiState() {
    assert(viewModelUser.getUiState() == mockUiState.value)
  }
}
