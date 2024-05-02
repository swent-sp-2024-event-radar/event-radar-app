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
          eventsAttendeeSet = mutableSetOf("event1", "event2"),
          eventsHostSet = mutableSetOf("event3"),
          friendsSet = mutableSetOf(),
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
  fun testDeleteUser() = runTest {
    userRepository.addUser(mockUser)
    userRepository.deleteUser(mockUser)
    val result = userRepository.getUser("1")
    assert(result is Resource.Failure)
    assert((result as Resource.Failure).throwable.message == "User with id 1 not found")
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
  fun testUploadImageUserExistsFolderExistsSuccess() = runTest {
    val mockURI = mock(Uri::class.java)
    // Add a User
    userRepository.addUser(mockUser)
    // Upload a user Image
    val result = userRepository.uploadImage(mockURI, "1", "Profile_Pictures")
    assert(result is Resource.Success)
  }

  @Test
  fun testUploadImageUserDoesNotExistFailure() = runTest {
    // Arrange
    val mockURI = mock(Uri::class.java)
    val userId = "non_existing_user"
    val folderName = "Profile_Pictures"

    // Act
    val result = userRepository.uploadImage(mockURI, userId, folderName)

    // Assert
    assertTrue(result is Resource.Failure)
    assertEquals("User with id $userId not found", (result as Resource.Failure).throwable.message)
  }

  @Test
  fun testUploadImageInvalidFolderFailure() = runTest {
    // Arrange
    val mockURI = mock(Uri::class.java)
    val userId = mockUser.userId
    val folderName = "Invalid_Folder"
    userRepository.addUser(mockUser)

    // Act
    val result = userRepository.uploadImage(mockURI, userId, folderName)

    // Assert
    assertTrue(result is Resource.Failure)
    assertEquals(
        "Folder $folderName does not exist", (result as Resource.Failure).throwable.message)
  }

  @Test
  fun testGetImageUserExistsFolderExistsSuccess() = runTest {
    // Arrange
    val expectedUrl = "http://example.com/Profile_Pictures/pic.jpg"
    val userId = mockUser.userId
    userRepository.addUser(mockUser)
    // Act
    val result = userRepository.getImage(userId, "Profile_Pictures")
    // Assert
    assertTrue(result is Resource.Success)
    assertEquals(expectedUrl, (result as Resource.Success).data)
  }

  @Test
  fun testGetImageUserDoesNotExistFailure() = runTest {
    // Arrange
    val userId = "non_existing_user"

    // Act
    val result = userRepository.getImage(userId, "Profile_Pictures")

    // Assert
    assertTrue(result is Resource.Failure)
    assertEquals("User with id $userId not found", (result as Resource.Failure).throwable.message)
  }

  @Test
  fun testGetImageInvalidFolderFailure() = runTest {
    // Arrange
    val userId = mockUser.userId
    userRepository.addUser(mockUser)
    userRepository.uploadImage(mock(Uri::class.java), userId, "Profile_Pictures")

    // Act
    val result = userRepository.getImage(userId, "Invalid_Folder")

    // Assert
    assertTrue(result is Resource.Failure)
    assertEquals(
        "Folder Invalid_Folder does not exist", (result as Resource.Failure).throwable.message)
  }

  @Test
  fun testGetImageImageNotFoundFailure() = runTest {
    // Arrange
    val userId = mockUser.userId
    val mockUserWithNoProfilePic = mockUser.copy(profilePicUrl = "")
    userRepository.addUser(mockUserWithNoProfilePic)

    // Act
    val result = userRepository.getImage(userId, "Profile_Pictures")

    // Assert
    assertTrue(result is Resource.Failure)
    assertEquals(
        "Image from folder Profile_Pictures not found for user $userId",
        (result as Resource.Failure).throwable.message)
  }

  @Test
  fun testUploadAndGetImageSuccess() = runTest {
    val expectedUrl = "http://example.com/Profile_Pictures/pic.jpg"
    val userId = mockUser.userId
    // initialize user with no mock
    val mockUserWithNoProfilePic = mockUser.copy(profilePicUrl = "")
    userRepository.addUser(mockUserWithNoProfilePic)
    // Upload Image
    userRepository.uploadImage(mock(Uri::class.java), userId, "Profile_Pictures")
    // Get Image
    val result = userRepository.getImage(userId, "Profile_Pictures")
    assertTrue(result is Resource.Success)
    assertEquals(expectedUrl, (result as Resource.Success).data)
  }
}
