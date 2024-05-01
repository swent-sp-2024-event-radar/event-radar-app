package com.github.se.eventradar

import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.User
import com.github.se.eventradar.model.repository.user.IUserRepository
import com.github.se.eventradar.model.repository.user.MockUserRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

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
          profilePicUrl = "http://example.com/pic.jpg",
          qrCodeUrl = "http://example.com/qr.jpg",
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
  fun testDoesUserExist() = runTest {
    userRepository.addUser(mockUser)
    val result = userRepository.doesUserExist("1")
    assert(result is Resource.Success)
  }

  @Test
  fun testDoesUserExistFalseCase() = runTest {
    val result = userRepository.doesUserExist("2")
    assert(result is Resource.Failure)
  }

  @Test
  fun testGetCurrentUserIdSuccess() = runTest {
    (userRepository as MockUserRepository).updateCurrentUserId("1")
    val result = userRepository.getCurrentUserId()
    assert(result is Resource.Success)
    assert((result as Resource.Success).data == "1")
  }

  @Test
  fun testGetCurrentUserIdFailure() = runTest {
    (userRepository as MockUserRepository).updateCurrentUserId(null) // Ensure no user is set
    val result = userRepository.getCurrentUserId()
    assert(result is Resource.Failure)
    assert((result as Resource.Failure).throwable.message == "No user currently signed in")
  }
}
