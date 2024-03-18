package com.github.se.partyradar

import com.github.se.partyradar.model.OverviewViewModel
import com.github.se.partyradar.model.location.Location
import com.github.se.partyradar.model.todo.ToDo
import com.github.se.partyradar.model.todo.ToDoList
import com.github.se.partyradar.model.todo.ToDoStatus
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDate
import org.junit.Test

class FilteredTaskUnitTest {

  @Test
  fun `filterToDos filters the list of tasks`() = run {
    val vm = mockk<OverviewViewModel>()

    val sampleTodoList =
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
                ToDo(
                    id = "2",
                    title = "Test 2",
                    assigneeName = "Test",
                    dueDate = LocalDate.now(),
                    location = Location(0.0, 0.0, "Test"),
                    description = "Test",
                    status = ToDoStatus.CREATED)))

    every { vm.uiState.value.toDoList } returns sampleTodoList

    every { vm.onSearchQueryChanged(any()) } answers
        {
          sampleTodoList.getFilteredTask =
              sampleTodoList.getAllTask.filter {
                it.title.contains(firstArg() as String, ignoreCase = true)
              }
        }

    vm.onSearchQueryChanged("Test 1")

    assert(vm.uiState.value.toDoList.getFilteredTask.size == 1)
    assert(vm.uiState.value.toDoList.getFilteredTask[0].title == "Test 1")

    verify { vm.onSearchQueryChanged("Test 1") }
    verify { vm.uiState }
    confirmVerified(vm)
  }
}
