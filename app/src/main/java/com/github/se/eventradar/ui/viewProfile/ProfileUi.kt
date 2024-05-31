package com.github.se.eventradar.ui.viewProfile

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
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
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.ui.navigation.Route
import com.github.se.eventradar.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.eventradar.ui.navigation.getTopLevelDestination
import com.github.se.eventradar.viewmodel.ProfileViewModel
import kotlinx.coroutines.runBlocking

@Composable
fun ProfileUi(
    isPublicView: Boolean,
    viewModel: ProfileViewModel,
    navigationActions: NavigationActions
) {
    val isEditMode = remember { mutableStateOf(false) }


    val imagePickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
            onResult = { uri -> viewModel.onSelectedImageUriChanged(uri) })

    viewModel.getProfileDetails()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    Scaffold(
        modifier = Modifier.testTag("profileScreen"),
        topBar = {
            // row and text profile Information
            Row(
                modifier = Modifier.padding(vertical = 32.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically) {
                if (isPublicView) {
                    GoBackButton(
                        modifier = Modifier.testTag("goBackButton"), { navigationActions.goBack() })
                    Text(
                        "Profile Information",
                        modifier = Modifier.testTag("username"),
                        fontSize = 22.sp,
                        letterSpacing = 0.36.sp,
                        fontFamily = FontFamily.Default,
                        color = MaterialTheme.colorScheme.onBackground)
                } else {
                    // Display the Event Radar logo when the view is private
                    Logo(
                        modifier =
                        Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp).testTag("logo"))
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
            } else {
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
                    .testTag("centeredViewProfileColumn"),
                verticalArrangement = Arrangement.Center, // Vertically center the content
                horizontalAlignment =
                Alignment.CenterHorizontally) { // Horizontally center the content) {
                UserProfileImage(
                    uiState.profilePicUrl,
                    uiState.firstName,
                    Modifier.size(150.dp)
                        .testTag("profilePic")
                        .padding(10.dp)
                        .clip(RoundedCornerShape(10.dp)))
                NameText(
                    name = "${uiState.firstName} ${uiState.lastName}",
                    modifier = Modifier.testTag("name"))
                StandardProfileInformationText(
                    text = "@${uiState.username}", modifier = Modifier.testTag("username"))
                Column(
                    modifier = Modifier.fillMaxWidth().testTag("leftAlignedViewProfileColumn"),
                    horizontalAlignment = Alignment.Start) {
                    ProfileInformationText(label = "Bio", info = uiState.bio, testTagPrefix = "bio")
                    if (!isPublicView) {
                        Row(
                            modifier = Modifier.fillMaxWidth().testTag("phoneNumberBirthDateRow"),
                            horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(modifier = Modifier.testTag("phoneNumberColumn")) {
                                ProfileInformationText(
                                    label = "Phone Number",
                                    info = uiState.phoneNumber,
                                    testTagPrefix = "phoneNumber")
                            }
                            Column(
                                modifier = Modifier.testTag("birthDateColumn"),
                                horizontalAlignment = Alignment.Start) {
                                ProfileInformationText(
                                    label = "Birth Date",
                                    info = uiState.birthDate,
                                    testTagPrefix = "birthDate")
                            }
                        }
                    }
                }
                if (isEditMode.value) {
                    Button(
                        onClick = {
                            // Save the changes to the UI state and update the user information in the database
                            viewModel.validateFields()
                            viewModel.updateUserInfo(
                                uiState.profilePicUrl,
                                uiState.firstName,
                                uiState.lastName,
                                uiState.username,
                                uiState.bio,
                                uiState.phoneNumber,
                                uiState.birthDate)
                            isEditMode.value = false
                        },
                        modifier = Modifier.testTag("saveButton")
                    ) {
                        Text("Save")
                    }
                }
            }
        })
}

@Composable
fun ProfileInformationText(label: String, info: String, testTagPrefix: String) {
    Text(
        text = label,
        modifier =
        Modifier.testTag("${testTagPrefix}LabelText")
            .padding(top = 16.dp), // Add padding below the label
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 12.sp,
        fontFamily = FontFamily.SansSerif)
    Text(
        text = info,
        modifier =
        Modifier.testTag("${testTagPrefix}InfoText")
            .padding(top = 4.dp), // Add padding below the info
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = 16.sp,
        fontFamily = FontFamily.SansSerif)
}

@Composable
fun ProfileInformationText(label: String, info: String, testTagPrefix: String, isEditMode: Boolean, onTextChange: (String) -> Unit) {
    if (isEditMode) {
        TextField(
            value = info,
            onValueChange = onTextChange,
            label = { Text(label) },
            modifier = Modifier.testTag("${testTagPrefix}InfoTextEdit")
        )
    } else {
        Text(
            text = info,
            modifier = Modifier.testTag("${testTagPrefix}InfoTextDisplay"),
        )
    }
}

@Composable
fun UserProfileImage(profilePicUrl: String, isEditMode: Boolean, onImageChange: (String) -> Unit, imagePickerLauncher: ManagedActivityResultLauncher<I, O>) {
    val context = LocalContext.current
    if (isEditMode) {
        Box(
            modifier = Modifier.clickable {
                imagePickerLauncher.launch("image/*")
            }
        ) {
            Image(
                painter = rememberImagePainter(profilePicUrl),
                contentDescription = "Profile Picture",
                modifier = Modifier.size(128.dp).clip(RoundedCornerShape(64.dp))
            )
        }
    } else {
        Image(
            painter = rememberImagePainter(profilePicUrl),
            contentDescription = "Profile Picture",
            modifier = Modifier.size(128.dp).clip(RoundedCornerShape(64.dp))
        )
    }
}

@Preview(showSystemUi = true, showBackground = true)
@ExcludeFromJacocoGeneratedReport
@Composable
fun PreviewProfile() {
    val userRepository = MockUserRepository()
    val mockUser =
        User(
            userId = "1",
            birthDate = "01/01/2000",
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