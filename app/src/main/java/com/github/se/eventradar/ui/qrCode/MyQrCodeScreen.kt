package com.github.se.eventradar.ui.qrCode

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.github.se.eventradar.R
import com.github.se.eventradar.viewmodel.MyQrCodeViewModel

@Composable
fun MyQrCodeScreen(
    viewModel: MyQrCodeViewModel,
    modifier: Modifier = Modifier.testTag("myQrCodeScreen")
) {

    val link = "https://firebasestorage.googleapis.com/v0/b/event-radar-e6a76.appspot.com/o/QR_Codes%2FAwOXI3dCWjfYKk7bnBQ0S94WxbD2.png?alt=media&token=8c803d0a-baa9-423e-b4ae-193cf46e4f4f"
    val uid = "AwOXI3dCWjfYKk7bnBQ0S94WxbD2"
  LaunchedEffect(Unit) {
    viewModel.getUsername()
    viewModel.getQRCodeLink()
  }

  val uiState by viewModel.uiState.collectAsState()

  Column(
      modifier = modifier,
      verticalArrangement = Arrangement.Center, // Vertically center the content
      horizontalAlignment = Alignment.CenterHorizontally // Horizontally center the content
      ) {

        Text(
            "@${uiState.username}", // uiState.qrCode @username
            modifier = Modifier.testTag("username"),
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.36.sp,
            fontFamily = FontFamily.Default,
            color = MaterialTheme.colorScheme.onBackground // Set the color to black,
            )

        AsyncImage(
            model =
                ImageRequest.Builder(LocalContext.current)
                    .data(uiState.qrCodeLink) // uiState.qrCodeLink (only works for jpg)
                    .crossfade(true)
                    .build(),
            error = painterResource(R.drawable.qr_code), // should be a error indicative
            placeholder = painterResource(R.drawable.placeholder), // should be loading image
            contentDescription = stringResource(R.string.my_qr_code),
            modifier = Modifier.size(width = 300.dp, height = 300.dp).testTag("myQrCodeImage"))
      }
}
