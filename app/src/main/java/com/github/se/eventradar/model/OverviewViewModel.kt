package com.github.se.eventradar.model

import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.se.eventradar.model.location.getLocation
import com.github.se.eventradar.model.todo.ToDo
import com.github.se.eventradar.model.todo.ToDoList
import com.github.se.eventradar.model.todo.getToDoStatus
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class OverviewViewModel(db: FirebaseFirestore = Firebase.firestore) : ViewModel() {
  private val _uiState = MutableStateFlow(OverviewUiState())

  val uiState: StateFlow<OverviewUiState> = _uiState

  private val taskRef = db.collection("tasks")

  fun onSearchQueryChanged(query: String) {
    _uiState.value = _uiState.value.copy(searchQuery = query)
    filterToDos()
  }

  private fun filterToDos() {
    val query = _uiState.value.searchQuery
    val filteredToDos =
        _uiState.value.toDoList.getAllTask.filter { it.title.contains(query, ignoreCase = true) }
    _uiState.value =
        _uiState.value.copy(
            toDoList = _uiState.value.toDoList.copy(getFilteredTask = filteredToDos))
  }

  fun getToDos() {
    taskRef
        .get()
        .addOnSuccessListener { result ->
          val todos =
              result.documents.map { document ->
                ToDo(
                    id = document.id,
                    title = document.data?.get("name") as String,
                    assigneeName = document.data?.get("assigneeName") as String,
                    dueDate = getLocalDate(document.data?.get("dueDate") as String),
                    location =
                        getLocation(
                            document.data?.get("location_name") as String,
                            document.data?.get("location_lat") as Double,
                            document.data?.get("location_lng") as Double,
                        ),
                    description = document.data?.get("description") as String,
                    status = getToDoStatus(document.data?.get("status") as String))
              }
          _uiState.value =
              _uiState.value.copy(
                  toDoList = ToDoList(todos, todos, _uiState.value.toDoList.getTask))
        }
        .addOnFailureListener { exception ->
          Log.d("OverviewViewModel", "Error getting documents: ", exception)
        }
  }

  fun onTaskClicked(id: String) {
    val toDo = _uiState.value.toDoList.getAllTask.find { it.id == id }
    _uiState.value =
        _uiState.value.copy(
            toDoList =
                ToDoList(
                    _uiState.value.toDoList.getAllTask,
                    _uiState.value.toDoList.getFilteredTask,
                    toDo))
  }

  companion object {
    fun getLocalDate(date: String): LocalDate {
      val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
      return LocalDate.parse(date, formatter)
    }
  }
}

data class OverviewUiState(
    val toDoList: ToDoList = ToDoList(emptyList(), emptyList(), null),
    val searchQuery: String = "",
)
