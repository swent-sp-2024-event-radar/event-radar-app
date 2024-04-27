package com.github.se.eventradar.model

data class User(
    val userId: String,
    val age: Int,
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
      eventsAttendeeList = convertToSetOfStrings(map["eventsAttendeeList"]),
      eventsHostList = convertToSetOfStrings(map["eventsHostList"]),
      friendList = convertToSetOfStrings(map["friendList"]),
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
    map["friendList"] = friendList
    map["profilePicUrl"] = profilePicUrl
    map["qrCodeUrl"] = qrCodeUrl
    map["username"] = username
    return map
  }
}

private fun convertToSetOfStrings(data: Any?): MutableSet<String> {
  return when (data) {
    is List<*> -> data.filterIsInstance<String>().toMutableSet()
    is String -> mutableSetOf(data)
    else -> mutableSetOf()
  }
}
