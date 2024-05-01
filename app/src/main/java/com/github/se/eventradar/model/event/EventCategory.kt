package com.github.se.eventradar.model.event

import android.content.Context
import android.util.Log
import com.github.se.eventradar.R

enum class EventCategory(val displayName: String) {
  MUSIC("Music"),
  SPORTS("Sports"),
  CONFERENCE("Conference"),
  EXHIBITION("Exhibition"),
  COMMUNITY("Community"),
  SOCIAL("Social"),
  PARTY("Party");

  fun convertToResString(context: Context): String {
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
