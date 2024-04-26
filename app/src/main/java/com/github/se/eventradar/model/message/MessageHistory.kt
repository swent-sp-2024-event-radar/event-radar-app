package com.github.se.eventradar.model.message

data class MessageHistory(
    val user1: String,
    val user2: String,
    var latestMessageId: String,
    val messages: MutableList<Message>,
    val id: String = "",
) {
  constructor(
      map: Map<String, Any>,
      id: String,
  ) : this(
      user1 = map["from_user"] as String,
      user2 = map["to_user"] as String,
      latestMessageId = map["latest_message_id"] as String,
      messages = getMapOfMessages(map["messages"]).map { Message(it) }.toMutableList(),
      id = id,
  )

  fun toMap(): HashMap<String, Any> {
    val map = HashMap<String, Any>()
    map["from_user"] = user1
    map["to_user"] = user2
    map["latest_message_id"] = latestMessageId
    map["messages"] = messages.map { it.toMap() }
    return map
  }
}

private fun getMapOfMessages(map: Any?): List<Map<String, Any>> {
  return when (map) {
    is List<*> -> map.filterIsInstance<Map<String, Any>>().toMutableList()
    else -> emptyList()
  }
}
