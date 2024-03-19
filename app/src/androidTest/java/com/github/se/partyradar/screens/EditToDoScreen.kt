package com.github.se.partyradar.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

/**
 * This class represents the EditToDo Screen and the elements it contains.
 *
 * It is used to interact with the UI elements during UI tests, incl. grading! You can adapt the
 * test tags if necessary to suit your own implementation, but the class properties need to stay the
 * same.
 *
 * You can refer to Figma for the naming conventions.
 * https://www.figma.com/file/PHSAMl7fCpqEkkSHGeAV92/TO-DO-APP-Mockup?type=design&node-id=435%3A3350&mode=design&t=GjYE8drHL1ACkQnD-1
 */
class EditToDoScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<EditToDoScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("editScreen") }) {

  val screenTitle: KNode = onNode { hasTestTag("editTodoTitle") }

  val goBackButton: KNode = onNode { hasTestTag("goBackButton") }
  val saveButton: KNode = onNode { hasTestTag("todoSave") }
  val deleteButton: KNode = onNode { hasTestTag("todoDelete") }

  val inputTitle: KNode = onNode { hasTestTag("inputTodoTitle") }
  val inputDescription: KNode = onNode { hasTestTag("inputTodoDescription") }
  val inputAssignee: KNode = onNode { hasTestTag("inputTodoAssignee") }
  val inputDueDate: KNode = onNode { hasTestTag("inputTodoDate") }
  val todoStatus: KNode = onNode { hasTestTag("todoStatus") }

  val inputLocation = onNode { hasTestTag("inputTodoLocation") }
  private val inputLocationDropdown: KNode = inputLocation.child { hasScrollAction() }
  val inputLocationText: KNode = inputLocation.child { hasText("Enter an address") }
  val inputLocationProposal: KNode = inputLocationDropdown.child { hasClickAction() }

  val todoStatusEnded: KNode = onNode { hasTestTag("todoStatusEnded") }
  val todoStatusSubmit: KNode = onNode { hasTestTag("todoStatusSubmit") }
  val todoStatusText: KNode = onNode { hasTestTag("todoStatusText") }

  val datePicker: KNode = onNode { hasTestTag("datePicker") }
  val datePickerSubmit: KNode = onNode { hasTestTag("datePickerSubmit") }
}
