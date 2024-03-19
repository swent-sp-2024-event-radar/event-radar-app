package com.github.se.partyradar.model.todo

data class ToDoList(
    val getAllTask: List<ToDo>,
    var getFilteredTask: List<ToDo> = getAllTask,
    val getTask: ToDo? = null,
)
