package com.github.se.eventradar.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

/**
 * This class represents the Overview Screen and the elements it contains.
 *
 * It is used to interact with the UI elements during UI tests, incl. grading! You can adapt the
 * test tags if necessary to suit your own implementation, but the class properties need to stay the
 * same.
 *
 * You can refer to Figma for the naming conventions.
 * https://www.figma.com/file/PHSAMl7fCpqEkkSHGeAV92/TO-DO-APP-Mockup?type=design&node-id=435%3A3350&mode=design&t=GjYE8drHL1ACkQnD-1
 */
class OverviewScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<OverviewScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("overviewScreen") }) {

  val todoList: KNode = child { hasTestTag("todoList") }
  val createTodoButton: KNode = child { hasTestTag("createTodoButton") }
  val todoListItems: KNode = todoList.child { hasTestTag("todoListItem") }

  val searchTodo: KNode = child { hasTestTag("searchTodo") }
  val todoListSearch: KNode = searchTodo.child { hasSetTextAction() }
  val filteredToDoList: KNode = searchTodo.child { hasTestTag("filteredTodoList") }
  val filteredToDoListItem: KNode = filteredToDoList.child { hasTestTag("todoListItem") }

  val mapButton: KNode = onNode { hasTestTag("mapBottomNav") }
  val overviewButton: KNode = onNode { hasTestTag("overviewBottomNav") }
}
