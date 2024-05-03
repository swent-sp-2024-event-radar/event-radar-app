package com.github.se.eventradar.ui.component

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.github.se.eventradar.viewmodel.EventsOverviewViewModel

fun getIconFromViewListBool(viewList: Boolean): ImageVector {
  return if (viewList) {
    Icons.Default.Place
  } else {
    Icons.AutoMirrored.Filled.List
  }
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
fun EventList(events: List<Event>, modifier: Modifier = Modifier, onCardClick: (String) -> Unit) {
  LazyColumn(modifier = modifier) { items(events) { event -> EventCard(event, onCardClick) } }
}

@Composable
fun EventCard(event: Event, onCardClick: (String) -> Unit) {
  // val context = LocalContext.current
  Card(
      modifier =
      Modifier
          .padding(horizontal = 16.dp, vertical = 8.dp)
          .height(IntrinsicSize.Min)
          .testTag("eventCard"),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
      onClick = {
        onCardClick(event.fireBaseID)
        // Toast.makeText(context, "Event details not yet available", Toast.LENGTH_SHORT).show()
      }) {
        Row(modifier = Modifier.fillMaxSize()) {
          Column(
              modifier =
              Modifier
                  .padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 8.dp)
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
              modifier = Modifier
                  .weight(0.3f)
                  .fillMaxHeight())
        }
      }
}

@Composable
fun SearchBarAndFilter(
    onSearchQueryChanged: (String) -> Unit,
    uiState: EventsOverviewUiState,
    onSearchActiveChanged: (Boolean) -> Unit,
    onFilterDialogOpen: () -> Unit,
    modifier: Modifier = Modifier,
) {
  Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
    // Search bar
    TextField(
        value = uiState.searchQuery,
        onValueChange = {
          onSearchQueryChanged(it)
          if (it == "") onSearchActiveChanged(false) else onSearchActiveChanged(true)
        },
        modifier = Modifier
            .weight(1f)
            .testTag("searchBar"),
        maxLines = 1,
        shape = RoundedCornerShape(32.dp),
        colors =
            TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent),
        placeholder = { Text(stringResource(id = R.string.home_search_placeholder)) },
        trailingIcon = { Icon(Icons.Default.Search, contentDescription = null) })

    // Filter button
    Button(
        onClick = { onFilterDialogOpen() },
        modifier = Modifier
            .padding(start = 8.dp)
            .testTag("filterButton")) {
          Text(stringResource(id = R.string.filter))
        }
  }
}

@Composable
fun FilterPopUp(
    onFreeSwitchChanged: () -> Unit,
    onFilterApply: () -> Unit,
    uiState: EventsOverviewUiState,
    onRadiusQueryChanged: (String) -> Unit,
    onCategorySelectionChanged: (EventCategory) -> Unit,
    viewModel: EventsOverviewViewModel,
    modifier: Modifier = Modifier,
) {
  Box(modifier = modifier) {
    Card(
        modifier = Modifier.padding(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
      Column(modifier = Modifier
          .fillMaxSize()
          .padding(12.dp)) {
        // Text input for radius
        Row(modifier = Modifier.fillMaxWidth()) {
          Box(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(id = R.string.radius_label),
                style = TextStyle(fontSize = 16.sp))
          }
          Box(modifier = Modifier.weight(1f)) {
            BasicTextField(
                value = uiState.radiusQuery,
                onValueChange = { onRadiusQueryChanged(it) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier =
                Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        shape = RoundedCornerShape(10)
                    )
                    .testTag("radiusInput"),
                textStyle = TextStyle.Default.copy(fontSize = 16.sp),
                singleLine = true)
          }
          Box(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(id = R.string.radius_km_label),
                style = TextStyle(fontSize = 16.sp))
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Slider for free selection
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start) {
              Text(
                  text = stringResource(id = R.string.free_events_label),
                  style =
                      TextStyle(
                          fontSize = 16.sp,
                      ))
              Switch(
                  checked = uiState.isFreeSwitchOn,
                  onCheckedChange = { onFreeSwitchChanged() },
                  modifier = Modifier.testTag("freeSwitch")
              )
            }

        // Buttons for category selection
        Text(
            text = stringResource(id = R.string.category_label),
            modifier = Modifier.weight(1f),
            style =
                TextStyle(
                    fontSize = 16.sp,
                ))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start) {
              CategorySelection(
                  modifyCategoryChecked = { category, isChecked ->
                      viewModel.modifyCategoryChecked(category, isChecked)
                  },
                  onCategorySelectionChanged = { onCategorySelectionChanged(it) },
                  uiState = uiState)
            }

        Spacer(modifier = Modifier.height(4.dp))

        // Button to apply filter
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
          Button(
              onClick = { onFilterApply() },
              modifier =  Modifier.testTag("filterApplyButton")
          ) {
              Text(stringResource(id = R.string.filter_apply))
        }
        }
      }
    }
  }
}

@Composable
fun CategorySelection(
    modifyCategoryChecked: (EventCategory, Boolean) -> Unit,
    onCategorySelectionChanged: (EventCategory) -> Unit,
    uiState: EventsOverviewUiState,
) {
    LaunchedEffect(Unit) {
        Log.d("MyComposable", "Composable recomposed")
    }
  LazyColumn {
    items(EventCategory.entries) { category ->
      Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Start,
          modifier = Modifier.padding(vertical = 0.dp)) {
            Checkbox(
                checked = uiState.isCategoriesChecked[category]!!,
                onCheckedChange = {
                    Log.d("modifyCategoryChecked", "checked ui b4 ${uiState.isCategoriesChecked[category]}")
                    modifyCategoryChecked(category, !uiState.isCategoriesChecked[category]!!)
                    Log.d("modifyCategoryChecked", "isCategoriesChecked ui ${uiState.isCategoriesChecked}")
                    onCategorySelectionChanged(category)
                    Log.d("CategorySelection", "Category ui ${uiState.categoriesCheckedList}")
                    Log.d("modifyCategoryChecked", "checked ui af ${uiState.isCategoriesChecked[category]}")
                    Log.d("modifyCategoryChecked", "isCategoriesChecked ui af ${uiState.isCategoriesChecked[category]!!}")
                                  },
                modifier = Modifier
                    .scale(0.6f)
                    .size(10.dp)
                    .padding(start = 10.dp))
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
