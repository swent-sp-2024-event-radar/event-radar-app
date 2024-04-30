package com.github.se.eventradar.model

data class User(
    val userId: String,
    val birthDate: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val accountStatus: String,
    val eventsAttendeeList: MutableSet<String>,
    val eventsHostList: MutableSet<String>,
    val friendList: MutableSet<String>,
    val profilePicUrl: String,
    val qrCodeUrl: String,
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
      eventsAttendeeList = convertToListOfStrings(map["eventsAttendeeList"]),
      eventsHostList = convertToListOfStrings(map["eventsHostList"]),
      friendList = convertToListOfStrings(map["friendList"]),
      profilePicUrl = map["profilePicUrl"] as String,
      qrCodeUrl = map["qrCodeUrl"] as String,
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
    map["profilePicUrl"] = profilePicUrl
    map["qrCodeUrl"] = qrCodeUrl
    map["username"] = username
    return map
  }
}

private fun convertToListOfStrings(data: Any?): MutableSet<String> {
  return when (data) {
    is List<*> -> data.filterIsInstance<String>().toMutableSet()
    is String -> mutableSetOf(data)
    else -> mutableSetOf()
  }
}
