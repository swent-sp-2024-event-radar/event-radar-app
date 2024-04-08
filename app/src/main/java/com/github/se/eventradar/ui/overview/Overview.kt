package com.github.se.eventradar.ui.overview


import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.eventradar.model.OverviewUiState
import com.github.se.eventradar.model.OverviewViewModel
import com.github.se.eventradar.ui.navigation.NavigationActions

@Composable
fun Overview(viewModel: OverviewViewModel = viewModel(), navigationActions: NavigationActions) {
  val uiState by viewModel.uiState.collectAsState()

    // Temporarily commented out as we switch to events
    // LaunchedEffect(key1 = uiState.toDoList) { viewModel.getToDos() }

  OverviewUI(
      { viewModel.onSearchQueryChanged(it) },
      uiState,
      navigationActions)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewUI(
    onSearchQueryChanged: (String) -> Unit,
    uiState: OverviewUiState,
    navigationActions: NavigationActions
) {
    var isActive by remember { mutableStateOf(false) }
}
