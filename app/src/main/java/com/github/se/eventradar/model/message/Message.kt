package com.github.se.eventradar.model.message

import java.time.LocalDateTime

data class Message(
    val sender: String,
    val content: String,
    var dateTimeSent: LocalDateTime,
    val id: String,
) {
  constructor(
      map: Map<String, Any>,
      id: String = "",
  ) : this(
      sender = map["sender"] as String,
      content = map["content"] as String,
      dateTimeSent = map["date_time_sent"] as LocalDateTime,
      id = id,
  )

  fun toMap(): HashMap<String, Any> {
    val map = HashMap<String, Any>()
    map["sender"] = sender
    map["content"] = content
    map["date_time_sent"] = dateTimeSent
    return map
  }
}
