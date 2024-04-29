package com.github.se.eventradar.viewmodel.qrCode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.repository.user.FirebaseUserRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

enum class NavigationEvent {
    None,
    NavigateToNextScreen
}

class QrCodeFriendViewModel(private val firebaseRepository: FirebaseUserRepository, // Dependency injection
                            private val qrCodeAnalyser: QrCodeAnalyser = QrCodeAnalyser()): ViewModel() {

    private val _decodedResult = MutableStateFlow<String?>(null)
    val decodedString: StateFlow<String?> = _decodedResult.asStateFlow()

    private val _navigationEvent = MutableStateFlow<NavigationEvent>(NavigationEvent.None)
    val navigationEvent: StateFlow<NavigationEvent> = _navigationEvent.asStateFlow()

    init {
        qrCodeAnalyser.onDecoded = { decodedString ->
            _decodedResult.value = decodedString ?: "Failed to decode QR Code"
        }

    viewModelScope.launch {
        _decodedResult
            .filterNotNull()  // Only continue if the value is not null
            .collect { result ->
                if (result != "Failed to decode QR Code") {
                    updateFriendList(result)
                }
            }
    }
}
    private fun updateFriendList(friendID: String) {
        viewModelScope.launch {
            val myUID = firebaseRepository.getUser("mockId")
                .toString()  // Fetch the current user ID correctly //TODO CHANGE TO GET MY CURRENT()

            val friendUserDeferred = async { firebaseRepository.getUser(friendID) }
            val currentUserDeferred =
                async { firebaseRepository.getUser(myUID) }  // Assume getting current user ID correctly

            val friendUser = friendUserDeferred.await()
            val currentUser = currentUserDeferred.await()

            if (friendUser is Resource.Success && currentUser is Resource.Success) {
                val friendUpdatesDeferred = async {
                    if (!friendUser.data!!.friendList.contains(myUID)) {
                        friendUser.data.friendList.add(myUID)
                        firebaseRepository.updateUser(friendUser.data)
                    } else {
                        Resource.Success(Unit)  // No update needed, still success
                    }
                }
                val userUpdatesDeferred = async {
                    if (!currentUser.data!!.friendList.contains(friendID)) {
                        currentUser.data.friendList.add(friendID)
                        firebaseRepository.updateUser(currentUser.data)
                    } else {
                        Resource.Success(Unit)  // No update needed, still success
                    }
                }

                val friendUpdateResult =friendUpdatesDeferred.await()
                val userUpdateResult = userUpdatesDeferred.await()

                if (friendUpdateResult is Resource.Success && userUpdateResult is Resource.Success) {
                    _navigationEvent.value = NavigationEvent.NavigateToNextScreen
                }

            } else {
                println("Failed to fetch user data from Firebase")
            }
        }
    }
    fun resetNavigationEvent() {
        _navigationEvent.value = NavigationEvent.None
    }
}