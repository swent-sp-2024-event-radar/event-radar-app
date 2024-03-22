package com.github.se.eventradar.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.eventradar.model.ToDoUiState
import com.github.se.eventradar.model.ToDoViewModel
import com.github.se.eventradar.model.todo.ToDoStatus
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.swent.bootcamp.R
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun EditToDo(
    taskId: String?,
    viewModel: ToDoViewModel = viewModel(),
    navigationActions: NavigationActions
) {
  if (!viewModel.haveFetchedTodo) {
    viewModel.getTodo()
  }
  EditToDoUI(
      uiState = viewModel.uiState.collectAsState().value,
      onTitleChanged = viewModel::onTitleChanged,
      onAssigneeNameChanged = viewModel::onAssigneeNameChanged,
      onDueDateChanged = viewModel::onDueDateChanged,
      onLocationChanged = { lat, long, name -> viewModel.onLocationChanged(lat, long, name) },
      onDescriptionChanged = viewModel::onDescriptionChanged,
      goBack = navigationActions::goBack,
      getLocations = viewModel::getLongAndLad,
      editToDo = {
        viewModel.submitToDoEdits()
        navigationActions.goBack()
      },
      deleteToDo = {
        viewModel.deleteToDo()
        navigationActions.goBack()
      },
      onStatusChanged = viewModel::onStatusChanged,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditToDoUI(
    uiState: ToDoUiState,
    onTitleChanged: (String) -> Unit,
    onAssigneeNameChanged: (String) -> Unit,
    onDueDateChanged: (String) -> Unit,
    onLocationChanged: (Double, Double, String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onStatusChanged: (ToDoStatus) -> Unit,
    editToDo: () -> Unit,
    deleteToDo: () -> Unit,
    goBack: () -> Unit,
    getLocations: (String) -> Unit,
) {
  Scaffold(
      topBar = {
        TopAppBar(
            title = {
              Text(
                  text = "Edit the task",
                  style =
                      TextStyle(
                          fontSize = 24.sp,
                          lineHeight = 32.sp,
                          fontFamily = FontFamily.SansSerif,
                          fontWeight = FontWeight(400),
                      ),
                  modifier = Modifier.testTag("editTodoTitle"))
            },
            navigationIcon = {
              Icon(
                  painter = painterResource(id = R.drawable.back_arrow),
                  contentDescription = null,
                  modifier =
                      Modifier.width(24.dp)
                          .height(24.dp)
                          .clickable(onClick = { goBack() })
                          .testTag("goBackButton"),
              )
            })
      },
      modifier = Modifier.testTag("editScreen"),
  ) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(it).padding(start = 28.dp, end = 28.dp).fillMaxSize()) {
          item {
            TaskTextField(
                value = uiState.title,
                onValueChange = onTitleChanged,
                label = { Text(text = stringResource(id = R.string.title)) },
                placeholder = { Text(text = stringResource(id = R.string.title_placeholder)) },
                singleLine = true,
                modifier = Modifier.testTag("inputTodoTitle"))
            TaskTextField(
                value = uiState.description,
                onValueChange = onDescriptionChanged,
                label = { Text(text = stringResource(id = R.string.description)) },
                placeholder = {
                  Text(text = stringResource(id = R.string.description_placeholder))
                },
                modifier = Modifier.height(150.dp).testTag("inputTodoDescription"))
            TaskTextField(
                value = uiState.assigneeName,
                onValueChange = onAssigneeNameChanged,
                label = { Text(text = stringResource(id = R.string.assignee_name)) },
                placeholder = {
                  Text(text = stringResource(id = R.string.assignee_name_placeholder))
                },
                singleLine = true,
                modifier = Modifier.testTag("inputTodoAssignee"))
            LocationTextField(
                value = uiState.location.name,
                onLocationChanged = onLocationChanged,
                label = { Text(text = stringResource(id = R.string.location)) },
                placeholder = { Text(text = stringResource(id = R.string.location_placeholder)) },
                singleLine = true,
                getLocations = getLocations,
                locationList = uiState.locationList,
                modifier = Modifier.testTag("inputTodoLocation"))
            DateTextField(
                value = uiState.dueDate,
                onValueChange = onDueDateChanged,
                label = { Text(text = stringResource(id = R.string.due_date)) },
                modifier = Modifier.testTag("inputTodoDate"))
            TodoStatusTextField(
                value = uiState.status,
                onValueChanged = onStatusChanged,
                modifier = Modifier.testTag("todoStatus"))
            TextButton(
                onClick = editToDo,
                modifier =
                    Modifier.fillMaxWidth().padding(start = 28.dp, end = 28.dp).testTag("todoSave"),
                enabled =
                    isEnabled(
                        uiState.title,
                        uiState.assigneeName,
                        uiState.dueDate,
                        uiState.location.name,
                        uiState.description),
                colors =
                    ButtonDefaults.textButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
            ) {
              Text(
                  text = stringResource(id = R.string.save),
                  color = MaterialTheme.colorScheme.onPrimary,
              )
            }
            TextButton(
                onClick = { deleteToDo() },
                modifier =
                    Modifier.fillMaxWidth()
                        .padding(start = 28.dp, end = 28.dp)
                        .testTag("todoDelete"),
                colors =
                    ButtonDefaults.textButtonColors(
                        contentColor = Color.Red,
                        containerColor = Color.Transparent,
                    ),
            ) {
              Icon(
                  painter = painterResource(id = R.drawable.delete),
                  contentDescription = null,
                  modifier = Modifier.width(24.dp).height(24.dp))
              Text(
                  text = stringResource(id = R.string.delete),
              )
            }
          }
        }
  }
}

fun isEnabled(
    title: String,
    assigneeName: String,
    dueDate: String,
    location: String,
    description: String
): Boolean {
  return title.isNotEmpty() &&
      assigneeName.isNotEmpty() &&
      dueDate.isNotEmpty() &&
      location.isNotEmpty() &&
      description.isNotEmpty()
}

@Composable
fun TodoStatusTextField(
    value: ToDoStatus,
    onValueChanged: (ToDoStatus) -> Unit,
    modifier: Modifier = Modifier,
) {
  var showStatusPicker by remember { mutableStateOf(false) }

  TextButton(
      onClick = { showStatusPicker = true },
      modifier =
          modifier
              .fillMaxWidth()
              .padding(top = 8.dp, bottom = 16.dp)
              .border(width = 1.dp, color = value.statusColor, shape = RoundedCornerShape(8.dp))
              .testTag("todoStatusText"),
  ) {
    Text(
        text = value.statusName,
        color = value.statusColor,
    )
  }

  if (showStatusPicker) {
    StatusDialog(
        selected = value,
        onSubmit = {
          onValueChanged(it)
          showStatusPicker = false
        },
        onDismiss = { showStatusPicker = false })
  }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun StatusDialog(selected: ToDoStatus, onSubmit: (ToDoStatus) -> Unit, onDismiss: () -> Unit) {
  val statusChoice = mutableStateOf(selected)

  AlertDialog(
      onDismissRequest = onDismiss,
      title = { Text(text = "Choose Status", modifier = Modifier.padding(bottom = 8.dp)) },
      buttons = {
        Row(
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.End) {
              Button(
                  onClick = onDismiss,
              ) {
                Text("Cancel")
              }
              Spacer(modifier = Modifier.width(8.dp))
              Button(
                  onClick = { onSubmit(statusChoice.value) },
                  modifier = Modifier.testTag("todoStatusSubmit"),
              ) {
                Text("Submit")
              }
            }
      },
      text = {
        Column(modifier = Modifier.padding(8.dp)) {
          StatusOption(ToDoStatus.CREATED, statusChoice, Modifier.testTag("todoStatusCreated"))
          StatusOption(ToDoStatus.STARTED, statusChoice, Modifier.testTag("todoStatusStarted"))
          StatusOption(ToDoStatus.ENDED, statusChoice, Modifier.testTag("todoStatusEnded"))
          StatusOption(ToDoStatus.ARCHIVED, statusChoice, Modifier.testTag("todoStatusArchived"))
        }
      },
      backgroundColor = MaterialTheme.colorScheme.background,
      modifier = Modifier.testTag("todoStatusDialog"),
  )
}

@Composable
private fun StatusOption(
    option: ToDoStatus,
    selectedStatus: MutableState<ToDoStatus>,
    modifier: Modifier = Modifier,
) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    RadioButton(
        selected = selectedStatus.value == option,
        onClick = { selectedStatus.value = option },
        colors =
            RadioButtonDefaults.colors(
                selectedColor = option.statusColor,
                unselectedColor = Color.Gray,
            ),
        modifier = modifier)
    Text(
        text = option.statusName,
        color = option.statusColor,
        modifier = Modifier.padding(end = 8.dp))
  }
}

