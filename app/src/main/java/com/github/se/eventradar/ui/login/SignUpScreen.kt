package com.github.se.eventradar.ui.login

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.github.se.eventradar.R
import com.github.se.eventradar.model.repository.user.MockUserRepository
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.ui.navigation.Route
import com.github.se.eventradar.viewmodel.CountryCode
import com.github.se.eventradar.viewmodel.LoginViewModel

@Composable
fun SignUpScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    navigationActions: NavigationActions
) {
  val uiState by viewModel.uiState.collectAsState()

  val openErrorDialog = remember { mutableStateOf(false) }

  val launcher =
      rememberLauncherForActivityResult(
          contract = FirebaseAuthUIActivityResultContract(),
          onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
              viewModel.addUser()
              navigationActions.navController.navigate(Route.HOME)
            } else {
              openErrorDialog.value = true
            }
          })

  ErrorDialogBox(openErrorDialog, modifier = Modifier.testTag("signUpErrorDialog"))

  val imagePickerLauncher =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.GetContent(),
          onResult = { uri -> viewModel.onSelectedImageUriChanged(uri) })

  val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())
  val intent =
      AuthUI.getInstance()
          .createSignInIntentBuilder()
          .setIsSmartLockEnabled(false)
          .setAvailableProviders(providers)
          .build()

  Column(
      modifier =
          Modifier.fillMaxSize().testTag("signUpScreen").verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Row(
        modifier = Modifier.padding(top = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically) {
          Image(
              painter = painterResource(R.drawable.event_radar_logo),
              contentDescription = "Event Radar Logo",
              modifier = Modifier.padding(1.dp).width(300.dp).height(100.dp).testTag("signUpLogo"),
          )
        }
    Row(
        modifier = Modifier.padding(top = 32.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically) {
          val imageUri = uiState.selectedImageUri
          if (imageUri != null) {
            val imageBitmap = rememberImagePainter(data = imageUri)
            Image(
                painter = imageBitmap,
                contentDescription = "Selected Profile Picture",
                contentScale = ContentScale.Crop,
                modifier =
                    Modifier.size(150.dp) // Adjust size as needed
                        .clip(CircleShape)
                        .testTag("signUpProfilePicture")
                        .clickable {
                          imagePickerLauncher.launch("image/*")
                        } // Launch the image picker when the image is clicked
                )
          } else {
            Image(
                painter =
                    painterResource(
                        id =
                            R.drawable.placeholder), // Replace with your placeholder image resource
                contentDescription = "Profile Picture Placeholder",
                contentScale = ContentScale.Crop,
                modifier =
                    Modifier.size(150.dp) // Adjust size as needed
                        .clip(CircleShape)
                        .testTag("signUpProfilePicture")
                        .clickable {
                          imagePickerLauncher.launch("image/*")
                        } // Launch the image picker when the placeholder is clicked
                )
          }
        }
    Row(
        modifier = Modifier.padding(top = 16.dp).testTag("signUpUsernameField"),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically) {
          OutlinedTextField(
              value = uiState.username,
              onValueChange = viewModel::onUsernameChanged,
              label = { Text("Username") },
              modifier = Modifier.width(320.dp).testTag("signUpUsernameTextField"),
              colors =
                  OutlinedTextFieldDefaults.colors()
                      .copy(
                          focusedPrefixColor = MaterialTheme.colorScheme.primary,
                          unfocusedPrefixColor = MaterialTheme.colorScheme.primary),
              shape = RoundedCornerShape(12.dp),
              leadingIcon = {
                Text("@", Modifier.padding(start = 12.dp), MaterialTheme.colorScheme.primary)
              },
              isError = uiState.userNameIsError,
          )
        }
    Row(
        modifier = Modifier.padding(top = 16.dp).testTag("signUpNameField"),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically) {
          OutlinedTextField(
              value = uiState.firstName,
              onValueChange = viewModel::onFirstNameChanged,
              label = { Text("First Name") },
              modifier = Modifier.width(320.dp).testTag("signUpNameTextField"),
              colors =
                  OutlinedTextFieldDefaults.colors()
                      .copy(
                          focusedPrefixColor = MaterialTheme.colorScheme.primary,
                          unfocusedPrefixColor = MaterialTheme.colorScheme.primary),
              shape = RoundedCornerShape(12.dp),
              isError = uiState.firstNameIsError,
          )
        }
    Row(
        modifier = Modifier.padding(top = 16.dp).testTag("signUpSurnameField"),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically) {
          OutlinedTextField(
              value = uiState.lastName,
              onValueChange = viewModel::onLastNameChanged,
              label = { Text("Last Name") },
              modifier = Modifier.width(320.dp).testTag("signUpSurnameTextField"),
              colors =
                  OutlinedTextFieldDefaults.colors()
                      .copy(
                          focusedPrefixColor = MaterialTheme.colorScheme.primary,
                          unfocusedPrefixColor = MaterialTheme.colorScheme.primary),
              shape = RoundedCornerShape(12.dp),
              isError = uiState.lastNameIsError,
          )
        }
    Row(
        modifier = Modifier.padding(top = 16.dp).testTag("signUpPhoneField"),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically) {
          PhoneNumberInput(
              phoneNumber = uiState.phoneNumber,
              onPhoneNumberChanged = viewModel::onPhoneNumberChanged,
              selectedCountryCode = uiState.selectedCountryCode,
              onCountryCodeChanged = viewModel::onCountryCodeChanged,
              isValidPhoneNumber = uiState.phoneNumberIsError,
          )
        }
    Row(
        modifier = Modifier.padding(top = 16.dp).testTag("signUpBirthDateField"),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically) {
          OutlinedTextField(
              value = uiState.birthDate,
              onValueChange = viewModel::onBirthDateChanged,
              label = { Text("Birth Date (DD/MM/YYYY)") },
              modifier = Modifier.width(320.dp).testTag("signUpBirthDateTextField"),
              colors =
                  OutlinedTextFieldDefaults.colors()
                      .copy(
                          focusedPrefixColor = MaterialTheme.colorScheme.primary,
                          unfocusedPrefixColor = MaterialTheme.colorScheme.primary),
              shape = RoundedCornerShape(12.dp),
              isError = uiState.birthDateIsError,
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          )
        }
    Row(
        modifier = Modifier.padding(top = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically) {
          Button(
              onClick = {
                if (viewModel.validateFields()) {
                  launcher.launch(intent)
                }
              },
              modifier = Modifier.wrapContentSize().width(250.dp).testTag("signUpLoginButton"),
              border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary),
              colors =
                  ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
          ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Google Logo",
                modifier = Modifier.width(24.dp).height(24.dp).align(Alignment.CenterVertically),
            )
            Text(
                text = "Sign Up with Google",
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
        }
  }
}

@Composable
fun PhoneNumberInput(
    phoneNumber: String,
    onPhoneNumberChanged: (String) -> Unit,
    selectedCountryCode: CountryCode,
    onCountryCodeChanged: (CountryCode) -> Unit,
    isValidPhoneNumber: Boolean,
) {
  var isDropdownExpanded by remember { mutableStateOf(false) }

  OutlinedTextField(
      value = phoneNumber,
      onValueChange = onPhoneNumberChanged,
      label = { Text("Phone Number") },
      modifier = Modifier.width(320.dp).testTag("signUpPhoneTextField"),
      colors =
          OutlinedTextFieldDefaults.colors()
              .copy(
                  focusedPrefixColor = MaterialTheme.colorScheme.primary,
                  unfocusedPrefixColor = MaterialTheme.colorScheme.primary),
      shape = RoundedCornerShape(12.dp),
      leadingIcon = {
        Box {
          TextButton(onClick = { isDropdownExpanded = true }) {
            Text(selectedCountryCode.ext, color = MaterialTheme.colorScheme.primary)
          }
          DropdownMenu(
              expanded = isDropdownExpanded, onDismissRequest = { isDropdownExpanded = false }) {
                val countryCodes = enumValues<CountryCode>()
                countryCodes.forEach { code ->
                  DropdownMenuItem(
                      onClick = {
                        onCountryCodeChanged(code)
                        isDropdownExpanded = false
                      },
                      text = { Text(code.ext) })
                }
              }
        }
      },
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
      isError = isValidPhoneNumber)
}

@Preview
@Composable
fun PreviewSignUpScreen() {
  SignUpScreen(LoginViewModel(MockUserRepository()), NavigationActions(rememberNavController()))
}
