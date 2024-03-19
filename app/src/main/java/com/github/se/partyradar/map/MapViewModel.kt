package com.github.se.partyradar.map

import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.se.partyradar.model.OverviewViewModel.Companion.getLocalDate
import com.github.se.partyradar.model.location.getLocation
import com.github.se.partyradar.model.todo.ToDo
import com.github.se.partyradar.model.todo.ToDoList
import com.github.se.partyradar.model.todo.getToDoStatus
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MapViewModel : ViewModel() {
  private val _uiState = MutableStateFlow(MapUiState())
  val uiState: StateFlow<MapUiState> = _uiState

  private val db = Firebase.firestore

  fun getToDos() {
    db.collection("tasks")
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
}

data class MapUiState(
    val toDoList: ToDoList = ToDoList(emptyList(), emptyList(), null),
)
