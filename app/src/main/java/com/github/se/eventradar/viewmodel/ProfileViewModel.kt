package com.github.se.eventradar.viewmodel

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.repository.user.IUserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@HiltViewModel(assistedFactory = ProfileViewModel.Factory::class)
class ProfileViewModel
@AssistedInject
constructor(
    private val userRepository: IUserRepository,
    @Assisted var userId: String?
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        getProfileDetails()
    }

    fun getProfileDetails() {
        viewModelScope.launch {
            if (userId == null) {
                when (val currentUid = userRepository.getCurrentUserId()) {
                    is Resource.Success -> {
                        userId = currentUid.data
                    }
                    is Resource.Failure ->
                        Log.d(
                            "ProfileViewModel",
                            "Error getting current user id")
                }
            } else {
                when (val userResource = userRepository.getUser(userId!!)) {
                    is Resource.Success -> {
                        val user = userResource.data
                        if (user != null) {
                            _uiState.update {
                                it.copy(
                                    profilePicUrl = user.profilePicUrl,
                                    firstName = user.firstName,
                                    lastName = user.lastName,
                                    username = user.username,
                                    bio = user.bio,
                                    phoneNumber = user.phoneNumber,
                                    birthDate = user.birthDate
                                )
                            }
                        }
                    }

                    is Resource.Failure ->
                        Log.d(
                            "ProfileViewModel",
                            "Error getting user details for user $userId"
                        )
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(userId: String?): ProfileViewModel
    }

    companion object {
        @Composable
        fun create(userId: String? = null): ProfileViewModel {
            return hiltViewModel<ProfileViewModel, Factory>(
                creationCallback = { factory -> factory.create(userId = userId) })
        }
    }
}

data class ProfileUiState(
    val profilePicUrl: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val username: String = "",
    val bio: String = "",
    val location: String = "",
    val phoneNumber: String = "",
    val birthDate: String = ""
)
