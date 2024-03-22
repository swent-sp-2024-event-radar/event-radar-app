package com.github.se.eventradar.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.github.se.eventradar.model.location.Location
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.swent.bootcamp.R

@Composable
fun CreateToDo(viewModel: ToDoViewModel = viewModel(), navigationActions: NavigationActions) {
  CreateToDoUI(
      uiState = viewModel.uiState.collectAsState().value,
      onTitleChanged = viewModel::onTitleChanged,
      onAssigneeNameChanged = viewModel::onAssigneeNameChanged,
      onDueDateChanged = viewModel::onDueDateChanged,
      onLocationChanged = { lat, long, name -> viewModel.onLocationChanged(lat, long, name) },
      onDescriptionChanged = viewModel::onDescriptionChanged,
      addToDo = {
        viewModel.submitNewToDo()
        navigationActions.goBack()
      },
      goBack = navigationActions::goBack,
      getLocations = viewModel::getLongAndLad,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateToDoUI(
    uiState: ToDoUiState,
    onTitleChanged: (String) -> Unit,
    onAssigneeNameChanged: (String) -> Unit,
    onDueDateChanged: (String) -> Unit,
    onLocationChanged: (Double, Double, String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    addToDo: () -> Unit,
    goBack: () -> Unit,
    getLocations: (String) -> Unit,
) {
  Scaffold(
      topBar = {
        TopAppBar(
            title = {
              Text(
                  text = "Create a new task",
                  style =
                      TextStyle(
                          fontSize = 24.sp,
                          lineHeight = 32.sp,
                          fontFamily = FontFamily.SansSerif,
                          fontWeight = FontWeight(400),
                      ),
                  modifier = Modifier.testTag("createTodoTitle"))
            },
            navigationIcon = {
              Icon(
                  painter = painterResource(id = R.drawable.back_arrow),
                  contentDescription = null,
                  modifier =
                      Modifier.width(24.dp)
                          .height(24.dp)
                          .clickable { goBack() }
                          .testTag("goBackButton"))
            })
      },
      modifier = Modifier.testTag("createScreen")) {
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
                    placeholder = {
                      Text(text = stringResource(id = R.string.location_placeholder))
                    },
                    singleLine = true,
                    getLocations = getLocations,
                    locationList = uiState.locationList,
                    modifier = Modifier.testTag("inputTodoLocation"))
                DateTextField(
                    value = uiState.dueDate,
                    onValueChange = onDueDateChanged,
                    label = { Text(text = stringResource(id = R.string.due_date)) },
                    modifier = Modifier.testTag("inputTodoDate"))
                TextButton(
                    onClick = addToDo,
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(top = 8.dp, start = 28.dp, end = 28.dp)
                            .testTag("todoSave"),
                    colors =
                        ButtonDefaults.textButtonColors(
                            containerColor = Color(0xFF00668A),
                        ),
                    enabled =
                        isEnabled(
                            uiState.title,
                            uiState.assigneeName,
                            uiState.dueDate,
                            uiState.location.name,
                            uiState.description),
                ) {
                  Text(
                      text = stringResource(id = R.string.save),
                  )
                }
              }
            }
      }
}

@Composable
fun TaskTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable () -> Unit,
    placeholder: @Composable (() -> Unit),
    modifier: Modifier = Modifier,
    singleLine: Boolean = false,
) {
  TextField(
      value = value,
      onValueChange = onValueChange,
      label = label,
      placeholder = placeholder,
      singleLine = singleLine,
      modifier =
          modifier
              .padding(vertical = 16.dp)
              .border(width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(8.dp))
              .fillMaxWidth(),
      colors =
          TextFieldDefaults.colors(
              focusedContainerColor = Color.Transparent,
              unfocusedContainerColor = Color.Transparent,
              focusedIndicatorColor = Color.Transparent,
              unfocusedIndicatorColor = Color.Transparent,
              focusedLabelColor = Color(0xFF00668A),
          ))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationTextField(
    value: String,
    onLocationChanged: (Double, Double, String) -> Unit,
    label: @Composable () -> Unit,
    placeholder: @Composable (() -> Unit),
    modifier: Modifier = Modifier,
    singleLine: Boolean = false,
    getLocations: (String) -> Unit,
    locationList: List<Location>,
) {
  var locations by remember { mutableStateOf(emptyList<Location>()) }
  var showLocations by remember { mutableStateOf(false) }

  ExposedDropdownMenuBox(
      expanded = showLocations,
      onExpandedChange = { showLocations = !showLocations },
  ) {
    TaskTextField(
        value = value,
        onValueChange = {
          onLocationChanged(0.0, 0.0, it)
          getLocations(it)
          showLocations = true
        },
        label = label,
        placeholder = placeholder,
        singleLine = singleLine,
        modifier = modifier.menuAnchor(),
    )

    LaunchedEffect(key1 = locationList) { locations = locationList }

    if (showLocations) {
      ExposedDropdownMenu(
          expanded = showLocations,
          onDismissRequest = { showLocations = false },
      ) {
        for (location in locations) {
          DropdownMenuItem(
              text = {
                Text(
                    text = location.name,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 4.dp))
              },
              onClick = {
                onLocationChanged(location.latitude, location.longitude, location.name)
                showLocations = false
              },
              modifier = Modifier.testTag("locationDropdownItem"))
        }
      }
    }
  }
}
