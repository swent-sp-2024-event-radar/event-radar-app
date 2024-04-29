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
) {
  constructor(
      map: Map<String, Any>,
      id: String
  ) : this(
      userId = id,
      age = map["age"] as Int,
      email = map["email"] as String,
      firstName = map["firstName"] as String,
      lastName = map["lastName"] as String,
      phoneNumber = map["phoneNumber"] as String,
      accountStatus = map["accountStatus"] as String,
      eventsAttendeeList = convertToListOfStrings(map["eventsAttendeeList"]),
      eventsHostList = convertToListOfStrings(map["eventsHostList"]),
      profilePicUrl = map["profilePicUrl"] as String,
      qrCodeUrl = map["qrCodeUrl"] as String,
      username = map["username"] as String)

  fun toMap(): HashMap<String, Any> {
    val map = HashMap<String, Any>()
    map["userId"] = userId
    map["age"] = age
    map["email"] = email
    map["firstName"] = firstName
    map["lastName"] = lastName
    map["phoneNumber"] = phoneNumber
    map["accountStatus"] = accountStatus
    map["eventsAttendeeList"] = eventsAttendeeList
    map["eventsHostList"] = eventsHostList
    map["profilePicUrl"] = profilePicUrl
    map["qrCodeUrl"] = qrCodeUrl
    map["username"] = username
    return map
  }
}

private fun convertToListOfStrings(data: Any?): List<String> {
  return when (data) {
    is List<*> -> data.filterIsInstance<String>().toList()
    is String -> listOf(data)
    else -> emptyList()
  }
}
