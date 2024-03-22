package com.github.se.eventradar.ui.overview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.eventradar.model.OverviewUiState
import com.github.se.eventradar.model.OverviewViewModel
import com.github.se.eventradar.model.todo.ToDo
import com.github.se.eventradar.ui.BottomNavigationMenu
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.ui.navigation.Route
import com.github.se.eventradar.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.swent.bootcamp.R

@Composable
fun Overview(viewModel: OverviewViewModel = viewModel(), navigationActions: NavigationActions) {
  val uiState by viewModel.uiState.collectAsState()

  LaunchedEffect(key1 = uiState.toDoList) { viewModel.getToDos() }

  OverviewUI(
      { viewModel.onSearchQueryChanged(it) },
      uiState,
      navigationActions,
      { navigationActions.navController.navigate(Route.NEW_TASK) },
      {
        viewModel.onTaskClicked(it)
        navigationActions.navController.navigate("${Route.EDIT_TASK}/$it")
      })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewUI(
    onSearchQueryChanged: (String) -> Unit,
    uiState: OverviewUiState,
    navigationActions: NavigationActions,
    createNewTask: () -> Unit,
    editTask: (String) -> Unit,
) {
  var isActive by remember { mutableStateOf(false) }

  Scaffold(
      topBar = {
        DockedSearchBar(
            query = uiState.searchQuery,
            onQueryChange = {
              onSearchQueryChanged(it)
              isActive = it != ""
            },
            onSearch = onSearchQueryChanged,
            active = isActive,
            onActiveChange = {},
            leadingIcon = { Icon(Icons.Default.Menu, contentDescription = null) },
            trailingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier =
                Modifier.fillMaxWidth()
                    .heightIn(max = 328.dp)
                    .fillMaxHeight(if (isActive) 1f else .3f)
                    .padding(16.dp)
                    .testTag("searchTodo")) {
              TodoLazyColumn(
                  todos = uiState.toDoList.getFilteredTask,
                  editTask = editTask,
                  modifier = Modifier.testTag("filteredTodoList"))
            }
      },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelected = navigationActions::navigateTo,
            tabList = TOP_LEVEL_DESTINATIONS,
            selectedItem = TOP_LEVEL_DESTINATIONS[0],
            modifier = Modifier.testTag("bottomNavMenu"))
      },
      floatingActionButton = {
        FloatingActionButton(
            onClick = { createNewTask() }, modifier = Modifier.testTag("createTodoButton")) {
              Icon(
                  painter = painterResource(id = R.drawable.edit),
                  contentDescription = null,
                  modifier = Modifier.width(24.dp).height(24.dp))
            }
      },
      floatingActionButtonPosition = FabPosition.End,
      modifier = Modifier.testTag("overviewScreen")) {
        TodoLazyColumn(
            todos = uiState.toDoList.getAllTask,
            padding = it,
            editTask = editTask,
            modifier = Modifier.testTag("todoList"))
      }
}

@Composable
fun TodoLazyColumn(
    todos: List<ToDo>,
    modifier: Modifier = Modifier,
    padding: PaddingValues? = null,
    editTask: (String) -> Unit
) {
  LazyColumn(
      modifier = modifier.padding(padding ?: PaddingValues(0.dp)).padding(horizontal = 8.dp)) {
        items(todos) { task ->
          Row(
              modifier =
                  Modifier.fillMaxWidth()
                      .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp)
                      .clickable(onClick = { editTask(task.id) })
                      .testTag("todoListItem")) {
                Column(modifier = Modifier.fillMaxWidth(.7f)) {
                  Text(
                      text = task.dueDate.toString(),
                      style =
                          TextStyle(
                              fontSize = 12.sp,
                              lineHeight = 16.sp,
                              fontFamily = FontFamily.SansSerif,
                              fontWeight = FontWeight(500),
                              letterSpacing = 0.5.sp,
                          ))
                  Text(
                      text = task.title,
                      style =
                          TextStyle(
                              fontSize = 16.sp,
                              lineHeight = 24.sp,
                              fontFamily = FontFamily.SansSerif,
                              fontWeight = FontWeight(400),
                              letterSpacing = 0.5.sp,
                          ))
                  Text(
                      text = task.assigneeName,
                      style =
                          TextStyle(
                              fontSize = 14.sp,
                              lineHeight = 20.sp,
                              fontFamily = FontFamily.SansSerif,
                              fontWeight = FontWeight(400),
                              letterSpacing = 0.25.sp,
                          ))
                }
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()) {
                      Text(
                          text = task.status!!.statusName,
                          style =
                              TextStyle(
                                  fontSize = 11.sp,
                                  lineHeight = 16.sp,
                                  fontFamily = FontFamily.SansSerif,
                                  fontWeight = FontWeight(500),
                                  color = task.status.statusColor,
                                  textAlign = TextAlign.Right,
                                  letterSpacing = 0.5.sp,
                              ),
                      )
                      Icon(
                          painter = painterResource(id = R.drawable.arrow),
                          contentDescription = null,
                          modifier = Modifier.width(24.dp).height(24.dp))
                    }
                Spacer(modifier = Modifier.height(8.dp))
              }
          Divider(modifier = Modifier.padding(horizontal = 8.dp))
        }
      }
}
