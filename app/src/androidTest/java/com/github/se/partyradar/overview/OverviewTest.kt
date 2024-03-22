package com.github.se.partyradar.overview

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.partyradar.model.OverviewUiState
import com.github.se.partyradar.model.OverviewViewModel
import com.github.se.partyradar.model.location.Location
import com.github.se.partyradar.model.todo.ToDo
import com.github.se.partyradar.model.todo.ToDoList
import com.github.se.partyradar.model.todo.ToDoStatus
import com.github.se.partyradar.screens.OverviewScreen
import com.github.se.partyradar.ui.navigation.NavigationActions
import com.github.se.partyradar.ui.navigation.Route
import com.github.se.partyradar.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.partyradar.ui.overview.Overview
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OverviewTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()

  // This rule automatic initializes lateinit properties with @MockK, @RelaxedMockK, etc.
  @get:Rule val mockkRule = MockKRule(this)

  // Relaxed mocks methods have a default implementation returning values
  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  @RelaxedMockK lateinit var mockOverviewViewModel: OverviewViewModel

  private val sampleTodoList =
      MutableStateFlow(
          OverviewUiState(
              toDoList =
                  ToDoList(
                      List(20) {
                        ToDo(
                            id = "$it",
                            title = "Test $it",
                            assigneeName = "Test",
                            dueDate = LocalDate.of(2023, 1, 1),
                            location = Location(0.0, 0.0, "Test"),
                            description = "Test",
                            status = ToDoStatus.CREATED)
                      })))

  @Before
  fun testSetup() {
    every { mockOverviewViewModel.getToDos() } returns Unit

    every { mockOverviewViewModel.onSearchQueryChanged(any()) } answers
        {
          sampleTodoList.value =
              sampleTodoList.value.copy(
                  searchQuery = firstArg(),
                  toDoList =
                      sampleTodoList.value.toDoList.copy(
                          getFilteredTask =
                              sampleTodoList.value.toDoList.getAllTask.filter {
                                it.title.contains(firstArg() as String, ignoreCase = true)
                              }))
        }

    every { mockOverviewViewModel.onTaskClicked(any()) } answers
        {
          mockNavActions.navController.navigate("${Route.EDIT_TASK}/${firstArg() as String}")
        }

    every { mockOverviewViewModel.uiState } returns sampleTodoList

    composeTestRule.setContent { Overview(mockOverviewViewModel, mockNavActions) }
  }

  @Test
  fun clickingTaskNavigatesOffScreen() = run {
    onComposeScreen<OverviewScreen>(composeTestRule) {
      todoListItems {
        assertIsDisplayed()
        performScrollTo()
        performClick()
      }

      verify { mockNavActions.navController.navigate("${Route.EDIT_TASK}/0") }
      confirmVerified(mockNavActions)
    }
  }

  @Test
  fun clickingCreateTaskNavigatesOffScreen() = run {
    onComposeScreen<OverviewScreen>(composeTestRule) {
      createTodoButton {
        assertIsDisplayed()
        performClick()
      }

      verify { mockNavActions.navController.navigate(Route.NEW_TASK) }
      confirmVerified(mockNavActions)
    }
  }

  @Test
  fun typingInSearchBarShowsResults() = run {
    onComposeScreen<OverviewScreen>(composeTestRule) {
      searchTodo {
        assertIsDisplayed()
        performClick()
      }

      todoListSearch {
        assertIsDisplayed()
        performTextInput("Test 0")
      }

      filteredToDoListItem {
        assertIsDisplayed()
        performClick()
      }

      verify { mockOverviewViewModel.onSearchQueryChanged("Test 0") }
      verify { mockOverviewViewModel.uiState }
      verify { mockOverviewViewModel.getToDos() }
      verify { mockOverviewViewModel.onTaskClicked("0") }
      verify { mockNavActions.navController.navigate("${Route.EDIT_TASK}/0") }
      confirmVerified(mockNavActions)
      confirmVerified(mockOverviewViewModel)
    }
  }

  @Test
  fun mapButtonNavigatesOffScreen() = run {
    onComposeScreen<OverviewScreen>(composeTestRule) {
      mapButton {
        assertIsDisplayed()
        performClick()
      }

      verify { mockNavActions.navigateTo(TOP_LEVEL_DESTINATIONS[1]) }
      confirmVerified(mockNavActions)
    }
  }

  @Test
  fun overviewButtonNavigatesToSameScreen() = run {
    onComposeScreen<OverviewScreen>(composeTestRule) {
      overviewButton {
        assertIsDisplayed()
        performClick()
      }

      verify { mockNavActions.navigateTo(TOP_LEVEL_DESTINATIONS[0]) }
      confirmVerified(mockNavActions)
    }
  }
}
