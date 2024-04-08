package com.github.se.eventradar.model

import androidx.lifecycle.ViewModel
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
  }

  fun onTaskClicked(id: String) {
      // Temporarily commented out as we transition from handling to-dos to handling events.

      /*
    val to_Do = _uiState.value.toDoList.getAllTask.find { it.id == id }
    _uiState.value =
        _uiState.value.copy(
            toDoList =
                ToDoList(
                    _uiState.value.toDoList.getAllTask,
                    _uiState.value.toDoList.getFilteredTask,
                    to_Do))
       */
  }

  companion object {
    fun getLocalDate(date: String): LocalDate {
      val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
      return LocalDate.parse(date, formatter)
    }
  }
}

data class OverviewUiState(
    val searchQuery: String = "",
)
