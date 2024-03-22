package com.github.se.eventradar.model.todo

import com.github.se.eventradar.model.location.Location
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
