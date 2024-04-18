package com.github.se.eventradar.model.event

import android.content.Context
import com.github.se.eventradar.R
import com.github.se.eventradar.model.Location
import java.time.LocalDateTime

enum class EventCategory {
  MUSIC,
  SPORTS,
  CONFERENCE,
  EXHIBITION,
  COMMUNITY,
  PARTY,
  UNDEFINED;

  fun toString(context: Context): String {
    return when (this) {
      MUSIC -> context.getString(R.string.event_category_music)
      SPORTS -> context.getString(R.string.event_category_sport)
      CONFERENCE -> context.getString(R.string.event_category_conference)
      PARTY -> context.getString(R.string.event_category_party)
      EXHIBITION -> context.getString(R.string.event_category_exhibition)
      COMMUNITY -> context.getString(R.string.event_category_communities)
      UNDEFINED -> context.getString(R.string.event_category_undefined)
    }
  }
}

data class Ticket(val name: String, val price: Double, val quantity: Int)
// new event struct final
data class Event(
    val eventName: String,
    val eventPhoto: String,
    val start: LocalDateTime,
    val end: LocalDateTime,
    val location: Location,
    val description: String,
    val ticket: Ticket,
    val contact: String,
    val organiserList: Set<String>,
    val attendeeList: Set<String>,
    val category: EventCategory,
    val fireBaseID: String
)
