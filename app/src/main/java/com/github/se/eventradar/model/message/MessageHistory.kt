package com.github.se.eventradar.model.message

data class MessageHistory(
  val fromUser: String,
  val toUser: String,
  val latestMessageId: String,
  val messages: List<Message>,
  val id: String = "",
) {
  constructor(
    map: Map<String, Any>,
    id: String,
  ) : this(
    fromUser = map["from_user"] as String,
    toUser = map["to_user"] as String,
    latestMessageId = map["latest_message_id"] as String,
    messages = getMapOfMessages(map["messages"]).map { Message(it) },
    id = id,
  )

  fun toMap(): HashMap<String, Any> {
    val map = HashMap<String, Any>()
    map["from_user"] = fromUser
    map["to_user"] = toUser
    map["latest_message_id"] = latestMessageId
    map["messages"] = messages.map { it.toMap() }
    return map
  }
}

private fun getMapOfMessages(map: Any?): List<Map<String, Any>> {
  return when (map) {
    is List<*> -> map.filterIsInstance<Map<String, Any>>().toList()
    else -> emptyList()
  }
}
