package com.github.se.partyradar.model.todo

import com.github.se.partyradar.model.location.Location
import java.time.LocalDate

data class ToDo(
    val id: String,
    val title: String,
    val assigneeName: String,
    val dueDate: LocalDate,
    val location: Location,
    val description: String,
    val status: ToDoStatus? = null,
)