@Composable
fun DateTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable () -> Unit,
) {
  var showDatePicker by remember { mutableStateOf(false) }

  Box {
    TaskTextField(
        value = value, onValueChange = {}, modifier = modifier, label = label, placeholder = {})
    Box(
        modifier =
            Modifier.matchParentSize().alpha(0f).clickable(onClick = { showDatePicker = true }),
    )
  }

  if (showDatePicker) {
    DateDialog(
        onSubmit = {
          onValueChange(it)
          showDatePicker = false
        },
        onDismiss = { showDatePicker = false })
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateDialog(onSubmit: (String) -> Unit, onDismiss: () -> Unit) {
  val state = rememberDatePickerState()

  DatePickerDialog(
      onDismissRequest = onDismiss,
      confirmButton = {
        TextButton(
            onClick = { onSubmit(format(state.selectedDateMillis ?: 0L)) },
            modifier = Modifier.testTag("datePickerSubmit")) {
              Text(text = "OK")
            }
      },
      dismissButton = {
        TextButton(onClick = onDismiss, modifier = Modifier.testTag("datePickerCancel")) {
          Text(text = "CANCEL")
        }
      },
      modifier = Modifier.testTag("datePicker"),
  ) {
    DatePicker(
        state = state,
    )
  }
}

fun format(date: Long): String {

  val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

  val localDate = Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).toLocalDate()

  return localDate.format(formatter)
}
