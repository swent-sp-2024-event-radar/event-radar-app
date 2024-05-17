package com.github.se.eventradar

import android.net.Uri
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.User
import com.github.se.eventradar.model.repository.user.IUserRepository
import com.github.se.eventradar.model.repository.user.MockUserRepository
import com.github.se.eventradar.viewmodel.ProfileUiState
import com.github.se.eventradar.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseUser
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class ProfileViewModelTest {

  @RelaxedMockK lateinit var viewModel: ProfileViewModel
  @RelaxedMockK lateinit var user: FirebaseUser
  @RelaxedMockK lateinit var uri: Uri

  private lateinit var mockUiState: MutableStateFlow<ProfileUiState>
  private lateinit var userRepository: IUserRepository

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

  val mockUser =
      User(
          userId = "1",
          birthDate = "01/01/2000",
          email = "test@test.com",
          firstName = "John",
          lastName = "Doe",
          phoneNumber = "1234567890",
          accountStatus = "active",
          eventsAttendeeList = mutableListOf(),
          eventsHostList = mutableListOf(),
          friendsList = mutableListOf(),
          profilePicUrl = "",
          qrCodeUrl = "",
          username = "john_doe")

  @get:Rule val mainDispatcherRule = MainDispatcherRule()

  @Before
  fun setUp() {
    userRepository = MockUserRepository()
    viewModel = ProfileViewModel(userRepository)
    mockUiState = MutableStateFlow(ProfileUiState())
    user = mockk<FirebaseUser>()
    uri =
        mockk<Uri> {
          every { path } returns
              "content://media/picker/0/com.android.providers.media.photopicker/media/1000009885"
        }

    every { user.uid } returns "1"
    every { user.email } returns "test@test.com"
  }

  @Test
  fun `updateField updates ProfileUiState correctly`() = runTest {
    userRepository.addUser(mockUser)

    // Arrange
    val field = "username"
    val newValue = "newUsername"
    Mockito.`when`(userRepository.updateUserField("1", field, newValue))
        .thenReturn(Resource.Success(Unit))

    // Act
    viewModel.updateUserData(field, newValue)

    // Assert
    val uiState = viewModel.uiState.first()
    assertEquals(newValue, uiState.username)
  }
}
