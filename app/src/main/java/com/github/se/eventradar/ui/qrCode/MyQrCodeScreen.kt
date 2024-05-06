package com.github.se.eventradar.ui.qrCode

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Absolute.Center
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
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
            "@${uiState.username}",
            modifier = Modifier.testTag("username"),
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.36.sp,
            fontFamily = FontFamily.Default,
            color = MaterialTheme.colorScheme.onBackground
            )

        AsyncImage(
            model =
                ImageRequest.Builder(LocalContext.current)
                    .data(uiState.qrCodeLink)
                    .crossfade(true)
                    .build(),
            contentDescription = stringResource(R.string.my_qr_code),
            modifier = Modifier.size(width = 300.dp, height = 300.dp).testTag("myQrCodeImage"))
      }
}
