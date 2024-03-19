package com.github.se.partyradar.model

import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.se.partyradar.model.location.Location
import com.github.se.partyradar.model.location.getLocation
import com.github.se.partyradar.model.todo.ToDoStatus
import com.github.se.partyradar.model.todo.getToDoStatus
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import org.json.JSONArray

class ToDoViewModel(val uid: String? = null, val db: FirebaseFirestore = Firebase.firestore) :
    ViewModel() {
  private val _uiState = MutableStateFlow(ToDoUiState())
  val uiState: StateFlow<ToDoUiState> = _uiState

  var haveFetchedTodo: Boolean = false
    private set

  fun getLongAndLad(
      query: String,
      callback: (String) -> Unit = {
        val responseArray = JSONArray(it)
        val locationList = mutableListOf<Location>()

        for (i in 0 until responseArray.length()) {
          val location = responseArray.getJSONObject(i)
          locationList.add(
              Location(
                  location.getDouble("lat"),
                  location.getDouble("lon"),
                  location.getString("name"),
              ))
        }
        onLocationListChanged(locationList)
      },
      client: OkHttpClient = OkHttpClient(),
  ) {
    val url = "https://nominatim.openstreetmap.org/search?q=${query}&format=jsonv2"
    val request = Request.Builder().url(url).build()

    client
        .newCall(request)
        .enqueue(
            object : Callback {
              override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
              }

              override fun onResponse(call: Call, response: Response) {
                response.use {
                  if (!response.isSuccessful) throw IOException("Unexpected code $response")
                  val responseBody = response.body!!.string()
                  callback(responseBody)
                }
              }
            })
  }

  fun onTitleChanged(title: String) {
    _uiState.value = _uiState.value.copy(title = title)
  }

  fun onAssigneeNameChanged(assigneeName: String) {
    _uiState.value = _uiState.value.copy(assigneeName = assigneeName)
  }

  fun onDueDateChanged(dueDate: String) {
    _uiState.value = _uiState.value.copy(dueDate = dueDate)
  }

  fun onLocationChanged(latitude: Double, longitude: Double, locationName: String) {
    _uiState.value = _uiState.value.copy(location = Location(latitude, longitude, locationName))
  }

  fun onDescriptionChanged(description: String) {
    _uiState.value = _uiState.value.copy(description = description)
  }

  fun onStatusChanged(status: ToDoStatus) {
    _uiState.value = _uiState.value.copy(status = status)
  }

  private fun onLocationListChanged(locationList: List<Location>) {
    _uiState.value = _uiState.value.copy(locationList = locationList)
  }

  fun submitNewToDo(uiState: ToDoUiState = this.uiState.value) {
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

    db.collection("tasks")
        .add(todo)
        .addOnSuccessListener { documentReference ->
          Log.d("ToDoViewModel", "ToDo added with ID: ${documentReference.id}")
        }
        .addOnFailureListener { e -> Log.d("ToDoViewModel", "Error adding ToDo: $e") }
  }

  fun submitToDoEdits(uiState: ToDoUiState = this.uiState.value) {
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

    db.collection("tasks")
        .document(uiState.id)
        .update(todo as Map<String, Any>)
        .addOnSuccessListener { Log.d("EditToDo", "ToDo updated with ID: ${uiState.id}") }
        .addOnFailureListener { e -> Log.d("EditToDo", "Error adding ToDo: $e") }
  }

  fun getTodo(id: String? = uid) {
    if (id != null) {
      db.collection("tasks")
          .document(id)
          .get()
          .addOnSuccessListener { document ->
            if (document != null) {
              _uiState.value =
                  _uiState.value.copy(
                      id = id,
                      title = document.data?.get("name") as String,
                      assigneeName = document.data?.get("assigneeName") as String,
                      dueDate = document.data?.get("dueDate") as String,
                      location =
                          getLocation(
                              document.data?.get("location_name") as String,
                              document.data?.get("location_lat") as Double,
                              document.data?.get("location_lng") as Double,
                          ),
                      description = document.data?.get("description") as String,
                      status = getToDoStatus(document.data?.get("status") as String))
              haveFetchedTodo = true
            } else {
              Log.d("EditTaskViewModel", "No such document")
            }
          }
          .addOnFailureListener { exception ->
            Log.d("EditTaskViewModel", "get failed with ", exception)
          }
    } else {
      Log.d("EditTaskViewModel", "id is null")
    }
  }

  fun deleteToDo(uiState: ToDoUiState = this.uiState.value) {
    db.collection("tasks")
        .document(uiState.id)
        .delete()
        .addOnSuccessListener {
          Log.d("EditTaskViewModel", "DocumentSnapshot successfully deleted!")
        }
        .addOnFailureListener { e -> Log.w("EditTaskViewModel", "Error deleting document", e) }
  }
}

data class ToDoUiState(
    val id: String = "",
    val title: String = "",
    val assigneeName: String = "",
    val dueDate: String = "",
    val location: Location = Location(0.0, 0.0, ""),
    val description: String = "",
    val status: ToDoStatus = ToDoStatus.CREATED,
    val locationList: List<Location> = emptyList(),
)
