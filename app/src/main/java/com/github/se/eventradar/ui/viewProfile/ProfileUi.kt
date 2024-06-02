package com.github.se.eventradar.ui.viewProfile

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.github.se.eventradar.ExcludeFromJacocoGeneratedReport
import com.github.se.eventradar.R
import com.github.se.eventradar.model.User
import com.github.se.eventradar.model.repository.user.MockUserRepository
import com.github.se.eventradar.ui.BottomNavigationMenu
import com.github.se.eventradar.ui.component.GoBackButton
import com.github.se.eventradar.ui.component.Logo
import com.github.se.eventradar.ui.component.NameText
import com.github.se.eventradar.ui.component.StandardProfileInformationText
import com.github.se.eventradar.ui.component.UserProfileImage
import com.github.se.eventradar.ui.login.ErrorDialogBox
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.ui.navigation.Route
import com.github.se.eventradar.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.eventradar.ui.navigation.getTopLevelDestination
import com.github.se.eventradar.viewmodel.CountryCode
import com.github.se.eventradar.viewmodel.ProfileViewModel
import kotlinx.coroutines.runBlocking

@Composable
fun ProfileUi(
    isPublicView: Boolean,
    viewModel: ProfileViewModel,
    navigationActions: NavigationActions
) {
  val openErrorDialog = remember { mutableStateOf(false) }
  val isEditMode = remember { mutableStateOf(false) }

  ErrorDialogBox(openErrorDialog, modifier = Modifier.testTag("signUpErrorDialog"))

  val imagePickerLauncher =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.GetContent(),
          onResult = { uri -> viewModel.onSelectedImageUriChanged(uri!!) })

  viewModel.getProfileDetails()
  val uiState by viewModel.uiState.collectAsState()
  val context = LocalContext.current
  val focusManager = LocalFocusManager.current
  var isDropdownExpanded by remember { mutableStateOf(false) }
  Scaffold(
      modifier = Modifier.testTag("profileScreen"),
      topBar = {
        // row and text profile Information
        Row(
            modifier = Modifier.padding(vertical = 32.dp).fillMaxWidth().testTag("topBar"),
            verticalAlignment = Alignment.CenterVertically) {
              if (isPublicView) {
                GoBackButton(modifier = Modifier.testTag("goBackButton")) {
                  navigationActions.goBack()
                }
                Text(
                    "Profile Information",
                    modifier = Modifier.testTag("username"),
                    fontSize = 22.sp,
                    letterSpacing = 0.36.sp,
                    fontFamily = FontFamily.Default,
                    color = MaterialTheme.colorScheme.onBackground)
              } else {
                if (isEditMode.value) {
                  GoBackButton(modifier = Modifier.testTag("goBackButton")) {
                    isEditMode.value = false

                    Toast.makeText(context, "Changes have not been saved", Toast.LENGTH_SHORT)
                        .show()
                  }
                  Text(
                      "Edit Profile",
                      modifier = Modifier.testTag("editProfile"),
                      fontSize = 22.sp,
                      letterSpacing = 0.36.sp,
                      fontFamily = FontFamily.Default,
                      color = MaterialTheme.colorScheme.onBackground)
                } else {
                  // Display the Event Radar logo when the view is private and not in edit mode
                  Logo(
                      modifier =
                          Modifier.fillMaxWidth()
                              .padding(start = 16.dp, end = 16.dp)
                              .testTag("logo"))
                }
              }
            }
      },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelected = navigationActions::navigateTo,
            tabList = TOP_LEVEL_DESTINATIONS,
            selectedItem =
                if (isPublicView) getTopLevelDestination(Route.MESSAGE)
                else getTopLevelDestination(Route.PROFILE),
            modifier = Modifier.testTag("bottomNavMenu"))
      },
      floatingActionButton = {
        if (isPublicView) {
          FloatingActionButton(
              onClick = {
                navigationActions.navController.navigate(
                    Route.PRIVATE_CHAT + "/${viewModel.userId}")
              },
              modifier = Modifier.padding(bottom = 16.dp, end = 16.dp).testTag("chatButton"),
              containerColor = MaterialTheme.colorScheme.primaryContainer,
          ) {
            Icon(
                painter = painterResource(id = R.drawable.chat_bubble),
                contentDescription = "Chat Button",
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
          }
        } else if (!isEditMode.value) {
          // Replace the chat button with an edit button
          FloatingActionButton(
              onClick = { isEditMode.value = !isEditMode.value },
              modifier = Modifier.padding(bottom = 16.dp, end = 16.dp).testTag("editButton"),
              containerColor = MaterialTheme.colorScheme.primaryContainer,
          ) {
            Icon(
                painter = painterResource(id = R.drawable.edit),
                contentDescription = "Edit Button",
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
          }
        }
      },
      content = {
        Column(
            modifier =
                Modifier.padding(it)
                    .padding(horizontal = 41.dp, vertical = 20.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .testTag("centeredViewProfileColumn"),
            verticalArrangement = Arrangement.Center, // Vertically center the content
            horizontalAlignment =
                Alignment.CenterHorizontally) { // Horizontally center the content)
              UserProfileImage(
                  uiState.profilePicUri.toString(),
                  uiState.firstName,
                  Modifier.size(150.dp)
                      .testTag("profilePic")
                      .padding(10.dp)
                      .clip(RoundedCornerShape(10.dp))
                      .clickable(enabled = isEditMode.value) {
                        imagePickerLauncher.launch("image/*")
                      })
              if (isEditMode.value) {
                // Display text fields for editing the user's first and last name on the same row
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().testTag("nameRow")) {
                      OutlinedTextField(
                          value = uiState.firstName,
                          onValueChange = viewModel::onFirstNameChanged,
                          modifier = Modifier.weight(1f).testTag("firstNameTextField"),
                          label = { Text("First Name") },
                          singleLine = true,
                          isError = uiState.firstNameIsError,
                          colors =
                              OutlinedTextFieldDefaults.colors()
                                  .copy(
                                      focusedPrefixColor = MaterialTheme.colorScheme.primary,
                                      unfocusedLabelColor = MaterialTheme.colorScheme.primary,
                                  ),
                          keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                          keyboardActions =
                              KeyboardActions(
                                  onNext = { focusManager.moveFocus(FocusDirection.Next) }))
                      Spacer(modifier = Modifier.width(10.dp).testTag("nameSpacer"))
                      OutlinedTextField(
                          value = uiState.lastName,
                          onValueChange = viewModel::onLastNameChanged,
                          modifier = Modifier.weight(1f).testTag("lastNameTextField"),
                          label = { Text("Last Name") },
                          singleLine = true,
                          isError = uiState.lastNameIsError,
                          colors =
                              OutlinedTextFieldDefaults.colors()
                                  .copy(
                                      focusedPrefixColor = MaterialTheme.colorScheme.primary,
                                      unfocusedLabelColor = MaterialTheme.colorScheme.primary,
                                  ),
                          keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                          keyboardActions =
                              KeyboardActions(
                                  onNext = { focusManager.moveFocus(FocusDirection.Next) }))
                    }
                OutlinedTextField(
                    value = uiState.username,
                    onValueChange = viewModel::onUsernameChanged,
                    modifier =
                        Modifier.padding(top = 10.dp).fillMaxWidth().testTag("usernameTextField"),
                    label = { Text("Username") },
                    singleLine = true,
                    isError = uiState.userNameIsError,
                    colors =
                        OutlinedTextFieldDefaults.colors()
                            .copy(
                                focusedPrefixColor = MaterialTheme.colorScheme.primary,
                                unfocusedLabelColor = MaterialTheme.colorScheme.primary,
                            ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions =
                        KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Next) }),
                    supportingText = { Text("Your username must be unique") })
                Column(modifier = Modifier.fillMaxWidth().testTag("leftAlignedViewProfileColumn")) {
                  OutlinedTextField(
                      value = uiState.bio,
                      onValueChange = viewModel::onBioChanged,
                      modifier =
                          Modifier.fillMaxWidth()
                              .padding(top = 10.dp)
                              .imePadding()
                              .testTag("bioTextField"),
                      label = { Text("Bio") },
                      isError = uiState.bioIsError,
                      colors =
                          OutlinedTextFieldDefaults.colors()
                              .copy(
                                  focusedPrefixColor = MaterialTheme.colorScheme.primary,
                                  unfocusedLabelColor = MaterialTheme.colorScheme.primary,
                              ),
                      keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                      keyboardActions =
                          KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Next) }))
                  Row(
                      horizontalArrangement = Arrangement.SpaceBetween,
                      modifier = Modifier.padding(top = 10.dp).testTag("phoneNumberBirthDateRow")) {
                        OutlinedTextField(
                            value = uiState.phoneNumber,
                            onValueChange = viewModel::onPhoneNumberChanged,
                            modifier = Modifier.weight(1f).testTag("phoneNumberTextField"),
                            label = { Text("Phone Number") },
                            singleLine = true,
                            leadingIcon = {
                              Box {
                                TextButton(onClick = { isDropdownExpanded = true }) {
                                  Text(
                                      uiState.selectedCountryCode.ext,
                                      color = MaterialTheme.colorScheme.primary)
                                }
                                DropdownMenu(
                                    expanded = isDropdownExpanded,
                                    onDismissRequest = { isDropdownExpanded = false }) {
                                      val countryCodes = enumValues<CountryCode>()
                                      countryCodes.forEach { code ->
                                        DropdownMenuItem(
                                            onClick = {
                                              viewModel.onCountryCodeChanged(code)
                                              isDropdownExpanded = false
                                            },
                                            text = { Text(code.ext) })
                                      }
                                    }
                              }
                            },
                            isError = uiState.phoneNumberIsError,
                            colors =
                                OutlinedTextFieldDefaults.colors()
                                    .copy(
                                        focusedPrefixColor = MaterialTheme.colorScheme.primary,
                                        unfocusedLabelColor = MaterialTheme.colorScheme.primary,
                                    ),
                            keyboardOptions =
                                KeyboardOptions(
                                    imeAction = ImeAction.Next, keyboardType = KeyboardType.Number),
                            keyboardActions =
                                KeyboardActions(
                                    onNext = { focusManager.moveFocus(FocusDirection.Next) }))
                        Spacer(
                            modifier = Modifier.width(10.dp).testTag("phoneNumberBirthDateSpacer"))
                        OutlinedTextField(
                            value = uiState.birthDate,
                            onValueChange = viewModel::onBirthDateChanged,
                            modifier = Modifier.weight(1f).testTag("birthDateTextField"),
                            label = { Text("Birth Date (DD.MM.YYYY)") },
                            singleLine = true,
                            isError = uiState.birthDateIsError,
                            colors =
                                OutlinedTextFieldDefaults.colors()
                                    .copy(
                                        focusedPrefixColor = MaterialTheme.colorScheme.primary,
                                        unfocusedLabelColor = MaterialTheme.colorScheme.primary,
                                    ),
                            keyboardOptions =
                                KeyboardOptions(
                                    imeAction = ImeAction.Next, keyboardType = KeyboardType.Number),
                            keyboardActions =
                                KeyboardActions(
                                    onNext = { focusManager.moveFocus(FocusDirection.Next) }))
                      }
                }

                Button(
                    onClick = {
                      if (viewModel.validateFields()) {
                        viewModel.updateUserInfo()
                        isEditMode.value = false
                      }
                    },
                    modifier = Modifier.padding(top = 10.dp).testTag("saveButton")) {
                      Text("Save")
                    }
              } else {
                NameText(
                    name = "${uiState.firstName} ${uiState.lastName}",
                    modifier = Modifier.testTag("name"))
                StandardProfileInformationText(
                    text = "@${uiState.username}", modifier = Modifier.testTag("username"))
                Column(
                    modifier = Modifier.fillMaxWidth().testTag("leftAlignedViewProfileColumn"),
                ) {
                  Text(
                      text = "Bio",
                      modifier = Modifier.padding(top = 16.dp).testTag("bioLabelText"),
                      color = MaterialTheme.colorScheme.onSurfaceVariant,
                      fontSize = 12.sp,
                      style = MaterialTheme.typography.labelMedium)
                  Text(
                      text = uiState.bio,
                      modifier = Modifier.padding(top = 4.dp).testTag("bioInfoText"),
                      color = MaterialTheme.colorScheme.onSurface,
                      fontSize = 16.sp,
                      style = MaterialTheme.typography.bodyMedium)
                  if (!isPublicView) {
                    Row(
                        modifier = Modifier.padding(top = 16.dp).testTag("phoneNumberBirthDateRow"),
                        horizontalArrangement = Arrangement.SpaceBetween) {
                          Column(modifier = Modifier.weight(1f).testTag("phoneNumberColumn")) {
                            Text(
                                text = "Phone Number",
                                modifier =
                                    Modifier.padding(top = 16.dp).testTag("phoneNumberLabelText"),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp,
                                style = MaterialTheme.typography.labelMedium)
                            Text(
                                text = uiState.phoneNumber,
                                modifier =
                                    Modifier.padding(top = 4.dp).testTag("phoneNumberInfoText"),
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 16.sp,
                                style = MaterialTheme.typography.bodyMedium)
                          }
                          Column(modifier = Modifier.weight(1f).testTag("birthDateColumn")) {
                            Text(
                                text = "Birth Date",
                                modifier =
                                    Modifier.padding(top = 16.dp).testTag("birthDateLabelText"),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp,
                                style = MaterialTheme.typography.labelMedium)
                            Text(
                                text = uiState.birthDate,
                                modifier =
                                    Modifier.padding(top = 4.dp).testTag("birthDateInfoText"),
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 16.sp,
                                style = MaterialTheme.typography.bodyMedium)
                          }
                        }
                  }
                }
              }
            }
      })
}

@Preview(showSystemUi = true, showBackground = true)
@ExcludeFromJacocoGeneratedReport
@Composable
fun PreviewProfile() {
  val userRepository = MockUserRepository()
  val mockUser =
      User(
          userId = "1",
          birthDate = "01.01.2000",
          email = "test@example.com",
          firstName = "John",
          lastName = "Doe",
          phoneNumber = "1234567890",
          accountStatus = "active",
          eventsAttendeeList = mutableListOf("event1", "event2"),
          eventsHostList = mutableListOf("event3"),
          friendsList = mutableListOf("2"),
          profilePicUrl = "http://example.com/Profile_Pictures/1",
          qrCodeUrl = "http://example.com/QR_Codes/1",
          bio =
              "Hi! My name is John Doe. I am a software engineer. I love to code. I am a fan of the Foo Fighters. \nI greatly enjoy fishes. I have a pet fish named Bubbles.",
          username = "johndoe")
  runBlocking { userRepository.addUser(mockUser) }
  val viewModel = ProfileViewModel(userRepository, "1")
  ProfileUi(
      isPublicView = false,
      viewModel = viewModel,
      navigationActions = NavigationActions(rememberNavController()))
}
