package com.github.se.eventradar.ui.home

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
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.github.se.eventradar.R
import com.github.se.eventradar.model.event.Event
import com.github.se.eventradar.model.event.EventCategory
import com.github.se.eventradar.model.repository.event.MockEventRepository
import com.github.se.eventradar.model.repository.user.MockUserRepository
import com.github.se.eventradar.ui.BottomNavigationMenu
import com.github.se.eventradar.ui.component.EventList
import com.github.se.eventradar.ui.component.ViewToggleFab
import com.github.se.eventradar.ui.map.EventMap
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.ui.navigation.Route
import com.github.se.eventradar.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.eventradar.ui.navigation.getTopLevelDestination
import com.github.se.eventradar.viewmodel.EventsOverviewUiState
import com.github.se.eventradar.viewmodel.EventsOverviewViewModel
import com.github.se.eventradar.viewmodel.Tab

@Composable
fun HomeScreen(
    viewModel: EventsOverviewViewModel = hiltViewModel(),
    navigationActions: NavigationActions
) {

  // Ui States handled by viewModel
  val uiState by viewModel.uiState.collectAsState()
  var selectedTabIndex by remember { mutableIntStateOf(if (uiState.tab == Tab.BROWSE) 0 else 1) }
  var viewToggleIcon by remember {
    mutableStateOf(if (uiState.viewList) Icons.Default.Place else Icons.AutoMirrored.Filled.List)
  }
  LaunchedEffect(key1 = uiState.eventList) { viewModel.getEvents() }
  val context = LocalContext.current

  ConstraintLayout(modifier = Modifier.fillMaxSize().testTag("homeScreen")) {
    val (logo, tabs, searchAndFilter, filterPopUp, eventList, eventMap, bottomNav, viewToggle) =
        createRefs()
    Row(
        modifier =
            Modifier.fillMaxWidth()
                .constrainAs(logo) {
                  top.linkTo(parent.top, margin = 32.dp)
                  start.linkTo(parent.start, margin = 16.dp)
                }
                .testTag("logo"),
        verticalAlignment = Alignment.CenterVertically) {
          Image(
              painter = painterResource(id = R.drawable.event_logo),
              contentDescription = "Event Radar Logo",
              modifier = Modifier.size(width = 186.dp, height = 50.dp))
        }

    TabRow(
        selectedTabIndex = selectedTabIndex,
        modifier =
            Modifier.fillMaxWidth()
                .padding(top = 8.dp)
                .constrainAs(tabs) {
                  top.linkTo(logo.bottom, margin = 16.dp)
                  start.linkTo(parent.start)
                  end.linkTo(parent.end)
                }
                .testTag("tabs"),
        contentColor = MaterialTheme.colorScheme.primary) {
          Tab(
              selected = selectedTabIndex == 0,
              onClick = {
                uiState.tab = Tab.BROWSE
                selectedTabIndex = if (uiState.tab == Tab.BROWSE) 0 else 1
              },
              modifier = Modifier.testTag("browseTab"),
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
                    ),
                modifier = Modifier.padding(bottom = 8.dp))
          }
          Tab(
              selected = selectedTabIndex == 1,
              onClick = {
                uiState.tab = Tab.UPCOMING_EVENTS
                selectedTabIndex = if (uiState.tab == Tab.BROWSE) 0 else 1
              },
              modifier = Modifier.testTag("upcomingTab")) {
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
                        ),
                    modifier = Modifier.padding(bottom = 8.dp))
              }
        }

    SearchBarAndFilter(
        searchQuery = uiState.searchQuery,
        onSearchQueryChange = { uiState.searchQuery = it },
        onSearchButtonClick = {
          // Handle search button click
        },
        showFilterPopUp = uiState.isFilterDialogOpen,
        setShowFilterPopUp = { uiState.isFilterDialogOpen = it },
        modifier =
            Modifier.padding(horizontal = 16.dp, vertical = 8.dp).constrainAs(searchAndFilter) {
              top.linkTo(tabs.bottom, margin = 8.dp)
              start.linkTo(parent.start)
              end.linkTo(parent.end)
            })

    if (selectedTabIndex == 0) {
      if (uiState.viewList) {
        EventList(
            uiState.eventList.allEvents,
            Modifier.fillMaxWidth().constrainAs(eventList) {
              top.linkTo(tabs.bottom, margin = 8.dp)
              start.linkTo(parent.start)
              end.linkTo(parent.end)
            })
      } else {
        EventMap(
            uiState.eventList.allEvents,
            navigationActions,
            Modifier.testTag("map").fillMaxWidth().constrainAs(eventMap) {
              top.linkTo(tabs.bottom, margin = 8.dp)
              start.linkTo(parent.start)
              end.linkTo(parent.end)
            })
      }
    } else {
      // "Upcoming" tab content
      // TODO: Implement upcoming events
      Toast.makeText(context, "Upcoming events not yet available", Toast.LENGTH_SHORT).show()
      uiState.tab = Tab.BROWSE
      selectedTabIndex = if (uiState.tab == Tab.BROWSE) 0 else 1
    }
    // Note for now, the filter dialog is always open to verify the UI
    if (!uiState.isFilterDialogOpen) {
      FilterPopUp(
          onRadiusChange = { radius ->
            // Handle radius change
          },
          onFreeSelectionChange = { isFree ->
            // Handle free selection change
          },
          onCategorySelectionChange = { selectedCategories ->
            // Handle category selection change
          },
          modifier =
              Modifier.height(320.dp).width(230.dp).constrainAs(filterPopUp) {
                top.linkTo(searchAndFilter.bottom)
                end.linkTo(parent.end)
              })
    }

    BottomNavigationMenu(
        onTabSelected = { tab -> navigationActions.navigateTo(tab) },
        tabList = TOP_LEVEL_DESTINATIONS,
        selectedItem = getTopLevelDestination(Route.HOME),
        modifier =
            Modifier.testTag("bottomNavMenu").constrainAs(bottomNav) {
              bottom.linkTo(parent.bottom)
              start.linkTo(parent.start)
              end.linkTo(parent.end)
            })
    ViewToggleFab(
        modifier =
            Modifier.padding(16.dp).testTag("viewToggleFab").constrainAs(viewToggle) {
              bottom.linkTo(bottomNav.top)
              absoluteRight.linkTo(parent.absoluteRight)
            },
        onClick = {
          uiState.viewList = !uiState.viewList
          viewToggleIcon =
              if (uiState.viewList) Icons.Default.Place else Icons.AutoMirrored.Filled.List
        },
        iconVector = viewToggleIcon)
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
        elevation = cardElevation(defaultElevation = 4.dp),
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
      colors = cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = cardElevation(defaultElevation = 4.dp),
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
  val mockEventRepo = MockEventRepository()
  val mockUserRepo = MockUserRepository()
  HomeScreen(
      EventsOverviewViewModel(mockEventRepo, mockUserRepo),
      NavigationActions(rememberNavController()))
}
