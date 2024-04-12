package com.github.se.eventradar.ui.login

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.util.Log
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.rememberImagePainter
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.github.se.eventradar.R
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.ui.navigation.Route
import com.github.se.eventradar.util.toast
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat

private const val TAG = "SignUpScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navigationActions: NavigationActions) {

    val openErrorDialog = remember { mutableStateOf(false) }

    val context = LocalContext.current
    val selectedImageUri = rememberSaveable { mutableStateOf<Uri?>(null) }
    val username = rememberSaveable { mutableStateOf("") }
    val name = rememberSaveable { mutableStateOf("") }
    val surname = rememberSaveable { mutableStateOf("") }
    val phoneNumber = rememberSaveable { mutableStateOf("") }
    val birthDate = rememberSaveable { mutableStateOf("") }
    val selectedCountryCode = rememberSaveable { mutableStateOf("+1") } // Default country code
    // List of country codes. You can replace this with your own list.
    val countryCodes = listOf("+1", "+33", "+41", "+44", "+91", "+61", "+81")

    val launcher =
        rememberLauncherForActivityResult(
            contract = FirebaseAuthUIActivityResultContract(),
            onResult = { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val user = Firebase.auth.currentUser
                    // If no image is selected, use a placeholder image
                    val imageURI = selectedImageUri.value
                        ?: Uri.parse("android.resource://com.github.se.eventradar/drawable/placeholder")
                    val userValues = hashMapOf(
                        "Name" to name.value,
                        "Surname" to surname.value,
                        "Phone Number" to phoneNumber.value,
                        "Birth Date" to birthDate.value,
                        "Email" to user?.email,
                        "Profile Picture" to imageURI.toString()
                    )

                    // Add a new document with a generated ID into collection "users"
                    Firebase.firestore.collection("users").add(userValues)
                        .addOnSuccessListener { documentReference ->
                            Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                            context.toast("User data added successfully")
                        }.addOnFailureListener { e ->
                            Log.w(TAG, "Error adding document", e)
                            context.toast("Error adding user data")
                        }
                    navigationActions.navController.navigate(Route.OVERVIEW)
                } else {
                    // Handle the error
                    openErrorDialog.value = true
                }
            })

    ErrorDialogBox(openErrorDialog = openErrorDialog)

    val imagePickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
            onResult = { uri ->
                // Handle the returned Uri
                selectedImageUri.value = uri
            })

    val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())
    val intent =
        AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setIsSmartLockEnabled(false)
            .setAvailableProviders(providers)
            .build()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("signUpScreen"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ConstraintLayout {
            val (
                titleRow,
                profilePicture,
                usernameField,
                nameField,
                surnameField,
                phoneField,
                birthDateField,
                logInButton) =
                createRefs()
            Row(
                modifier =
                Modifier.constrainAs(ref = titleRow) {
                    top.linkTo(parent.top, margin = 16.dp)
                    centerHorizontallyTo(parent)
                },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.event_radar_logo),
                    contentDescription = "Event Radar Logo",
                    modifier =
                    Modifier
                        .padding(1.dp)
                        .width(300.dp)
                        .height(100.dp)
                        .testTag("signUpLogo"),
                )
            }
            Row(
                modifier =
                Modifier.constrainAs(ref = profilePicture) {
                    top.linkTo(titleRow.bottom, margin = 32.dp)
                    centerHorizontallyTo(parent)
                },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val imageUri = selectedImageUri.value
                if (imageUri != null) {
                    val imageBitmap = rememberImagePainter(data = imageUri)
                    Image(
                        painter = imageBitmap,
                        contentDescription = "Selected Profile Picture",
                        modifier =
                        Modifier
                            .size(150.dp) // Adjust size as needed
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
                            R.drawable
                                .placeholder
                        ), // Replace with your placeholder image resource
                        contentDescription = "Profile Picture Placeholder",
                        modifier =
                        Modifier
                            .size(150.dp) // Adjust size as needed
                            .testTag("signUpProfilePicture")
                            .clickable {
                                imagePickerLauncher.launch("image/*")
                            } // Launch the image picker when the placeholder is clicked
                    )
                }
            }
            Row(
                modifier =
                Modifier
                    .constrainAs(ref = usernameField) {
                        top.linkTo(profilePicture.bottom, margin = 16.dp)
                        centerHorizontallyTo(parent)
                    }
                    .testTag("signUpUsernameField"),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = username.value,
                    onValueChange = { username.value = it },
                    label = { Text("Username") },
                    modifier = Modifier
                        .width(320.dp)
                        .testTag("signUpUsernameTextField"),
                    colors =
                    TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = {
                        Text(
                            "@",
                            Modifier.padding(start = 12.dp),
                            MaterialTheme.colorScheme.primary
                        )
                    },
                    isError = username.value.isEmpty(),
                )
            }
            Row(
                modifier =
                Modifier
                    .constrainAs(ref = nameField) {
                        top.linkTo(usernameField.bottom, margin = 16.dp)
                        centerHorizontallyTo(parent)
                    }
                    .testTag("signUpNameField"),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = name.value,
                    onValueChange = { name.value = it },
                    label = { Text("Name") },
                    modifier = Modifier
                        .width(320.dp)
                        .testTag("signUpNameTextField"),
                    colors =
                    TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    isError = name.value.isEmpty()
                )
            }
            Row(
                modifier =
                Modifier
                    .constrainAs(ref = surnameField) {
                        top.linkTo(nameField.bottom, margin = 16.dp)
                        centerHorizontallyTo(parent)
                    }
                    .testTag("signUpSurnameField"),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = surname.value,
                    onValueChange = { surname.value = it },
                    label = { Text("Surname") },
                    modifier = Modifier
                        .width(320.dp)
                        .testTag("signUpSurnameTextField"),
                    colors =
                    TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    isError = name.value.isEmpty()
                )
            }
            Row(
                modifier =
                Modifier
                    .constrainAs(ref = phoneField) {
                        top.linkTo(surnameField.bottom, margin = 16.dp)
                        centerHorizontallyTo(parent)
                    }
                    .testTag("signUpPhoneField"),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PhoneNumberInput(
                    phoneNumber = phoneNumber,
                    selectedCountryCode = selectedCountryCode,
                    countryCodes = countryCodes
                )
            }
            Row(
                modifier =
                Modifier
                    .constrainAs(ref = birthDateField) {
                        top.linkTo(phoneField.bottom, margin = 16.dp)
                    }
                    .testTag("signUpBirthDateField"),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = birthDate.value,
                    onValueChange = { birthDate.value = it },
                    label = { Text("Birth Date (DD/MM/YYYY)") },
                    modifier = Modifier
                        .width(320.dp)
                        .testTag("signUpBirthDateTextField"),
                    colors =
                    TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    isError = !isValidDate(birthDate.value),
                )
            }
            Row(
                modifier =
                Modifier.constrainAs(ref = logInButton) {
                    top.linkTo(birthDateField.bottom, margin = 16.dp)
                    centerHorizontallyTo(parent)
                },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        launcher.launch(intent)
                    },
                    modifier = Modifier
                        .wrapContentSize()
                        .width(250.dp)
                        .testTag("signUpLoginButton"),
                    border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary),
                    colors =
                    ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Google Logo",
                        modifier = Modifier
                            .width(24.dp)
                            .height(24.dp)
                            .align(Alignment.CenterVertically),
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneNumberInput(
    phoneNumber: MutableState<String>,
    selectedCountryCode: MutableState<String>,
    countryCodes: List<String>
) {
    var isDropdownExpanded by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = phoneNumber.value,
        onValueChange = { phoneNumber.value = it },
        label = { Text("Phone Number") },
        modifier = Modifier
            .width(320.dp)
            .testTag("signUpPhoneTextField"),
        colors =
        TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.primary
        ),
        shape = RoundedCornerShape(12.dp),
        leadingIcon = {
            Box {
                TextButton(onClick = { isDropdownExpanded = true }) {
                    Text(selectedCountryCode.value, color = MaterialTheme.colorScheme.primary)
                }
                DropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false }) {
                    countryCodes.forEach { countryCode ->
                        DropdownMenuItem(
                            text = { Text(text = countryCode) },
                            onClick = {
                                selectedCountryCode.value = countryCode
                                isDropdownExpanded = false
                            })
                    }
                }
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        isError = !isValidPhoneNumber(phoneNumber.value, selectedCountryCode.value)
    )
}

fun isValidPhoneNumber(phoneNumber: String, countryCode: String): Boolean {
    val validLength =
        when (countryCode) {
            "+1" -> 10 // USA
            "+33" -> 9 // France
            "+41" -> 9 // Switzerland
            "+44" -> 10 // UK
            "+61" -> 9 // Australia
            "+81" -> 10 // Japan
            "+91" -> 10 // India
            // Add more country codes and their valid lengths here
            else -> 0
        }
    return phoneNumber.length == validLength
}

@SuppressLint("SimpleDateFormat")
fun isValidDate(date: String): Boolean {
    val format = SimpleDateFormat("dd/MM/yyyy")
    format.isLenient = false
    return try {
        format.parse(date)
        true
    } catch (e: Exception) {
        false
    }
}
