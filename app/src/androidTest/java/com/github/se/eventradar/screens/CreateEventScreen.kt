package com.github.se.eventradar.screens

import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class CreateEventScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<CreateEventScreen>(
    semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("createEventScreen") }) {
    val topBar: KNode = child { hasTestTag("topBar") }
    val goBackButton: KNode = topBar.child { hasTestTag("goBackButton")}
    val createEventText: KNode = topBar.child{ hasTestTag("createEventText")}
    val createEventScreenColumn: KNode = child {hasTestTag("createEventScreenColumn")}
    val eventImagePicker : KNode = createEventScreenColumn.child {hasTestTag("eventImagePicker")}
    val eventNameTextField : KNode = createEventScreenColumn.child {hasTestTag("eventNameTextField")}
    val eventDescriptionTextField : KNode = createEventScreenColumn.child { hasTestTag("eventDescriptionTextField")}

    val datesRow : KNode = createEventScreenColumn.child{hasTestTag("datesRow")}
    val startDateTextField : KNode = datesRow.child { hasTestTag("startDateTextField") }
    val endDateTextField : KNode = datesRow.child { hasTestTag("endDateTextField") }

    val timesRow : KNode = createEventScreenColumn.child{hasTestTag("timesRow")}
    val startTimeTextField : KNode = timesRow.child { hasTestTag("startTimeTextField") }
    val endTimeTextField : KNode = timesRow.child { hasTestTag("endTimeTextField") }
    val locationExposedDropDownMenuBox :KNode = createEventScreenColumn.child{hasTestTag("locationExposedDropDownMenuBox")}
    val locationDropDownMenuTextField : KNode = locationExposedDropDownMenuBox.child{hasTestTag("locationDropDownMenuTextField")}

    val exposedDropDownMenuBox : KNode = createEventScreenColumn.child{hasTestTag("exposedDropDownMenuBox")}
    val ticketNameDropDownMenu : KNode = exposedDropDownMenuBox.child{hasTestTag("ticketNameDropDownMenu")}
    val eventCategoryDropDown : KNode = exposedDropDownMenuBox.child { hasTestTag("eventCategoryDropDown")}

    val ticketQuantityTextField : KNode = createEventScreenColumn.child{hasTestTag("ticketQuantityTextField")}
    val ticketPriceTextField : KNode = createEventScreenColumn.child{hasTestTag("ticketPriceTextField")}

    val multiSelectExposedDropDownMenuBox :KNode = createEventScreenColumn.child{hasTestTag("multiSelectExposedDropDownMenuBox")}
    val organisersMultiDropDownMenuTextField : KNode = multiSelectExposedDropDownMenuBox.child{hasTestTag("organisersMultiDropDownMenuTextField")}
    val publishEventButton : KNode = createEventScreenColumn.child{hasTestTag("publishEventButton")}

    val successDialogBox : KNode = createEventScreenColumn.child{hasTestTag("successDialogBox")}
    val successDisplayText : KNode = successDialogBox.child { hasTestTag("DisplayText") }
    val successDisplayTitle : KNode = successDialogBox.child { hasTestTag("DisplayTitle") }
    val successDialogConfirmButton : KNode = successDialogBox.child { hasTestTag("dialogConfirmButton") }
    val failureDialogBox : KNode = createEventScreenColumn.child{hasTestTag("failureDialogBox")}
    val failureDisplayText : KNode = successDialogBox.child { hasTestTag("DisplayText") }
    val failureDisplayTitle : KNode = successDialogBox.child { hasTestTag("DisplayTitle") }
    val failureDialogConfirmButton : KNode = successDialogBox.child { hasTestTag("dialogConfirmButton") }
}