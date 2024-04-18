package com.github.se.eventradar.model.event

import android.content.Context
import com.github.se.eventradar.R

enum class EventCategory {
  MUSIC,
  SPORTS,
  CONFERENCE,
  EXHIBITION,
  COMMUNITY,
  SOCIAL,
  PARTY;

  fun toString(context: Context): String {
    return when (this) {
      MUSIC -> context.getString(R.string.event_category_music)
      SPORTS -> context.getString(R.string.event_category_sport)
      CONFERENCE -> context.getString(R.string.event_category_conference)
      PARTY -> context.getString(R.string.event_category_party)
      EXHIBITION -> context.getString(R.string.event_category_exhibition)
      COMMUNITY -> context.getString(R.string.event_category_communities)
      SOCIAL -> context.getString(R.string.event_category_social)
    }
  }
}

fun getEventCategory(categoryString: String): EventCategory {
  return when (categoryString.uppercase()) {
    "MUSIC" -> EventCategory.MUSIC
    "SPORTS" -> EventCategory.SPORTS
    "CONFERENCE" -> EventCategory.CONFERENCE
    "EXHIBITION" -> EventCategory.EXHIBITION
    "COMMUNITY" -> EventCategory.COMMUNITY
    "PARTY" -> EventCategory.PARTY
    "SOCIAL" -> EventCategory.SOCIAL
    else -> EventCategory.SOCIAL // default is SOCIAL
  }
}
