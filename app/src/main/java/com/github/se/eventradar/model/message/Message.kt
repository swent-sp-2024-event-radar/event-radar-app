package com.github.se.eventradar.model.message

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
      dateTimeSent =
          LocalDateTime.parse(
              map["date_time_sent"] as String, DateTimeFormatter.ISO_LOCAL_DATE_TIME),
      id = id,
  )

  fun toMap(): HashMap<String, Any> {
    val map = HashMap<String, Any>()
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    map["sender"] = sender
    map["content"] = content
    map["date_time_sent"] = dateTimeSent.format(formatter)
    return map
  }
}
