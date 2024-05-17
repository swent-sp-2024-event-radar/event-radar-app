package com.github.se.eventradar.model.message

data class MessageHistory(
    val user1: String,
    val user2: String,
    var latestMessageId: String,
    var user1ReadMostRecentMessage: Boolean,
    var user2ReadMostRecentMessage: Boolean,
    val messages: MutableList<Message>,
    val id: String = "",
) {
  constructor(
      map: Map<String, Any>,
      id: String,
  ) : this(
      user1 = map["from_user"] as String,
      user2 = map["to_user"] as String,
      user1ReadMostRecentMessage = map["from_user_read"] as Boolean,
      user2ReadMostRecentMessage = map["to_user_read"] as Boolean,
      latestMessageId = map["latest_message_id"] as String,
      messages = getMutableListOfMessages(map["messages"]).toMutableList(),
      id = id,
  )

  fun toMap(): HashMap<String, Any> {
    val map = HashMap<String, Any>()
    map["from_user"] = user1
    map["to_user"] = user2
    map["latest_message_id"] = latestMessageId
    map["from_user_read"] = user1ReadMostRecentMessage
    map["to_user_read"] = user2ReadMostRecentMessage
    return map
  }
}

fun getMutableListOfMessages(messages: Any?): MutableList<Message> {
  return when (messages) {
    is List<*> -> messages.filterIsInstance<Message>().toMutableList()
    else -> mutableListOf()
  }
}
