package com.github.se.eventradar.ui.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.github.se.eventradar.R

@Composable
fun UserProfileImage(profilePicLink: String, name: String, modifier: Modifier) {
  AsyncImage(
      model = ImageRequest.Builder(LocalContext.current).data(profilePicLink).build(),
      placeholder = painterResource(id = R.drawable.placeholder),
      contentDescription = "Profile picture of ${name}",
      contentScale = ContentScale.Crop,
      modifier = modifier)
}

@Composable
fun NameText(name: String, modifier: Modifier) {
  Text(
      name,
      modifier = modifier,
      fontSize = 32.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = 0.36.sp,
      fontFamily = FontFamily.Default,
      color = MaterialTheme.colorScheme.onBackground)
}

@Composable
fun StandardProfileInformationText(text: String, modifier: Modifier) {
  Text(
      text,
      modifier = modifier,
      fontSize = 14.sp,
      letterSpacing = 0.36.sp,
      fontFamily = FontFamily.Default,
      color = MaterialTheme.colorScheme.onSurface)
}
