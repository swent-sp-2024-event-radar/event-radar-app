package com.github.se.eventradar.ui.component

import android.widget.Toast
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.eventradar.R
import com.github.se.eventradar.model.event.Event
import com.github.se.eventradar.model.event.EventCategory
import com.github.se.eventradar.viewmodel.EventsOverviewUiState

fun getIconFromViewListBool(viewList: Boolean): ImageVector {
  if (viewList) return Icons.Default.Place else return Icons.AutoMirrored.Filled.List
}

@Composable
fun ViewToggleFab(modifier: Modifier = Modifier, onClick: () -> Unit, iconVector: ImageVector) {
  FloatingActionButton(onClick = { onClick() }, modifier = modifier) {
    Icon(imageVector = iconVector, contentDescription = "Icon")
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

@Composable
fun SearchBarAndFilter(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchButtonClick: () -> Unit,
    showFilterPopUp: Boolean,
    setShowFilterPopUp: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
  Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
    // Search bar
    TextField(
        value = searchQuery,
        // function onSearchQueryChange() needs to be created in the VM
        onValueChange = { onSearchQueryChange(it) },
        modifier = Modifier.weight(1f),
        maxLines = 1,
        shape = RoundedCornerShape(32.dp),
        colors =
            TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent),
        placeholder = { Text("Search event...") },
        trailingIcon = {
          IconButton(
              onClick = onSearchButtonClick, modifier = Modifier.padding(horizontal = 4.dp)) {
                Icon(Icons.Default.Search, contentDescription = null)
              }
        })

    // Filter button
    Button(
        // function setShowFilterPopUp() needs to be created in the VM
        onClick = { setShowFilterPopUp(!showFilterPopUp) },
        modifier = Modifier.padding(start = 8.dp)) {
          Text("Filter")
        }
  }
}

@Composable
fun FilterPopUp(
    onRadiusChange: (Double) -> Unit,
    onFreeSelectionChange: (Boolean) -> Unit,
    onCategorySelectionChange: (List<EventCategory>) -> Unit,
    modifier: Modifier = Modifier,
    uiState: EventsOverviewUiState = EventsOverviewUiState()
) {
  Box(modifier = modifier) {
    Card(
        modifier = Modifier.padding(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
      Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        // Text input for radius
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start) {
              Text(
                  text = "Radius: ",
                  style =
                      TextStyle(
                          fontSize = 16.sp,
                      ))
              TextField(
                  value = uiState.radiusInputFilter.toString(),
                  onValueChange = { input ->
                    uiState.radiusInputFilter = input.toDoubleOrNull() ?: uiState.radiusInputFilter
                  },
                  keyboardOptions =
                      KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                  modifier = Modifier.width(70.dp).height(10.dp),
              )
              Text(
                  text = " km",
                  modifier = Modifier.weight(1f),
                  style =
                      TextStyle(
                          fontSize = 16.sp,
                      ))
            }

        Spacer(modifier = Modifier.height(12.dp))

        // Slider for free selection
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start) {
              Text(
                  text = "Free:  ",
                  style =
                      TextStyle(
                          fontSize = 16.sp,
                      ))
              Switch(
                  checked = uiState.freeEventsFilter,
                  onCheckedChange = { uiState.freeEventsFilter = it },
              )
            }

        Spacer(modifier = Modifier.height(4.dp))

        // Buttons for category selection
        Text(
            text = "Category: ",
            modifier = Modifier.weight(1f),
            style =
                TextStyle(
                    fontSize = 16.sp,
                ))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start) {
              CategorySelection()
            }

        Spacer(modifier = Modifier.height(4.dp))

        // Button to apply filter
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
          Button(
              onClick = {
                // Apply filter
                onRadiusChange(uiState.radiusInputFilter)
                onFreeSelectionChange(uiState.freeEventsFilter)
                onCategorySelectionChange(uiState.categorySelectionFilter)
                onCategorySelectionChange(uiState.categorySelectionFilter)
              }) {
                Text("Apply")
              }
        }
      }
    }
  }
}

@Composable
fun CategorySelection() {
  LazyColumn {
    items(EventCategory.entries) { category ->
      var isChecked by remember { mutableStateOf(false) }
      Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Start,
          modifier = Modifier.padding(vertical = 0.dp)) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = { isChecked = it },
                modifier = Modifier.scale(0.6f).size(10.dp).padding(start = 10.dp))
            Text(
                text = category.displayName,
                style =
                    TextStyle(
                        fontSize = 16.sp,
                    ),
                modifier = Modifier.padding(start = 16.dp))
          }
    }
  }
}