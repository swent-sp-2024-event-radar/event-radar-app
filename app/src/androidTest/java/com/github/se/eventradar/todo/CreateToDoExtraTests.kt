package com.github.se.eventradar.todo

// ***************************************************************************** //
// ***                                                                       *** //
// *** THIS FILE WILL BE OVERWRITTEN DURING GRADING. IT SHOULD BE LOCATED IN *** //
// *** `app/src/androidTest/java/com/github/se/bootcamp/todo/`.              *** //
// *** DO **NOT** IMPLEMENT YOUR OWN TESTS IN THIS FILE                      *** //
// ***                                                                       *** //
// ***************************************************************************** //

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.eventradar.model.ToDoViewModel
import com.github.se.eventradar.screens.CreateToDoScreen
import com.github.se.eventradar.ui.CreateToDo
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import io.mockk.confirmVerified
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateToDoExtraTests : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()

  // This rule automatic initializes lateinit properties with @MockK, @RelaxedMockK, etc.
  @get:Rule val mockkRule = MockKRule(this)

  // Relaxed mocks methods have a default implementation returning values
  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  @Before
  fun testSetup() {
    val vm = ToDoViewModel()
    composeTestRule.setContent { CreateToDo(vm, mockNavActions) }
  }

  @Test
  fun saveToDoWorks() = run {
    onComposeScreen<CreateToDoScreen>(composeTestRule) {
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
  fun allPlaceHoldersAreCorrect() {
    onComposeScreen<CreateToDoScreen>(composeTestRule) {
      inputTitle {
        assertIsDisplayed()
        assertTextContains("Title")
        performClick()
        assertTextContains("Name the task", substring = true)
      }

      inputDescription {
        assertIsDisplayed()
        assertTextContains("Description")
        performClick()
        assertTextContains("Describe the task", substring = true)
      }

      inputAssignee {
        assertIsDisplayed()
        assertTextContains("Assignee Name")
        performClick()
        assertTextContains("Assign a person", substring = true)
      }

      inputLocation {
        assertIsDisplayed()
        assertTextContains("Location")
        performClick()
        assertTextContains("Enter an address", substring = true)
      }

      inputDueDate {
        assertIsDisplayed()
        assertTextContains("Due Date", substring = true)
      }
    }
  }

  @Test
  fun dateDialogSubmitAndCancelButtonIsCorrect() {
    onComposeScreen<CreateToDoScreen>(composeTestRule) {
      inputDueDate {
        assertIsDisplayed()
        performClick()
      }

      datePickerSubmit {
        assertIsDisplayed()
        assertTextContains("Ok", ignoreCase = true)
      }

      datePickerCancel {
        assertIsDisplayed()
        assertTextContains("Cancel", ignoreCase = true)
      }
    }
  }

  @Test
  fun dateCancelButtonWorks() {
    onComposeScreen<CreateToDoScreen>(composeTestRule) {
      inputDueDate {
        assertIsDisplayed()
        assertTextContains("Due Date", substring = true)
        performClick()
      }

      datePickerCancel {
        assertIsDisplayed()
        performClick()
      }

      inputDueDate {
        assertIsDisplayed()
        assertTextContains("Due Date", substring = true)
      }
    }
  }

  @Test
  fun saveTodoDoesNotWorkWithEmptyDescription() {
    onComposeScreen<CreateToDoScreen>(composeTestRule) {
      inputTitle {
        assertIsDisplayed()
        performTextInput("Sample title")
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

      saveButton {
        assertIsDisplayed()
        performClick()
      }

      // verify that the nav action has not been called
      verify(exactly = 0) { mockNavActions.goBack() }
      confirmVerified(mockNavActions)
    }
  }

  @Test
  fun saveDoesNotWorkWithEmptyLocation() {
    onComposeScreen<CreateToDoScreen>(composeTestRule) {
      inputTitle {
        assertIsDisplayed()
        performTextInput("Sample title")
      }

      inputDescription {
        assertIsDisplayed()
        performTextInput("Sample description")
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

      saveButton {
        assertIsDisplayed()
        performClick()
      }

      // verify that the nav action has not been called
      verify(exactly = 0) { mockNavActions.goBack() }
      confirmVerified(mockNavActions)
    }
  }
}
