package com.github.se.eventradar.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.eventradar.R
import com.github.se.eventradar.model.event.Event
import com.github.se.eventradar.model.event.EventCategory
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun HomeScreen() {
  val mockEvents =
      listOf(
          Event(
              "1",
              "User1",
              "Jazz Concert",
              "An evening with smooth jazz.",
              LocalDate.of(2024, 4, 12),
              LocalTime.of(19, 30),
              EventCategory.MUSIC),
          Event(
              "2",
              "User2",
              "Soccer Game",
              "Cheer for your local soccer team!",
              LocalDate.of(2024, 4, 18),
              LocalTime.of(15, 45),
              EventCategory.SPORTS),
          Event(
              "3",
              "User3",
              "Tech Conference",
              "The latest in tech innovation.",
              LocalDate.of(2024, 5, 24),
              LocalTime.of(10, 0),
              EventCategory.CONFERENCE))

  var selectedTabIndex by remember { mutableIntStateOf(0) }

  Column(modifier = Modifier.fillMaxSize()) {
    Spacer(modifier = Modifier.height(50.dp))
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 32.dp),
        verticalAlignment = Alignment.CenterVertically) {
          Image(
              painter = painterResource(id = R.drawable.event_logo),
              contentDescription = "Event Radar Logo",
              modifier = Modifier.size(width = 184.35.dp, height = 46.99.dp))
        }
    Spacer(modifier = Modifier.height(16.dp))
    TabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = Modifier.fillMaxWidth().height(96.dp).padding(32.dp),
        contentColor = MaterialTheme.colorScheme.primary) {
          Tab(
              selected = selectedTabIndex == 0,
              onClick = { selectedTabIndex = 0 },
          ) {
            Text(
                text = "Browse",
                style =
                    TextStyle(
                        fontSize = 19.sp,
                        lineHeight = 17.sp,
                        fontFamily = FontFamily(Font(R.font.roboto)),
                        fontWeight = FontWeight(500),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center,
                        letterSpacing = 0.25.sp,
                    ))
          }
          Tab(selected = selectedTabIndex == 1, onClick = { selectedTabIndex = 1 }) {
            Text(
                text = "Upcoming",
                style =
                    TextStyle(
                        fontSize = 19.sp,
                        lineHeight = 17.sp,
                        fontFamily = FontFamily(Font(R.font.roboto)),
                        fontWeight = FontWeight(500),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center,
                        letterSpacing = 0.25.sp,
                    ))
          }
        }
    Spacer(modifier = Modifier.height(8.dp))
    if (selectedTabIndex == 0) {
      EventList(mockEvents)
    } else {
      // "Upcoming" tab content
      Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Upcoming events feature not implemented yet",
            style =
                TextStyle(
                    fontFamily = FontFamily(Font(R.font.roboto)),
                    fontSize = 18.sp,
                    fontWeight = FontWeight(500)),
            textAlign = TextAlign.Center)
      }
    }
  }
}

@Composable
fun EventList(events: List<Event>) {
  LazyColumn { items(events) { event -> EventCard(event) } }
}

@Composable
fun EventCard(event: Event) {
  Card(
      modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).height(IntrinsicSize.Min),
      colors = cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = cardElevation(defaultElevation = 4.dp)) {
        Row(modifier = Modifier.fillMaxSize()) {
          Column(
              modifier =
                  Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 8.dp)
                      .weight(1f),
              verticalArrangement = Arrangement.Center) {
                Text(
                    text = event.name,
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

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
  HomeScreen()
}
