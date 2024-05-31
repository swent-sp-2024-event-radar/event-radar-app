package com.github.se.eventradar.ui.hosting

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.github.se.eventradar.ExcludeFromJacocoGeneratedReport
import com.github.se.eventradar.model.event.EventCategory
import com.github.se.eventradar.model.repository.event.MockEventRepository
import com.github.se.eventradar.model.repository.location.MockLocationRepository
import com.github.se.eventradar.model.repository.user.MockUserRepository
import com.github.se.eventradar.ui.component.DateInputTextField
import com.github.se.eventradar.ui.component.DropdownInputTextField
import com.github.se.eventradar.ui.component.GoBackButton
import com.github.se.eventradar.ui.component.ImagePicker
import com.github.se.eventradar.ui.component.LocationDropDownMenu
import com.github.se.eventradar.ui.component.MultiSelectDropDownMenu
import com.github.se.eventradar.ui.component.StandardDialogBox
import com.github.se.eventradar.ui.component.StandardInputTextField
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.viewmodel.CreateEventViewModel

@Composable
fun CreateEventScreen(
    viewModel: CreateEventViewModel = hiltViewModel(),
    navigationActions: NavigationActions
) {
  val uiState by viewModel.uiState.collectAsState()
  Scaffold(
      modifier = Modifier.testTag("createEventScreen"),
      topBar = {
        Row(
            modifier = Modifier.padding(vertical = 32.dp).fillMaxWidth().testTag("topBar"),
            verticalAlignment = Alignment.CenterVertically) {
              GoBackButton(modifier = Modifier.testTag("goBackButton"), navigationActions::goBack)
              Text(
                  "Create Event",
                  modifier = Modifier.testTag("createEventText"),
                  fontSize = 22.sp,
                  letterSpacing = 0.36.sp,
                  fontFamily = FontFamily.Default,
                  color = MaterialTheme.colorScheme.onBackground)
            }
      },
      bottomBar = {}) {
        Column(
            modifier =
                Modifier.padding(it)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .testTag("createEventScreenColumn"),
            horizontalAlignment = Alignment.CenterHorizontally) {
              val imagePickerLauncher =
                  rememberLauncherForActivityResult(
                      contract = ActivityResultContracts.GetContent(),
                      onResult = { uri -> viewModel.onEventPhotoUriChanged(uri) })

              ImagePicker(
                  modifier =
                      Modifier.size(150.dp) // Adjust size as needed
                          .clip(RoundedCornerShape(15.dp))
                          .testTag("eventImagePicker")
                          .clickable { imagePickerLauncher.launch("image/*") },
                  uiState.eventPhotoUri)

              StandardInputTextField(
                  modifier =
                      Modifier.fillMaxWidth()
                          .padding(start = 35.dp, top = 8.dp, end = 35.dp)
                          .testTag("eventNameTextField"),
                  textFieldLabel = "Event Name",
                  textFieldValue = uiState.eventName,
                  onValueChange = viewModel::onEventNameChanged,
                  errorState = uiState.eventNameIsError,
              )
              StandardInputTextField(
                  modifier =
                      Modifier.fillMaxWidth()
                          .padding(start = 35.dp, top = 8.dp, end = 35.dp)
                          .height(128.dp)
                          .testTag("eventDescriptionTextField"),
                  textFieldLabel = "Event Description",
                  textFieldValue = uiState.eventDescription,
                  onValueChange = viewModel::onEventDescriptionChanged,
                  errorState = uiState.eventDescriptionIsError,
              )
              DropdownInputTextField(
                  modifier =
                      Modifier.fillMaxWidth()
                          .padding(start = 35.dp, top = 8.dp, end = 35.dp)
                          .testTag("eventCategoryDropDown"),
                  textFieldLabel = "Event Category",
                  textFieldValue = uiState.eventCategory,
                  onValueChange = viewModel::onEventCategoryChanged,
                  errorState = uiState.eventCategoryIsError,
                  options = EventCategory.entries.map { entry -> entry.toString() }.toList(),
                  toggleIconTestTag = "eventCategoryToggleIcon")
              Row(
                  horizontalArrangement = Arrangement.Center,
                  modifier =
                      Modifier.fillMaxWidth()
                          .padding(start = 35.dp, top = 8.dp, end = 35.dp)
                          .testTag("datesRow")) {
                    DateInputTextField(
                        modifier = Modifier.weight(1f).testTag("startDateTextField"),
                        textFieldLabel = "Start Date",
                        textFieldValue = uiState.startDate,
                        onValueChange = viewModel::onStartDateChanged,
                        errorState = uiState.startDateIsError)
                    Spacer(modifier = Modifier.width(8.dp))
                    DateInputTextField(
                        modifier = Modifier.weight(1f).testTag("endDateTextField"),
                        textFieldLabel = "End Date",
                        textFieldValue = uiState.endDate,
                        onValueChange = viewModel::onEndDateChanged,
                        errorState = uiState.endDateIsError)
                  }

              Row(
                  horizontalArrangement = Arrangement.Center,
                  modifier =
                      Modifier.fillMaxWidth()
                          .padding(start = 35.dp, top = 8.dp, end = 35.dp)
                          .testTag("timesRow")) {
                    StandardInputTextField(
                        modifier = Modifier.weight(1f).testTag("startTimeTextField"),
                        textFieldLabel = "Start Time",
                        textFieldValue = uiState.startTime,
                        placeHolderText = "HH:MM",
                        onValueChange = viewModel::onStartTimeChanged,
                        errorState = uiState.startTimeIsError)
                    Spacer(modifier = Modifier.width(8.dp))
                    StandardInputTextField(
                        modifier = Modifier.weight(1f).testTag("endTimeTextField"),
                        textFieldLabel = "End Time",
                        textFieldValue = uiState.endTime,
                        placeHolderText = "HH:MM",
                        onValueChange = viewModel::onEndTimeChanged,
                        errorState = uiState.endTimeIsError)
                  }
              LocationDropDownMenu(
                  value = uiState.location,
                  onLocationChanged = viewModel::onLocationChanged,
                  label = { Text("Location") },
                  modifier =
                      Modifier.fillMaxWidth()
                          .padding(start = 35.dp, top = 8.dp, end = 35.dp)
                          .testTag("locationDropDownMenuTextField"),
                  getLocations = viewModel::updateListOfLocations,
                  locationList = uiState.listOfLocations)

              DropdownInputTextField(
                  modifier =
                      Modifier.fillMaxWidth()
                          .padding(start = 35.dp, top = 8.dp, end = 35.dp)
                          .testTag("ticketNameDropDownMenuTextField"),
                  textFieldLabel = "Ticket Name",
                  textFieldValue = uiState.ticketName,
                  onValueChange = viewModel::onTicketNameChanged,
                  errorState = uiState.ticketNameIsError,
                  options = listOf("Standard", "VIP"),
                  toggleIconTestTag = "ticketNameToggleIcon") // temporary

              StandardInputTextField(
                  modifier =
                      Modifier.fillMaxWidth()
                          .padding(start = 35.dp, top = 8.dp, end = 35.dp)
                          .testTag("ticketQuantityTextField"),
                  textFieldLabel = "Ticket Quantity",
                  textFieldValue = uiState.ticketCapacity,
                  onValueChange = viewModel::onTicketCapacityChanged,
                  errorState = uiState.ticketCapacityIsError,
              )

              StandardInputTextField(
                  modifier =
                      Modifier.fillMaxWidth()
                          .padding(start = 35.dp, top = 8.dp, end = 35.dp)
                          .testTag("ticketPriceTextField"),
                  textFieldLabel = "Ticket Price",
                  textFieldValue = uiState.ticketPrice,
                  onValueChange = viewModel::onTicketPriceChanged,
                  errorState = uiState.ticketPriceIsError,
              )
              MultiSelectDropDownMenu(
                  value = "Organiser",
                  onSelectedListChanged = viewModel::onOrganiserListChanged,
                  label = { Text("Organisers") },
                  modifier =
                      Modifier.fillMaxWidth()
                          .padding(start = 35.dp, top = 8.dp, end = 35.dp)
                          .testTag("organisersMultiDropDownMenuTextField"),
                  getFriends = viewModel::getHostFriendList,
                  friendsList = uiState.hostFriendsList)
              // add a Button!
              // successful or not?
              Button(
                  onClick = {
                    if (viewModel.validateFields()) {
                      viewModel.addEvent()
                    }
                  },
                  modifier =
                      Modifier.wrapContentSize()
                          .fillMaxWidth()
                          .padding(start = 35.dp, top = 16.dp, end = 35.dp)
                          .testTag("publishEventButton"),
                  border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary),
                  colors =
                      ButtonDefaults.buttonColors(
                          containerColor = MaterialTheme.colorScheme.primary),
              ) {
                Text(
                    text = "Publish Event",
                    style =
                        TextStyle(
                            fontSize = 14.sp,
                            lineHeight = 17.sp,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight(500),
                            color = Color(0xFFFFFFFF),
                            letterSpacing = 0.25.sp,
                        ),
                    modifier = Modifier.padding(start = 8.dp),
                )
              }
              StandardDialogBox(
                  display = (uiState.showAddEventSuccess),
                  modifier = Modifier.testTag("successDialogBox"),
                  title = "Successfully Created Event",
                  message = "You have succesfully created your event, invite others over to join!",
                  onClickConfirmButton = {
                    // update the error states!
                    viewModel.resetStateAndSetAddEventSuccess(false)
                    navigationActions.goBack()
                  },
                  boxIcon = { Icon(Icons.Default.Check, "Success Icon") })
              StandardDialogBox(
                  display = (uiState.showAddEventFailure),
                  modifier = Modifier.testTag("failureDialogBox"),
                  title = "Creating Event has Failed",
                  message = "Something went wrong with creating your event, please retry.",
                  onClickConfirmButton = {
                    viewModel.resetStateAndSetAddEventFailure(false)
                    navigationActions.goBack()
                  }, // reset or not idk!
                  boxIcon = { Icon(Icons.Default.Clear, "Failure Icon") })
            }
      }
}

@Preview(showBackground = true)
@ExcludeFromJacocoGeneratedReport
@Composable
fun CreateEventScreenPreview() {
  val viewModel =
      CreateEventViewModel(MockLocationRepository(), MockEventRepository(), MockUserRepository())
  CreateEventScreen(
      viewModel = viewModel, navigationActions = NavigationActions(rememberNavController()))
}
