package com.github.se.eventradar.model.todo

data class ToDoList(
    val getAllTask: List<ToDo>,
    var getFilteredTask: List<ToDo> = getAllTask,
    val getTask: ToDo? = null,
)
