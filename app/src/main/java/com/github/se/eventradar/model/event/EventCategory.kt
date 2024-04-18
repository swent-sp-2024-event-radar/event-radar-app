package com.github.se.eventradar.model.event

enum class EventCategory {
  MUSIC,
  SPORTS,
  CONFERENCE,
  EXHIBITION,
  COMMUNITY
}

fun getEventCategory(categoryString: String): EventCategory {
  return when (categoryString.uppercase()) {
    "MUSIC" -> EventCategory.MUSIC
    "SPORTS" -> EventCategory.SPORTS
    "CONFERENCE" -> EventCategory.CONFERENCE
    "EXHIBITION" -> EventCategory.EXHIBITION
    "COMMUNITY" -> EventCategory.COMMUNITY
    else -> EventCategory.MUSIC // default is MUSIC
  }
}
