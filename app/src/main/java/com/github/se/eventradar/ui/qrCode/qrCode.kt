package com.github.se.eventradar.ui.qrCode
import android.Manifest
import androidx.navigation.compose.rememberNavController
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.eventradar.ui.BottomNavigationMenu
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview as Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import com.github.se.eventradar.R
import com.github.se.eventradar.qrCode.QrCodeScan
import com.github.se.eventradar.ui.BottomNavigationMenu
import com.github.se.eventradar.ui.navigation.TOP_LEVEL_DESTINATIONS

class qrCodeScannerUi(private val onQrCodeScanned: (String) -> Unit) {


  @Composable
  fun QrCodeScreen(navigationActions: NavigationActions) {
      var selectedTabIndex by remember { mutableIntStateOf(0) }
      val context = LocalContext.current

      ConstraintLayout(modifier = Modifier.fillMaxSize().testTag("homeScreen"),
          ) {
          val (logo, tabs, qrScanner, bottomNav) = createRefs()
          Row(
              modifier =
              Modifier.fillMaxWidth()
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
              selectedTabIndex = selectedTabIndex,
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
                  selected = selectedTabIndex == 0,
                  onClick = { selectedTabIndex = 0 },
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
                  selected = selectedTabIndex == 1,
                  onClick = { selectedTabIndex = 1 },
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

          if (selectedTabIndex == 0) {
                  Toast.makeText(context, "My Qr Code not yet available", Toast.LENGTH_SHORT).show()
          } else {


          }
          BottomNavigationMenu(
              onTabSelected = { tab -> navigationActions.navigateTo(tab) },
              tabList = TOP_LEVEL_DESTINATIONS,
              selectedItem = TOP_LEVEL_DESTINATIONS[2],
              modifier =
              Modifier.testTag("bottomNavMenu").constrainAs(bottomNav) {
                  bottom.linkTo(parent.bottom)
                  start.linkTo(parent.start)
                  end.linkTo(parent.end)
              })

      }
  }


}






val dummyQrCodeScanned: (String) -> Unit = { qrCode ->
  Log.d("QRCodeScanner", "QR Code Scanned: $qrCode")
  // You can perform any additional logic here for testing
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun QrcodeScanTest() {
  qrCodeScannerUi(dummyQrCodeScanned).QrCodeScreen(NavigationActions(rememberNavController()))
}
