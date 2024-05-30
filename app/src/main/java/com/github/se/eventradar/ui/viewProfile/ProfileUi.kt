package com.github.se.eventradar.ui.viewProfile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.github.se.eventradar.ExcludeFromJacocoGeneratedReport
import com.github.se.eventradar.R
import com.github.se.eventradar.model.repository.user.MockUserRepository
import com.github.se.eventradar.ui.BottomNavigationMenu
import com.github.se.eventradar.ui.component.GoBackButton
import com.github.se.eventradar.ui.component.NameText
import com.github.se.eventradar.ui.component.StandardProfileInformationText
import com.github.se.eventradar.ui.component.UserProfileImage
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.ui.navigation.Route
import com.github.se.eventradar.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.eventradar.ui.navigation.getTopLevelDestination
import com.github.se.eventradar.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    isPublicView: Boolean,
    viewModel: ProfileViewModel,
    navigationActions: NavigationActions
) {
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
                GoBackButton(
                    modifier = Modifier.testTag("goBackButton"), { navigationActions.goBack() })
                Text(
                    "Profile Information",
                    modifier = Modifier.testTag("username"),
                    fontSize = 22.sp,
                    letterSpacing = 0.36.sp,
                    fontFamily = FontFamily.Default,
                    color = MaterialTheme.colorScheme.onBackground)
            }
        },
        bottomBar = {
            BottomNavigationMenu(
                onTabSelected = navigationActions::navigateTo,
                tabList = TOP_LEVEL_DESTINATIONS,
                selectedItem = if (isPublicView) getTopLevelDestination(Route.MESSAGE) else getTopLevelDestination(Route.PROFILE),
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
                    text = "@${uiState.username}",
                    modifier = Modifier.testTag("username"))
                ProfileInformationText(
                    label = "Bio",
                    info = uiState.bio,
                    modifier = Modifier.testTag("bioInfoText")
                )
                if (!isPublicView) {
                    ProfileInformationText(
                        label = "Phone Number",
                        info = uiState.phoneNumber,
                        modifier = Modifier.testTag("phoneNumberInfoText")
                    )
                    ProfileInformationText(
                        label = "Birth Date",
                        info = uiState.birthDate,
                        modifier = Modifier.testTag("birthDateInfoText")
                    )
                }
            }
        })
}

@Composable
fun ProfileInformationText(label: String, info: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
            fontFamily = FontFamily.SansSerif
        )
        Text(
            text = info,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp,
            fontFamily = FontFamily.SansSerif
        )
    }
}

@Preview(showSystemUi = true, showBackground = true)
@ExcludeFromJacocoGeneratedReport
@Composable
fun PreviewProfile() {
    val userRepository = MockUserRepository()
    val viewModel = ProfileViewModel(userRepository, "")
    ProfileScreen(
        isPublicView = true, viewModel = viewModel, navigationActions = NavigationActions(rememberNavController()))
}
