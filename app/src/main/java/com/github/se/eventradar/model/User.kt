package com.github.se.eventradar.model

data class User(
    val userId: String,
    val age: Int,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val accountStatus: String,
    val eventsAttendeeList: List<String>,
    val eventsHostList: List<String>,
    val profilePicUrl: String,
    val qrCodeUrl: String,
    val username: String,
)
