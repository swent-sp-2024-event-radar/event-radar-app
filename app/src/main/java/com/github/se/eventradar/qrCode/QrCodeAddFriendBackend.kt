package com.github.se.eventradar.qrCode

import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.User
import com.github.se.eventradar.model.repository.user.FirebaseUserRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.*


class QrCodeAddFriendBackend {
    suspend fun addFriend(myUID: String): (String) -> Unit = {scannedUID ->
            when (val newUserFriend = FirebaseUserRepository().getUser(scannedUID)) {
                is Resource.Success ->
                   if (newUserFriend.data!!.friendList.contains(myUID)) {
                    //Navigate to message screen
                } else {
                    newUserFriend.data.friendList.add(myUID)
                    FirebaseUserRepository().updateUser(newUserFriend.data)
                   }
                is Resource.Failure -> {
                    println("Failed to Fetch from Database")
                }
            }




        }

    }
}