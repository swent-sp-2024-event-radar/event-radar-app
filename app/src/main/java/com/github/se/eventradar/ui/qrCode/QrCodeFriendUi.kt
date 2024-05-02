package com.github.se.eventradar.ui.qrCode

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.se.eventradar.R
import com.github.se.eventradar.ui.BottomNavigationMenu
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.ui.navigation.Route
import com.github.se.eventradar.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.eventradar.ui.navigation.TopLevelDestination
import com.github.se.eventradar.viewmodel.qrCode.ScanFriendQrViewModel

// TODO cleaner code for Navigation and to correct screen

@Composable
fun QrCodeScreen(
    viewModel: ScanFriendQrViewModel = hiltViewModel(),
    navigationActions: NavigationActions
) {
  val navigateState by viewModel.action.collectAsState()
  val activeTabState by viewModel.tabState.collectAsState()

  // React to changes in navigation state
  LaunchedEffect(navigateState) {
    when (navigateState) {
      ScanFriendQrViewModel.Action.NavigateToNextScreen -> {
        navigationActions.navigateTo(TOP_LEVEL_DESTINATIONS[1]) //TODO change to private message screen with friend // Adjust according to your actual navigation logic
        viewModel.resetNavigationEvent() // Reset the navigation event in the ViewModel to prevent
      }
      else -> Unit // Do nothing if the state is None or any other non-navigational state
    }
  }

  val context = LocalContext.current

  ConstraintLayout(
      modifier = Modifier.fillMaxSize().testTag("qrCodeScannerScreen"),
  ) {
    val (logo, tabs, bottomNav) = createRefs()
    Row(
        modifier =
            Modifier.fillMaxWidth()
                .fillMaxWidth()
                .constrainAs(logo) {
                  top.linkTo(parent.top, margin = 32.dp)
                  start.linkTo(parent.start, margin = 16.dp)
                }
                .testTag("logo"),
        verticalAlignment = Alignment.CenterVertically) {
          Image(
              painter = painterResource(id = R.drawable.event_logo),
              contentDescription = "Event Radar Logo",
              modifier = Modifier.size(width = 186.dp, height = 50.dp))
        }
    TabRow(
        //
        selectedTabIndex = activeTabState.ordinal,
        modifier =
            Modifier.fillMaxWidth()
                .padding(top = 8.dp)
                .constrainAs(tabs) {
                  top.linkTo(logo.bottom, margin = 16.dp)
                  start.linkTo(parent.start)
                  end.linkTo(parent.end)
                }
                .testTag("tabs"),
        contentColor = MaterialTheme.colorScheme.primary) {
          Tab(
              selected = activeTabState == ScanFriendQrViewModel.TAB.MyQR,
              onClick = { viewModel.changeTabState(ScanFriendQrViewModel.TAB.MyQR) },
              modifier = Modifier.testTag("My QR Code"),
          ) {
            Text(
                text = "My QR Code",
                style =
                    TextStyle(
                        fontSize = 19.sp,
                        lineHeight = 17.sp,
                        fontFamily = FontFamily(Font(R.font.roboto)),
                        fontWeight = FontWeight(500),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center,
                        letterSpacing = 0.25.sp,
                    ),
                modifier = Modifier.padding(bottom = 8.dp))
          }
          Tab(
              selected = activeTabState == ScanFriendQrViewModel.TAB.ScanQR,
              onClick = {
                viewModel.changeTabState(ScanFriendQrViewModel.TAB.ScanQR)
              }, // selectedTabIndex = 1
              modifier = Modifier.testTag("Scan QR Code")) {
                Text(
                    text = "Scan QR Code",
                    style =
                        TextStyle(
                            fontSize = 19.sp,
                            lineHeight = 17.sp,
                            fontFamily = FontFamily(Font(R.font.roboto)),
                            fontWeight = FontWeight(500),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center,
                            letterSpacing = 0.25.sp,
                        ),
                    modifier = Modifier.padding(bottom = 8.dp))
              }
        }

    if (activeTabState == ScanFriendQrViewModel.TAB.MyQR) {
      Toast.makeText(context, "My Qr Code not yet available", Toast.LENGTH_SHORT).show()
    } else {
      Column(modifier = Modifier.testTag("QrScanner")) {
        QrCodeScanner(analyser = viewModel.qrCodeAnalyser)
      }
    }
    BottomNavigationMenu(
        onTabSelected = { tab -> navigationActions.navigateTo(tab) },
        tabList = TOP_LEVEL_DESTINATIONS,
        selectedItem = TOP_LEVEL_DESTINATIONS[0],
        modifier =
            Modifier.testTag("bottomNavMenu").constrainAs(bottomNav) {
              bottom.linkTo(parent.bottom)
              start.linkTo(parent.start)
              end.linkTo(parent.end)
            })
  }
}

// @androidx.compose.ui.tooling.preview.Preview
// @Composable
// fun QrcodeScanTest() {
//  QrCodeScreen(navigationActions = NavigationActions(),
// }
