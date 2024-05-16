package com.github.se.eventradar.model

import com.github.se.eventradar.model.ConversionUtils.convertToMutableListOfStrings

data class User(
    val userId: String,
    val birthDate: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val accountStatus: String,
    val eventsAttendeeList: MutableList<String>,
    val eventsHostList: MutableList<String>,
    val friendsList: MutableList<String>,
    val profilePicUrl: String,
    val qrCodeUrl: String,
    val bio: String,
    val username: String,
) {
  constructor(
      map: Map<String, Any?>,
      id: String
  ) : this(
      userId = id,
      birthDate = map["private/birthDate"] as String,
      email = map["private/email"] as String,
      firstName = map["private/firstName"] as String,
      lastName = map["private/lastName"] as String,
      phoneNumber = map["private/phoneNumber"] as String,
      accountStatus = map["accountStatus"] as String,
      eventsAttendeeList = convertToMutableListOfStrings(map["eventsAttendeeList"]),
      eventsHostList = convertToMutableListOfStrings(map["eventsHostList"]),
      friendsList = convertToMutableListOfStrings(map["friendsList"]),
      profilePicUrl = map["profilePicUrl"] as String,
      qrCodeUrl = map["qrCodeUrl"] as String,
      bio = map ["bio"] as String,
      username = map["username"] as String)

  fun toMap(): HashMap<String, Any> {
    val map = HashMap<String, Any>()
    map["private/birthDate"] = birthDate
    map["private/email"] = email
    map["private/firstName"] = firstName
    map["private/lastName"] = lastName
    map["private/phoneNumber"] = phoneNumber
    map["accountStatus"] = accountStatus
    map["eventsAttendeeList"] = eventsAttendeeList
    map["eventsHostList"] = eventsHostList
    map["friendsList"] = friendsList
    map["profilePicUrl"] = profilePicUrl
    map["qrCodeUrl"] = qrCodeUrl
      map["bio"] = bio
    map["username"] = username
    return map
  }
}
