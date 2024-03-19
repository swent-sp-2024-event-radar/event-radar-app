package com.github.se.partyradar.map

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.partyradar.model.location.Location
import com.github.se.partyradar.model.todo.ToDo
import com.github.se.partyradar.model.todo.ToDoList
import com.github.se.partyradar.model.todo.ToDoStatus
import com.github.se.partyradar.screens.MapScreen
import com.github.se.partyradar.ui.navigation.NavigationActions
import com.github.se.partyradar.ui.navigation.TOP_LEVEL_DESTINATIONS
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
class MapTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()

  // This rule automatic initializes lateinit properties with @MockK, @RelaxedMockK, etc.
  @get:Rule val mockkRule = MockKRule(this)

  // Relaxed mocks methods have a default implementation returning values
  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  @RelaxedMockK lateinit var mockMapViewModel: MapViewModel

  private val sampleTodoList =
      MutableStateFlow(
          MapUiState(
              toDoList =
                  ToDoList(
                      List(1) {
                        ToDo(
                            id = "0",
                            title = "Test Title",
                            assigneeName = "Test",
                            dueDate = LocalDate.of(2023, 1, 1),
                            location = Location(46.5188, 6.5593, "EPFL"),
                            description = "Test",
                            status = ToDoStatus.CREATED)
                      })))

  @Before
  fun testSetup() {
    every { mockMapViewModel.getToDos() } returns Unit

    every { mockMapViewModel.uiState } returns sampleTodoList
  }

  @Test
  fun mapShowsCorrectly() = run {
    composeTestRule.setContent { Map(mockMapViewModel, mockNavActions) }
    onComposeScreen<MapScreen>(composeTestRule) { map { assertIsDisplayed() } }
  }

  @Test
  fun mapButtonNavigatesOffScreen() = run {
    composeTestRule.setContent { Map(mockMapViewModel, mockNavActions) }
    onComposeScreen<MapScreen>(composeTestRule) {
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
    composeTestRule.setContent { Map(mockMapViewModel, mockNavActions) }
    onComposeScreen<MapScreen>(composeTestRule) {
      overviewButton {
        assertIsDisplayed()
        performClick()
      }

      verify { mockNavActions.navigateTo(TOP_LEVEL_DESTINATIONS[0]) }
      confirmVerified(mockNavActions)
    }
  }

  @Test
  fun mapGetsToDoListAtBoot() = run {
    sampleTodoList.value = sampleTodoList.value.copy(toDoList = ToDoList(emptyList()))

    every { mockMapViewModel.uiState } returns sampleTodoList

    composeTestRule.setContent { Map(mockMapViewModel, mockNavActions) }
    onComposeScreen<MapScreen>(composeTestRule) {
      verify { mockMapViewModel.getToDos() }
      verify { mockMapViewModel.uiState }
      confirmVerified(mockMapViewModel)
    }
  }
}
