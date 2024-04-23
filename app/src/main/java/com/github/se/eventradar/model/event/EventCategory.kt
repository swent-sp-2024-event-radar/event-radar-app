package com.github.se.eventradar.model.event

enum class EventCategory {
  MUSIC,
  SPORTS,
  CONFERENCE,
  EXHIBITION,
  COMMUNITY,
  SOCIAL
}

fun getEventCategory(categoryString: String): EventCategory {
  return when (categoryString.uppercase()) {
    "MUSIC" -> EventCategory.MUSIC
    "SPORTS" -> EventCategory.SPORTS
    "CONFERENCE" -> EventCategory.CONFERENCE
    "EXHIBITION" -> EventCategory.EXHIBITION
    "COMMUNITY" -> EventCategory.COMMUNITY
    "SOCIAL" -> EventCategory.SOCIAL
    else -> EventCategory.SOCIAL // default is SOCIAL
  }
}