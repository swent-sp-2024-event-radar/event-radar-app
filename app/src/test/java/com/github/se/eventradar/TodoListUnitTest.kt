package com.github.se.eventradar

import com.github.se.eventradar.model.location.Location
import com.github.se.eventradar.model.todo.ToDo
import com.github.se.eventradar.model.todo.ToDoList
import com.github.se.eventradar.model.todo.ToDoStatus
import java.time.LocalDate
import org.junit.Test

class TodoListUnitTest {
  @Test
  fun todoList_isCorrect() = run {
    val todoList =
        ToDoList(
            listOf(
                ToDo(
                    id = "1",
                    title = "Test 1",
                    assigneeName = "Test",
                    dueDate = LocalDate.now(),
                    location = Location(0.0, 0.0, "Test"),
                    description = "Test",
                    status = ToDoStatus.CREATED),
            ))

    assert(todoList.getAllTask.size == 1)
    assert(todoList.getFilteredTask == todoList.getAllTask)
    assert(todoList.getTask == null)
  }
}
