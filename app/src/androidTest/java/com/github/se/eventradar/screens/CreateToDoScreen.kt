package com.github.se.eventradar.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

/**
 * This class represents the CreateToDo Screen and the elements it contains.
 *
 * It is used to interact with the UI elements during UI tests, incl. grading! You can adapt the
 * test tags if necessary to suit your own implementation, but the class properties need to stay the
 * same.
 *
 * You can refer to Figma for the naming conventions.
 * https://www.figma.com/file/PHSAMl7fCpqEkkSHGeAV92/TO-DO-APP-Mockup?type=design&node-id=435%3A3350&mode=design&t=GjYE8drHL1ACkQnD-1
 */
class CreateToDoScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<CreateToDoScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("createScreen") }) {

  val screenTitle: KNode = onNode { hasTestTag("createTodoTitle") }

  val goBackButton: KNode = onNode { hasTestTag("goBackButton") }
  val saveButton: KNode = onNode { hasTestTag("todoSave") }

  val inputTitle: KNode = onNode { hasTestTag("inputTodoTitle") }
  val inputDescription: KNode = onNode { hasTestTag("inputTodoDescription") }
  val inputAssignee: KNode = onNode { hasTestTag("inputTodoAssignee") }
  val inputDueDate: KNode = onNode { hasTestTag("inputTodoDate") }

  val inputLocation = onNode { hasTestTag("inputTodoLocation") }
  private val inputLocationDropdown: KNode = inputLocation.child { hasScrollAction() }
  val inputLocationText: KNode = inputLocation.child { hasText("Enter an address") }
  val inputLocationProposal: KNode =
      inputLocationDropdown.child { hasTestTag("locationDropdownItem") }

  val datePicker: KNode = onNode { hasTestTag("datePicker") }
  val datePickerSubmit: KNode = onNode { hasTestTag("datePickerSubmit") }
  val datePickerCancel: KNode = onNode { hasTestTag("datePickerCancel") }
}
