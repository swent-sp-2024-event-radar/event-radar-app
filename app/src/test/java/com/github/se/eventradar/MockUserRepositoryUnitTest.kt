package com.github.se.eventradar

import android.net.Uri
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.User
import com.github.se.eventradar.model.repository.user.IUserRepository
import com.github.se.eventradar.model.repository.user.MockUserRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

class MockUserRepositoryUnitTest {
  private lateinit var userRepository: IUserRepository

  private val mockUser =
      User(
          userId = "1",
          birthDate = "01/01/2000",
          email = "test@example.com",
          firstName = "John",
          lastName = "Doe",
          phoneNumber = "1234567890",
          accountStatus = "active",
          eventsAttendeeSet = mutableListOf("event1", "event2"),
          eventsHostSet = mutableListOf("event3"),
          friendsSet = mutableListOf(),
          profilePicUrl = "http://example.com/Profile_Pictures/pic.jpg",
          qrCodeUrl = "http://example.com/QR_Codes/qr.jpg",
          username = "johndoe")

  @Before
  fun setUp() {
    userRepository = MockUserRepository()
  }

  @Test
  fun testGetUsersEmptyAtConstruction() = runTest {
    val result = userRepository.getUsers()
    assert(result is Resource.Success)
    assert((result as Resource.Success).data.isEmpty())
  }

  @Test
  fun testGetUserEmptyAtConstruction() = runTest {
    val result = userRepository.getUser("1")
    assert(result is Resource.Failure)
    assert((result as Resource.Failure).throwable.message == "User with id 1 not found")
  }

  @Test
  fun testAddAndGetUser() = runTest {
    userRepository.addUser(mockUser)
    val result = userRepository.getUser("1")
    assert(result is Resource.Success)
    assert(mockUser == (result as Resource.Success).data)
  }

  @Test
  fun testAddUserWithMap() = runTest {
    val userMap = mockUser.toMap()

    val result = userRepository.addUser(userMap, "1")
    assert(result is Resource.Success)

    val getUserResult = userRepository.getUser("1")
    assert(getUserResult is Resource.Success)
    assert(mockUser == (getUserResult as Resource.Success).data)
  }

  @Test
  fun testUpdateUser() = runTest {
    userRepository.addUser(mockUser)
    val updatedUser = mockUser.copy(firstName = "Paul")
    userRepository.updateUser(updatedUser)
    val result = userRepository.getUser("1")
    assert(result is Resource.Success)
    assert("Paul" == (result as Resource.Success).data?.firstName)
  }

  @Test
  fun testUpdateNonExistentUser() = runTest {
    // Arrange
    val nonExistentUser = User(
      userId = "nonExistentId",
      birthDate = "01/01/2000",
      email = "test@example.com",
      firstName = "John",
      lastName = "Doe",
      phoneNumber = "1234567890",
      accountStatus = "active",
      eventsAttendeeSet = mutableListOf("event1", "event2"),
      eventsHostSet = mutableListOf("event3"),
      friendsSet = mutableListOf(),
      profilePicUrl = "http://example.com/Profile_Pictures/pic.jpg",
      qrCodeUrl = "http://example.com/QR_Codes/qr.jpg",
      username = "johndoe"
    )

    // Act
    val result = userRepository.updateUser(nonExistentUser)

    // Assert
    assertTrue(result is Resource.Failure)
    assertEquals("User with id ${nonExistentUser.userId} not found", (result as Resource.Failure).throwable.message)
  }

  @Test
  fun testDeleteUser() = runTest {
    userRepository.addUser(mockUser)
    userRepository.deleteUser(mockUser)
    val result = userRepository.getUser("1")
    assert(result is Resource.Failure)
    assert((result as Resource.Failure).throwable.message == "User with id 1 not found")
  }

  @Test
  fun testDeleteNonExistentUser() = runTest {
    // Arrange
    val nonExistentUser = User(
      userId = "nonExistentId",
      birthDate = "01/01/2000",
      email = "test@example.com",
      firstName = "John",
      lastName = "Doe",
      phoneNumber = "1234567890",
      accountStatus = "active",
      eventsAttendeeSet = mutableListOf("event1", "event2"),
      eventsHostSet = mutableListOf("event3"),
      friendsSet = mutableListOf(),
      profilePicUrl = "http://example.com/Profile_Pictures/pic.jpg",
      qrCodeUrl = "http://example.com/QR_Codes/qr.jpg",
      username = "johndoe"
    )

    // Act
    val result = userRepository.deleteUser(nonExistentUser)

    // Assert
    assertTrue(result is Resource.Failure)
    assertEquals("User with id ${nonExistentUser.userId} not found", (result as Resource.Failure).throwable.message)
  }

  @Test
  fun testAddMultipleUsers() = runTest {
    val user1 = mockUser.copy(userId = "1")
    val user2 = mockUser.copy(userId = "2")
    userRepository.addUser(user1)
    userRepository.addUser(user2)
    val result = userRepository.getUsers()
    assert(result is Resource.Success)
    assert((result as Resource.Success).data.size == 2)
    assert((result).data.containsAll(listOf(user1, user2)))
  }

  @Test
  fun testIsUserLoggedIn() = runTest {
    userRepository.addUser(mockUser)
    val result = userRepository.doesUserExist("1")
    assert(result is Resource.Success)
  }

  @Test
  fun testIsUserLoggedInFalseCase() = runTest {
    val result = userRepository.doesUserExist("2")
    assert(result is Resource.Failure)
  }

  @Test
  fun testUploadImage() = runTest {
    // Arrange
    val mockURI = Uri.parse("new_pic")
    val folderName = "Profile_Pictures"
    val expectedProfilePicUrl = "http://example.com/$folderName/$mockURI.jpg"
    val addUserResult = userRepository.addUser(mockUser)
    assertTrue(addUserResult is Resource.Success)

    // Act
    val result = userRepository.uploadImage(mockURI, "1", folderName)

    // Assert
    assertTrue(result is Resource.Success)
    assertEquals(expectedProfilePicUrl, (result as Resource.Success).data)
  }

  @Test
  fun testUploadImageExceptionMessage() = runTest {
    // Arrange
    val mockURI = Uri.parse("invalid_uri")
    val expectedMessage = "Invalid URI"

    // Act
    val result = userRepository.uploadImage(mockURI, "1", "Profile_Pictures")


    // Assert
    assertTrue(userRepository.getUser("1") is Resource.Failure)
    assertTrue(result is Resource.Failure)
    assertEquals(expectedMessage, (result as Resource.Failure).throwable.message)
  }

  @Test
  fun testGetImage() = runTest {
    // Arrange
    val expectedUrl = "http://example.com/Profile_Pictures/pic.jpg"
    val userId = "1"

    // Act
    val result = userRepository.getImage(userId, "Profile_Pictures")

    // Assert
    assertTrue(result is Resource.Success)
    assertEquals(expectedUrl, (result as Resource.Success).data)
  }

  @Test
  fun testGetImageException() = runTest {
    // Arrange
    val userId = "invalid_id"

    // Act
    val result = userRepository.getImage(userId, "Profile_Pictures")

    // Assert
    assertTrue(result is Resource.Failure)
    assertEquals("Invalid user ID", (result as Resource.Failure).throwable.message)
  }
}
