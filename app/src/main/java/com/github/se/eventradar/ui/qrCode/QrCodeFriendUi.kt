package com.github.se.eventradar.ui.qrCode

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.se.eventradar.R
import com.github.se.eventradar.ui.BottomNavigationMenu
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.ui.navigation.Route
import com.github.se.eventradar.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.eventradar.viewmodel.qrCode.ScanFriendQrViewModel
import com.github.se.eventradar.viewmodel.qrCode.Tab

@Composable
fun QrCodeScreen(
    viewModel: ScanFriendQrViewModel = hiltViewModel(),
    navigationActions: NavigationActions
) {

  val qrScanUiState = viewModel.uiState.collectAsStateWithLifecycle()

  // React to changes in navigation state
  LaunchedEffect(Unit) {
    viewModel.setDecodedResultCallback { friendId ->
      Log.d("QrCodeFriendViewModel", "Decoded QR Code: $friendId")
      if (friendId != null) {
        viewModel.onDecodedResultChanged(friendId)

        // Navigate to private chat screen
        if (viewModel.updateFriendList(friendId)) {
          navigationActions.navController.navigate(Route.PRIVATE_CHAT + "/$friendId")
        }
      } else {
        Log.d("QrCodeFriendViewModel", "Friend ID is null")
      }
    }
  }

  ConstraintLayout(
      modifier = Modifier.fillMaxSize().testTag("qrCodeScannerScreen"),
  ) {
    val (logo, tabs, myqrcode, bottomNav) = createRefs()
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
        selectedTabIndex = qrScanUiState.value.tabState.ordinal,
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
              selected = qrScanUiState.value.tabState == Tab.MyQR,
              onClick = { viewModel.changeTabState(Tab.MyQR) },
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
              selected = qrScanUiState.value.tabState == Tab.ScanQR,
              onClick = { viewModel.changeTabState(Tab.ScanQR) },
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

    if (qrScanUiState.value.tabState == Tab.MyQR) {
      Column(
          modifier =
              Modifier.testTag("myQrCodeScreen").constrainAs(myqrcode) {
                top.linkTo(tabs.bottom, margin = 74.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
              },
          verticalArrangement = Arrangement.Center, // Vertically center the content
          horizontalAlignment = Alignment.CenterHorizontally // Horizontally center the content
          ) {
            MyQrCodeScreen(viewModel)
          }
    } else {
      Column(modifier = Modifier.testTag("QrScanner")) {
        QrCodeScanner(analyser = viewModel.qrCodeAnalyser)
      }
    }
    //        }
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
