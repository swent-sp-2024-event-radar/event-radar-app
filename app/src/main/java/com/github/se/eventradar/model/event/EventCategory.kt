package com.github.se.eventradar.model.event

enum class EventCategory(val displayName: String) {
  MUSIC("Music"),
  SPORTS("Sports"),
  CONFERENCE("Conference"),
  EXHIBITION("Exhibition"),
  COMMUNITY("Community"),
  SOCIAL("Social"),
}

fun getEventCategory(categoryString: String): EventCategory {
  return try {
    enumValueOf<EventCategory>(categoryString.uppercase())
  } catch (e: IllegalArgumentException) {
    EventCategory.SOCIAL // Default to SOCIAL if categoryString is not valid
  }
}
