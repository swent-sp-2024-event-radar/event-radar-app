package com.github.se.partyradar.todo

// ***************************************************************************** //
// ***                                                                       *** //
// *** THIS FILE WILL BE OVERWRITTEN DURING GRADING. IT SHOULD BE LOCATED IN *** //
// *** `app/src/androidTest/java/com/github/se/bootcamp/todo/`.              *** //
// *** DO **NOT** IMPLEMENT YOUR OWN TESTS IN THIS FILE                      *** //
// ***                                                                       *** //
// ***************************************************************************** //

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.partyradar.model.ToDoUiState
import com.github.se.partyradar.model.ToDoViewModel
import com.github.se.partyradar.model.location.Location
import com.github.se.partyradar.model.todo.ToDoStatus
import com.github.se.partyradar.screens.EditToDoScreen
import com.github.se.partyradar.ui.EditToDo
import com.github.se.partyradar.ui.navigation.NavigationActions
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import io.mockk.Called
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EditToDoTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()

  // This rule automatic initializes lateinit properties with @MockK, @RelaxedMockK, etc.
  @get:Rule val mockkRule = MockKRule(this)

  // Relaxed mocks methods have a default implementation returning values
  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  @RelaxedMockK lateinit var mockToDoViewModel: ToDoViewModel

  private val sampleTodo =
      MutableStateFlow(
          ToDoUiState(
              title = "Test Title",
              description = "Test Description",
              assigneeName = "Test Assignee",
              dueDate = "Test Due Date",
              status = ToDoStatus.CREATED,
              location = Location(0.0, 0.0, "Test Location")))

  @Before
  fun testSetup() {
    every { mockToDoViewModel.getTodo() } returns Unit

    every { mockToDoViewModel.onTitleChanged(any()) } answers
        {
          sampleTodo.value = sampleTodo.value.copy(title = firstArg())
        }
    every { mockToDoViewModel.onDescriptionChanged(any()) } answers
        {
          sampleTodo.value = sampleTodo.value.copy(description = firstArg())
        }
    every { mockToDoViewModel.onAssigneeNameChanged(any()) } answers
        {
          sampleTodo.value = sampleTodo.value.copy(assigneeName = firstArg())
        }
    every { mockToDoViewModel.onDueDateChanged(any()) } answers
        {
          sampleTodo.value = sampleTodo.value.copy(dueDate = firstArg())
        }
    every { mockToDoViewModel.onLocationChanged(any(), any(), any()) } answers
        {
          sampleTodo.value =
              sampleTodo.value.copy(location = sampleTodo.value.location.copy(name = thirdArg()))
        }
    every { mockToDoViewModel.onStatusChanged(any()) } answers
        {
          sampleTodo.value = sampleTodo.value.copy(status = firstArg())
        }
    every { mockToDoViewModel.uiState } returns sampleTodo

    composeTestRule.setContent { EditToDo("", mockToDoViewModel, mockNavActions) }
  }

  @Test
  fun goBackButtonTriggersBackNavigation() = run {
    onComposeScreen<EditToDoScreen>(composeTestRule) {
      goBackButton {
        // arrange: verify the pre-conditions
        assertIsDisplayed()
        assertIsEnabled()

        // act: go back !
        performClick()
      }
    }

    // assert: the nav action has been called
    verify { mockNavActions.goBack() }
    confirmVerified(mockNavActions)
  }

  @Test
  fun saveToDoDoesNotWorkWithEmptyTitle() = run {
    onComposeScreen<EditToDoScreen>(composeTestRule) {
      step("Open todo screen") {
        inputTitle {
          assertIsDisplayed()
          performTextClearance()
        }

        saveButton {
          assertIsDisplayed()
          performClick()
        }

        // verify that the nav action has not been called
        verify { mockNavActions wasNot Called }
        confirmVerified(mockNavActions)
      }
    }
  }

  @Test
  fun saveToDoStatusWorks() = run {
    onComposeScreen<EditToDoScreen>(composeTestRule) {
      step("Open todo screen") {
        todoStatus {
          assertIsDisplayed()
          performClick()
        }

        todoStatusEnded {
          assertIsDisplayed()
          performClick()
        }

        todoStatusSubmit {
          assertIsDisplayed()
          performClick()
        }

        // verify that the nav action has been called
        verify { mockToDoViewModel.onStatusChanged(ToDoStatus.ENDED) }
        verify { mockToDoViewModel.haveFetchedTodo }
        verify { mockToDoViewModel.getTodo(null) }
        verify { mockToDoViewModel.uiState }

        confirmVerified(mockToDoViewModel)
      }
    }
  }

  @Test
  fun saveToDoWorks() = run {
    onComposeScreen<EditToDoScreen>(composeTestRule) {
      step("Open todo screen") {
        inputTitle {
          assertIsDisplayed()
          performTextInput("Sample title")
        }

        inputDescription {
          assertIsDisplayed()
          performTextInput("Sample description")
        }

        inputAssignee {
          assertIsDisplayed()
          performTextInput("Sample assignee")
        }

        inputLocation {
          assertIsDisplayed()
          performTextInput("Sample location")
        }

        inputDueDate {
          assertIsDisplayed()
          performClick()
        }

        datePicker {
          assertIsDisplayed()
          performClick()
        }

        datePickerSubmit {
          assertIsDisplayed()
          performClick()
        }

        todoStatus {
          assertIsDisplayed()
          performClick()
        }

        todoStatusEnded {
          assertIsDisplayed()
          performClick()
        }

        todoStatusSubmit {
          assertIsDisplayed()
          performClick()
        }

        saveButton {
          assertIsDisplayed()
          performClick()
        }

        // verify that the nav action has been called
        verify { mockNavActions.goBack() }
        confirmVerified(mockNavActions)
      }
    }
  }

  @Test
  fun deleteWorks() = run {
    onComposeScreen<EditToDoScreen>(composeTestRule) {
      step("Open todo screen") {
        deleteButton {
          assertIsDisplayed()
          performClick()
        }

        // verify that the nav action has been called
        verify { mockNavActions.goBack() }
        confirmVerified(mockNavActions)
      }
    }
  }
}
