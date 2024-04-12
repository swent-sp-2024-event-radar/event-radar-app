package com.github.se.eventradar.ui.login

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
        text = {
          Text(
              text = "Sign in Failed. Please try again.",
              textAlign = TextAlign.Center,
              modifier = Modifier.testTag("loginErrorDisplayText"))
        },
        title = {
          Text(
              text = "Sign in Failed",
              modifier = Modifier.testTag("loginErrorTitle"),
          )
        },
        onDismissRequest = { openErrorDialog.value = false },
        confirmButton = { TextButton(onClick = { openErrorDialog.value = false }) { Text("Ok") } })
  }
}

@Composable
fun LoginScreen(navigationActions: NavigationActions) {
  val openErrorDialog = remember { mutableStateOf(false) }
  val launcher =
      rememberLauncherForActivityResult(
          contract = FirebaseAuthUIActivityResultContract(),
          onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
              navigationActions.navController.navigate(
                  Route.OVERVIEW) // Note: Change to Route.Events When Nav Bar Is Done.
            } else {
              openErrorDialog.value = true
            }
          })
  ErrorDialogBox(openErrorDialog)
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
    ConstraintLayout {
      val (titleRow, signInButton, signUpRow) = createRefs()
      Row(
          modifier =
              Modifier.constrainAs(ref = titleRow, { top.linkTo(parent.top, margin = 16.dp) }),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.event_radar_logo),
                contentDescription = "Logo",
                modifier = Modifier.padding(1.dp).width(43.dp).height(68.dp).testTag("loginLogo"),
            )
            Text(
                text = "Event Radar",
                modifier = Modifier.width(253.dp).height(55.dp).testTag("loginTitle"),
                style =
                    TextStyle(
                        fontSize = 40.sp,
                        lineHeight = 17.sp,
                        fontFamily = FontFamily(Font(R.font.roboto)),
                        fontWeight = FontWeight(700),
                        color = MaterialTheme.colors.onBackground,
                        textAlign = TextAlign.Center,
                        letterSpacing = 0.25.sp,
                    ))
          }

      Button(
          onClick = { launcher.launch(intent) },
          modifier =
              Modifier.wrapContentSize()
                  .constrainAs(
                      ref = signInButton,
                      {
                        top.linkTo(titleRow.bottom, margin = 240.dp)
                        centerHorizontallyTo(titleRow)
                      })
                  .testTag("loginButton"),
          border = BorderStroke(width = 1.dp, color = MaterialTheme.colors.primary),
          colors =
              ButtonDefaults.buttonColors(
                  containerColor =
                      MaterialTheme.colors
                          .primary, // Note: Change to Primary Container once Vicky Merges Request
              ),
      ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Google Logo",
            modifier =
                Modifier.width(24.dp)
                    .height(24.dp)
                    .padding(end = 8.dp)
                    .align(Alignment.CenterVertically),
        )
        Text(
            text = "Sign in with Google",
            modifier = Modifier.padding(horizontal = 25.dp),
            style =
                TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 17.sp,
                    fontFamily = FontFamily(Font(R.font.roboto)),
                    fontWeight = FontWeight(500),
                    color = MaterialTheme.colors.onPrimary,
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.25.sp,
                ))
      }
      Row(
          modifier =
              Modifier.constrainAs(
                  ref = signUpRow,
                  {
                    top.linkTo(signInButton.bottom, margin = 20.dp)
                    centerHorizontallyTo(signInButton)
                  }),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Not a registered user?",
                modifier = Modifier.width(140.dp).height(27.dp),
                style =
                    TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 17.sp,
                        fontFamily = FontFamily(Font(R.font.roboto)),
                        fontWeight = FontWeight(500),
                        color = MaterialTheme.colors.onBackground,
                        textAlign = TextAlign.Center,
                        letterSpacing = 0.25.sp,
                    ))
            Text(
                text = "Sign up here",
                modifier = Modifier.width(101.dp).height(27.dp),
                style =
                    TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 17.sp,
                        fontFamily = FontFamily(Font(R.font.roboto)),
                        fontWeight = FontWeight(500),
                        color = MaterialTheme.colors.secondary,
                        textAlign = TextAlign.Center,
                        letterSpacing = 0.25.sp,
                    ))
          }
    }
  }
}
