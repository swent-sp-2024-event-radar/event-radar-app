package com.github.se.eventradar

import com.github.se.eventradar.model.ToDoUiState
import com.github.se.eventradar.model.ToDoViewModel
import com.github.se.eventradar.model.location.Location
import com.github.se.eventradar.model.todo.ToDoStatus
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.confirmVerified
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.slot
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TodoViewModelUnitTest {

  @get:Rule val mockkRule = MockKRule(this)

  @RelaxedMockK lateinit var db: FirebaseFirestore

  @RelaxedMockK lateinit var vm: ToDoViewModel

  private val uiState =
      ToDoUiState(
          id = "1",
          title = "Test Title",
          description = "Test Description",
          status = ToDoStatus.STARTED,
          location = Location(name = "Test Location", latitude = 0.0, longitude = 0.0),
      )

  @Before
  fun setup() {
    vm = ToDoViewModel(db = db)
  }

  @Test
  fun `submitNewToDo() correctly calls the database`() = run {
    vm.submitNewToDo(uiState)

    val todo =
        hashMapOf(
            "name" to uiState.title,
            "assigneeName" to uiState.assigneeName,
            "dueDate" to uiState.dueDate,
            "location_name" to uiState.location.name,
            "location_lat" to uiState.location.latitude,
            "location_lng" to uiState.location.longitude,
            "description" to uiState.description,
            "status" to ToDoStatus.CREATED.statusName,
        )

    val capture = slot<Map<String, Any>>()

    verify { db.collection("tasks").add(capture(capture)) }
    confirmVerified(db)

    assert(capture.captured == todo)
  }

  @Test
  fun `submitToDoEdits() correctly calls the database`() = run {
    vm.submitToDoEdits(uiState)

    val todo =
        hashMapOf(
            "name" to uiState.title,
            "assigneeName" to uiState.assigneeName,
            "dueDate" to uiState.dueDate,
            "location_name" to uiState.location.name,
            "location_lat" to uiState.location.latitude,
            "location_lng" to uiState.location.longitude,
            "description" to uiState.description,
            "status" to uiState.status.statusName,
        )

    val capture = slot<Map<String, Any>>()

    verify { db.collection("tasks").document(uiState.id).update(capture(capture)) }
    confirmVerified(db)

    assert(capture.captured == todo)
  }

  @Test
  fun `deleteToDo() correctly calls the database`() = run {
    vm.deleteToDo(uiState)

    verify { db.collection("tasks").document(uiState.id).delete() }
    confirmVerified(db)
  }
}
