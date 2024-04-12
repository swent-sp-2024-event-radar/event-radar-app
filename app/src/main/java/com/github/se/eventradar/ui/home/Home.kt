package com.github.se.eventradar.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.style.TextAlign
import com.github.se.eventradar.R
import com.github.se.eventradar.model.event.Event
import com.github.se.eventradar.model.event.EventCategory
import java.time.LocalDate
import java.time.LocalTime


@Composable
fun HomeScreen() {
    val mockEvents = listOf(
        Event("1", "User1", "Jazz Concert", "An evening with smooth jazz.",
            LocalDate.of(2024, 4, 12), LocalTime.of(19, 30), EventCategory.MUSIC),
        Event("2", "User2", "Soccer Game", "Cheer for your local soccer team!",
            LocalDate.of(2024, 4, 18), LocalTime.of(15, 45), EventCategory.SPORTS),
        Event("3", "User3", "Tech Conference", "The latest in tech innovation.",
            LocalDate.of(2024, 5, 24), LocalTime.of(10, 0), EventCategory.CONFERENCE)
    )

    var selectedTabIndex by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(50.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.event_logo),
                contentDescription = "Event Radar Logo",
                modifier = Modifier
                    .size(width = 184.35.dp, height = 46.99.dp)
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.padding(horizontal = 16.dp),
            backgroundColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
            ) {
                Text("Browse", textAlign = TextAlign.Center)
            }
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 }
            ) {
                Text("Upcoming", textAlign = TextAlign.Center)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (selectedTabIndex == 0) {
            EventList(mockEvents)
        }
    }
}

@Composable
fun EventList(events: List<Event>) {
    LazyColumn {
        items(events) { event ->
            EventCard(event)
        }
    }
}

@Composable
fun EventCard(event: Event) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = event.name)
            Text(text = event.description)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
