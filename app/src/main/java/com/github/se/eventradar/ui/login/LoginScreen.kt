package com.github.se.eventradar.ui.login

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.github.se.eventradar.R
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.ui.navigation.Route

@Composable
fun ErrorDialogBox(openErrorDialog: MutableState<Boolean>) {
  val display by openErrorDialog
  if (display) {
    AlertDialog(
        icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Account Icon") },
        text = { Text("Sign in Failed. Please try again.", textAlign = TextAlign.Center) },
        title = { Text("Sign in Failed") },
        onDismissRequest = { openErrorDialog.value = false },
        confirmButton = { TextButton(onClick = { openErrorDialog.value = false }) { Text("Ok") } })
  }
}

@Composable
fun LoginScreen(navigationActions: NavigationActions) {

  val launcher =
      rememberLauncherForActivityResult(
          contract = FirebaseAuthUIActivityResultContract(),
          onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK)
                navigationActions.navController.navigate(Route.OVERVIEW)
            else navigationActions.navController.navigate(Route.LOGIN)
          })

  val providers =
      arrayListOf(
          AuthUI.IdpConfig.GoogleBuilder().build(),
      )

  val intent =
      AuthUI.getInstance()
          .createSignInIntentBuilder()
          .setIsSmartLockEnabled(false)
          .setAvailableProviders(providers)
          .build()

  Column(
      modifier = Modifier.fillMaxSize().testTag("loginScreen"),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Image(
        painter = painterResource(R.drawable.image1),
        contentDescription = "Logo",
        modifier = Modifier.width(132.dp).height(132.dp).testTag("logo"),
    )
    Text(
        text = "Welcome",
        style =
            TextStyle(
                fontSize = 57.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight(400),
                textAlign = TextAlign.Center,
            ),
        modifier = Modifier.testTag("loginTitle"))
    Button(
        onClick = {
          launcher.launch(intent)
          navigationActions.navController.navigate(Route.OVERVIEW)
        },
        modifier = Modifier.wrapContentSize().testTag("loginButton"),
        border = BorderStroke(width = 1.dp, color = Color(0xFFDADCE0)),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFFFFF),
            ),
    ) {
      Image(
          painter = painterResource(id = R.drawable.logo),
          contentDescription = "Google Logo",
          modifier = Modifier.width(24.dp).height(24.dp).align(Alignment.CenterVertically),
      )
      Text(
          text = "Sign in with Google",
          style =
              TextStyle(
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
}
