package com.github.se.partyradar.model.todo

import androidx.compose.ui.graphics.Color

enum class ToDoStatus(val statusName: String, val statusColor: Color) {
  CREATED("To Do", Color(0xFF00668A)),
  STARTED("In Progress", Color(0xFFFB9905)),
  ENDED("Done", Color(0xFF1FC959)),
  ARCHIVED("Archived", Color(0xFFA0A0A0)),
}

fun getToDoStatus(statusName: String): ToDoStatus {
  return when (statusName) {
    "To Do" -> ToDoStatus.CREATED
    "In Progress" -> ToDoStatus.STARTED
    "Done" -> ToDoStatus.ENDED
    "Archived" -> ToDoStatus.ARCHIVED
    else -> ToDoStatus.CREATED
  }
}
