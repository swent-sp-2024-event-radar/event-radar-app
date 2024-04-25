package com.github.se.eventradar.ui.component

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.eventradar.R
import com.github.se.eventradar.model.event.Event

@Composable
fun ViewToggleFab(modifier: Modifier = Modifier, onClick: () -> Unit, iconVector: ImageVector) {
  FloatingActionButton(onClick = { onClick() }, modifier = modifier) {
    Icon(imageVector = iconVector, contentDescription = null)
  }
}

@Composable
fun Logo(modifier: Modifier = Modifier) {
  Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
    Image(
        painter = painterResource(id = R.drawable.event_logo),
        contentDescription = "Event Radar Logo",
        modifier = Modifier.size(width = 186.dp, height = 50.dp))
  }
}

@Composable
fun EventList(events: List<Event>, modifier: Modifier = Modifier) {
  LazyColumn(modifier = modifier) { items(events) { event -> EventCard(event) } }
}

@Composable
fun EventCard(event: Event) {
  // TODO: connected to event details
  val context = LocalContext.current
  Card(
      modifier =
          Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
              .height(IntrinsicSize.Min)
              .testTag("eventCard"),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
      onClick = {
        Toast.makeText(context, "Event details not yet available", Toast.LENGTH_SHORT).show()
      }) {
        Row(modifier = Modifier.fillMaxSize()) {
          Column(
              modifier =
                  Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 8.dp)
                      .weight(1f),
              verticalArrangement = Arrangement.Center) {
                Text(
                    text = event.eventName,
                    style =
                        TextStyle(
                            fontSize = 18.sp,
                            fontFamily = FontFamily(Font(R.font.roboto)),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        ))
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = event.description,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style =
                        TextStyle(
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.roboto)),
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onPrimaryContainer))
              }
          Image(
              painter = painterResource(id = R.drawable.placeholder),
              contentDescription = "Event Image",
              contentScale = ContentScale.FillBounds,
              modifier = Modifier.weight(0.3f).fillMaxHeight())
        }
      }
}