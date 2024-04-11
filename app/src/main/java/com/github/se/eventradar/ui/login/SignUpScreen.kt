package com.github.se.eventradar.ui.login

import android.app.Activity
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.github.se.eventradar.R
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.ui.navigation.Route
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

private const val TAG = "SignUpScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navigationActions: NavigationActions) {
    val selectedImageUri = rememberSaveable { mutableStateOf<Uri?>(null) }
    val name = rememberSaveable { mutableStateOf("") }
    val surname = rememberSaveable { mutableStateOf("") }
    val phoneNumber = rememberSaveable { mutableStateOf("") }
    val birthDate = rememberSaveable { mutableStateOf("") }
    val selectedCountryCode = rememberSaveable { mutableStateOf("+1") } // Default country code
    // List of country codes. You can replace this with your own list.
    val countryCodes = listOf("+1", "+33", "+41", "+44", "+91", "+61", "+81")

    val launcher =
        rememberLauncherForActivityResult(contract = FirebaseAuthUIActivityResultContract(),
            onResult = { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val user = Firebase.auth.currentUser
                    val userValues = hashMapOf(
                        "Name" to name.value,
                        "Surname" to surname.value,
                        "Phone Number" to phoneNumber.value,
                        "Birth Date" to birthDate.value,
                        "Email" to user?.email
                        // TODO: Add picture to userValues

                    )

                    // Add a new document with a generated ID into collection "users"
                    Firebase.firestore.collection("users").add(userValues)
                        .addOnSuccessListener { documentReference ->
                            Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                        }.addOnFailureListener { e ->
                            Log.w(TAG, "Error adding document", e)
                        }

                    navigationActions.navController.navigate(Route.OVERVIEW)
                } else {
                    navigationActions.navController.navigate(Route.LOGIN)
                }
            })

    val imagePickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent(),
            onResult = { uri ->
                // Handle the returned Uri
                selectedImageUri.value = uri
            })

    val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())
    val intent = AuthUI.getInstance().createSignInIntentBuilder().setIsSmartLockEnabled(false)
        .setAvailableProviders(providers).build()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .testTag("signUpScreen"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(R.drawable.event_radar_logo),
            contentDescription = "Logo",
            modifier = Modifier
                .align(Alignment.CenterHorizontally) // Center horizontally
                .width(300.dp) // Constrain on both sides
                .height(100.dp)
                .testTag("logo"),
        )
        Spacer(modifier = Modifier.height(16.dp)) // Space between logo and button
        Button(
            onClick = {
                imagePickerLauncher.launch("image/*") // Launch the image picker

            },
            modifier = Modifier
                .wrapContentSize()
                .testTag("profilePictureButton"),
            border = BorderStroke(width = 1.dp, color = Color(0xFFDADCE0)),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFFFFF)),
        ) {
            Text(
                text = "Choose Profile Picture",
                style = TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 17.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight(500),
                    color = Color(0xFF3C4043),
                    letterSpacing = 0.25.sp,
                ),
                modifier = Modifier.padding(start = 8.dp),
            )
        }
        Spacer(modifier = Modifier.height(16.dp)) // Space between button and text fields
        OutlinedTextField(value = name.value,
            onValueChange = { name.value = it },
            label = { Text("Name") },
            modifier = Modifier.width(320.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFFB422D9), unfocusedBorderColor = Color(0xFFB422D9)
            ),
            shape = RoundedCornerShape(12.dp),
            isError = name.value.isEmpty()
        )
        Spacer(modifier = Modifier.height(16.dp)) // Space between text fields
        OutlinedTextField(value = surname.value,
            onValueChange = { surname.value = it },
            label = { Text("Surname") },
            modifier = Modifier.width(320.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFFB422D9), unfocusedBorderColor = Color(0xFFB422D9)
            ),
            shape = RoundedCornerShape(12.dp),
            isError = name.value.isEmpty()
        )
        Spacer(modifier = Modifier.height(16.dp)) // Space between text fields
        PhoneNumberInput(
            phoneNumber = phoneNumber,
            selectedCountryCode = selectedCountryCode,
            countryCodes = countryCodes
        )

        Spacer(modifier = Modifier.height(16.dp)) // Space between text fields
        // TODO: Replace this TextField with an OutlinedTextField that formats the date as
        //  "dd/MM/yyyy" and validates the input
        OutlinedTextField(value = birthDate.value,
            onValueChange = { birthDate.value = it },
            label = { Text("Birth Date") },
            modifier = Modifier.width(320.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFFB422D9), unfocusedBorderColor = Color(0xFFB422D9)
            ),
            shape = RoundedCornerShape(12.dp),
            isError = name.value.isEmpty()
        )
        Spacer(modifier = Modifier.height(16.dp)) // Space between text fields

        Button(
            onClick = {
                launcher.launch(intent)
            },
            modifier = Modifier
                .wrapContentSize()
                .testTag("loginButton"),
            border = BorderStroke(width = 1.dp, color = Color(0xFFB422D9)),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB422D9)),
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
                text = "Log In with Google",
                style = TextStyle(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneNumberInput(
    phoneNumber: MutableState<String>,
    selectedCountryCode: MutableState<String>,
    countryCodes: List<String>
) {
    var isDropdownExpanded by remember { mutableStateOf(false) }

    OutlinedTextField(value = phoneNumber.value,
        onValueChange = { phoneNumber.value = it },
        label = { Text("Phone Number") },
        modifier = Modifier.width(320.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color(0xFFB422D9), unfocusedBorderColor = Color(0xFFB422D9)
        ),
        shape = RoundedCornerShape(12.dp),
        leadingIcon = {
            Box {
                TextButton(onClick = { isDropdownExpanded = true }) {
                    Text(selectedCountryCode.value)
                }
                DropdownMenu(expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false }) {
                    countryCodes.forEach { countryCode ->
                        DropdownMenuItem(text = { Text(text = countryCode) }, onClick = {
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
    val validLength = when (countryCode) {
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
