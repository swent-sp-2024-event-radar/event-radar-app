package com.github.se.eventradar.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.github.se.eventradar.R
import com.github.se.eventradar.model.event.Event
import com.github.se.eventradar.model.event.EventCategory
import com.github.se.eventradar.ui.BottomNavigationMenu
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.ui.navigation.Route
import com.github.se.eventradar.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.eventradar.ui.navigation.getTopLevelDestination
import com.github.se.eventradar.viewmodel.EventsOverviewUiState

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
  Card(
      modifier =
          Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
              .height(IntrinsicSize.Min)
              .testTag("eventCard"),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
      onClick = { onCardClick(event.fireBaseID) }) {
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
                        ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis)
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
    onSearchQueryChanged: (String) -> Unit,
    searchQuery: String,
    onSearchActiveChanged: (Boolean) -> Unit,
    onFilterDialogOpen: () -> Unit,
    modifier: Modifier = Modifier,
    placeholderStringResource: Int = R.string.search_placeholder,
) {
  Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
    SearchBarField(
        searchQuery,
        onSearchQueryChanged,
        onSearchActiveChanged,
        Modifier.weight(1f),
        placeholderStringResource)

    // Filter button
    Button(
        onClick = { onFilterDialogOpen() },
        modifier = Modifier.padding(start = 8.dp).testTag("filterButton")) {
          Text(stringResource(id = R.string.filter))
        }
  }
}

@Composable
fun SearchBarField(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onSearchActiveChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    placeholderStringResource: Int = R.string.search_placeholder,
) {
  // Search bar
  TextField(
      value = searchQuery,
      onValueChange = {
        onSearchQueryChanged(it)
        if (it == "") onSearchActiveChanged(false) else onSearchActiveChanged(true)
      },
      modifier = modifier.fillMaxWidth().testTag("searchBar"),
      maxLines = 1,
      shape = RoundedCornerShape(32.dp),
      colors =
          TextFieldDefaults.colors(
              focusedIndicatorColor = Color.Transparent,
              unfocusedIndicatorColor = Color.Transparent,
              disabledIndicatorColor = Color.Transparent),
      placeholder = { Text(stringResource(id = placeholderStringResource)) },
      trailingIcon = { Icon(Icons.Default.Search, contentDescription = null) })
}

@Composable
fun FilterPopUp(
    onFreeSwitchChanged: () -> Unit,
    onFilterApply: () -> Unit,
    uiState: EventsOverviewUiState,
    onRadiusQueryChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
  Box(modifier = modifier) {
    Card(
        modifier = Modifier.padding(12.dp).testTag("filterCard"),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
      Column(modifier = Modifier.padding(12.dp).testTag("filterCardColumn")) {
        // Text input for radius
        Row(
            modifier = Modifier.wrapContentWidth().testTag("filterCardColumnRow"),
            verticalAlignment = Alignment.CenterVertically) {
              Row(modifier = Modifier.weight(1f)) {
                Box(modifier = Modifier.testTag("filterCardColumnRowRadius")) {
                  Text(
                      text = stringResource(id = R.string.radius_label),
                      style = TextStyle(fontSize = 16.sp),
                      modifier = Modifier.testTag("radiusLabel"))
                }
                Box(modifier = Modifier.testTag("radiusInputBox")) {
                  BasicTextField(
                      value = uiState.radiusQuery,
                      onValueChange = { onRadiusQueryChanged(it) },
                      keyboardOptions =
                          KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                      modifier =
                          Modifier.widthIn(max = 36.dp)
                              .border(
                                  width = 1.dp,
                                  color = MaterialTheme.colorScheme.onPrimaryContainer,
                                  shape = RoundedCornerShape(10))
                              .testTag("radiusInput"),
                      textStyle = TextStyle.Default.copy(fontSize = 16.sp),
                      singleLine = true)
                }
                Box(modifier = Modifier.testTag("filterCardColumnRowKm")) {
                  Text(
                      text = stringResource(id = R.string.radius_km_label),
                      style = TextStyle(fontSize = 16.sp),
                      modifier = Modifier.testTag("kmLabel"))
                }
              }
              // Slider for free selection
              Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.weight(1.5f).testTag("freeSwitchRow")) {
                    Text(
                        text = stringResource(id = R.string.free_events_label),
                        style =
                            TextStyle(
                                fontSize = 16.sp,
                            ),
                        modifier = Modifier.testTag("freeSwitchLabel"))
                    Switch(
                        checked = uiState.isFreeSwitchOn,
                        onCheckedChange = { onFreeSwitchChanged() },
                        modifier = Modifier.widthIn(32.dp).testTag("freeSwitch"))
                  }
            }

        // Buttons for category selection
        Text(
            text = stringResource(id = R.string.category_label),
            modifier = Modifier.testTag("categoryLabel"),
            style =
                TextStyle(
                    fontSize = 16.sp,
                ))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.testTag("categoryRow"),
        ) {
          CategorySelection(
              uiState = uiState,
              modifier = Modifier.testTag("categoryOptionsColumn"),
          )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Button to apply filter
        Row(
            modifier = Modifier.testTag("filterApplyRow"),
            horizontalArrangement = Arrangement.Center) {
              Button(
                  onClick = { onFilterApply() }, modifier = Modifier.testTag("filterApplyButton")) {
                    Text(stringResource(id = R.string.filter_apply))
                  }
            }
      }
    }
  }
}

@Composable
fun CategorySelection(uiState: EventsOverviewUiState, modifier: Modifier) {
  LazyColumn(modifier = modifier) {
    items(EventCategory.entries.subList(0, EventCategory.entries.size / 2 + 1)) { category ->
      CategoryDisplayColumn(category, uiState)
    }
  }
  LazyColumn(modifier = modifier) {
    items(
        EventCategory.entries.subList(
            EventCategory.entries.size / 2 + 1, EventCategory.entries.size)) { category ->
          CategoryDisplayColumn(category, uiState)
        }
  }
}

@Composable
fun CategoryDisplayColumn(
    category: EventCategory,
    uiState: EventsOverviewUiState,
) {
  var isChecked by remember { mutableStateOf(uiState.categoriesCheckedList.contains(category)) }
  Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Start,
      modifier =
          Modifier.padding(vertical = 0.dp).testTag("categoryOptionRow-${category.displayName}")) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = {
              isChecked = it
              if (isChecked) {
                uiState.categoriesCheckedList.add(category)
              } else {
                uiState.categoriesCheckedList.remove(category)
              }
            },
            modifier = Modifier.scale(0.6f).size(10.dp).padding(start = 10.dp).testTag("checkbox"))
        Text(
            text = category.displayName,
            style =
                TextStyle(
                    fontSize = 16.sp,
                ),
            modifier = Modifier.padding(start = 16.dp).testTag("checkboxText"))
      }
}

@Composable
fun GoBackButton(modifier: Modifier, goBack: () -> Unit) {
  Button(
      onClick = { goBack() },
      modifier = modifier.testTag("goBackButton"),
      colors =
          ButtonDefaults.buttonColors(
              contentColor = Color.Transparent,
              containerColor = Color.Transparent,
          ),
  ) {
    Icon(
        painter = painterResource(id = R.drawable.back_arrow),
        contentDescription = "Back navigation arrow",
        tint = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.width(24.dp).height(24.dp).align(Alignment.CenterVertically))
  }
}

@Composable
fun ProfilePic(
    profilePicUrl: String,
    firstName: String,
    lastName: String,
    modifier: Modifier = Modifier
) {
  AsyncImage(
      model = ImageRequest.Builder(LocalContext.current).data(profilePicUrl).build(),
      placeholder = painterResource(id = R.drawable.placeholder),
      contentDescription = "Profile picture of $firstName $lastName",
      contentScale = ContentScale.Crop,
      modifier = modifier.padding(start = 16.dp).clip(CircleShape).size(56.dp))
}

@Composable
fun AppScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {
      Logo(modifier = Modifier.fillMaxWidth().padding(top = 32.dp, start = 16.dp).testTag("logo"))
    },
    floatingActionButton: @Composable () -> Unit,
    navigationActions: NavigationActions,
    selectedItem: String = Route.HOME,
    content: @Composable (PaddingValues) -> Unit,
) {
  Scaffold(
      modifier = modifier,
      topBar = topBar,
      bottomBar = {
        BottomNavigationMenu(
            onTabSelected = navigationActions::navigateTo,
            tabList = TOP_LEVEL_DESTINATIONS,
            selectedItem = getTopLevelDestination(selectedItem),
            modifier = Modifier.testTag("bottomNavMenu"))
      },
      floatingActionButton = floatingActionButton,
  ) {
    content(it)
  }
}

@Composable
fun GenericDialogBox(
    openDialog: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    title: String,
    message: String,
    onClickConfirmButton: () -> Unit = {},
    boxIcon: @Composable (() -> Unit)?,
) {
  val display by openDialog
  if (display) {
    AlertDialog(
        icon = boxIcon,
        text = {
          Text(
              text = message,
              textAlign = TextAlign.Center,
              modifier = Modifier.testTag("ErrorDisplayText"))
        },
        title = {
          Text(
              text = title,
              modifier = Modifier.testTag("ErrorTitle"),
          )
        },
        onDismissRequest = { openDialog.value = false },
        confirmButton = {
          TextButton(
              onClick = {
                openDialog.value = false
                onClickConfirmButton()
              },
              modifier = Modifier.testTag("dialogConfirmButton")) {
                Text("Ok")
              }
        },
        modifier = modifier)
  }
}
