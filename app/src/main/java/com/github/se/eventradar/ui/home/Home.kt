package com.github.se.eventradar.ui.home

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.material3.CardDefaults.shape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldDefaults.shape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.github.se.eventradar.ExcludeFromJacocoGeneratedReport
import com.github.se.eventradar.R
import com.github.se.eventradar.model.repository.event.MockEventRepository
import com.github.se.eventradar.model.repository.user.MockUserRepository
import com.github.se.eventradar.ui.BottomNavigationMenu
import com.github.se.eventradar.ui.component.EventList
import com.github.se.eventradar.ui.component.FilterPopUp
import com.github.se.eventradar.ui.component.SearchBarAndFilter
import com.github.se.eventradar.ui.component.ViewToggleFab
import com.github.se.eventradar.ui.component.getIconFromViewListBool
import com.github.se.eventradar.ui.map.EventMap
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.ui.navigation.Route
import com.github.se.eventradar.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.eventradar.ui.navigation.getTopLevelDestination
import com.github.se.eventradar.viewmodel.EventsOverviewViewModel
import com.github.se.eventradar.viewmodel.Tab

@Composable
fun HomeScreen(
    viewModel: EventsOverviewViewModel = hiltViewModel(),
    navigationActions: NavigationActions
) {
  // Ui States handled by viewModel
  val uiState by viewModel.uiState.collectAsState()
  LaunchedEffect(key1 = uiState.eventList, key2 = uiState.isSearchActive, key3 = uiState.isFilterActive) {
      if (uiState.isSearchActive || uiState.isFilterActive) {
          viewModel.filterEvents()
      } else {
          viewModel.getEvents()
      }
  }
  val context = LocalContext.current

  ConstraintLayout(modifier = Modifier
      .fillMaxSize()
      .testTag("homeScreen")) {
    val (logo, tabs, searchAndFilter, filterPopUp, eventList, eventMap, bottomNav, viewToggle) =
        createRefs()
    Row(
        modifier =
        Modifier
            .fillMaxWidth()
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
        selectedTabIndex = getTabIndexFromTabEnum(uiState.tab),
        modifier =
        Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .constrainAs(tabs) {
                top.linkTo(logo.bottom, margin = 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
            .testTag("tabs"),
        contentColor = MaterialTheme.colorScheme.primary) {
          Tab(
              selected = getTabIndexFromTabEnum(uiState.tab) == 0,
              onClick = { viewModel.onTabChanged(Tab.BROWSE) },
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
              selected = getTabIndexFromTabEnum(uiState.tab) == 1,
              onClick = { viewModel.onTabChanged(Tab.UPCOMING) },
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
        onSearchQueryChanged = { viewModel.onSearchQueryChanged(it) },
        uiState = uiState,
        onSearchActiveChanged = { viewModel.onSearchActiveChanged(it) },
        onFilterDialogOpen = { viewModel.onFilterDialogOpen() },
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("searchBarAndFilter")
            .constrainAs(searchAndFilter) {
                top.linkTo(tabs.bottom, margin = 8.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            })

    if (getTabIndexFromTabEnum(uiState.tab) == 0) {
      if (uiState.viewList) {
          if (uiState.isSearchActive || uiState.isFilterActive) {
              Log.d("HomeScreen", "Filtered events: ${uiState.eventList.filteredEvents}")
              EventList(
                  uiState.eventList.filteredEvents,
                  Modifier.testTag("eventList").fillMaxWidth().constrainAs(eventList) {
                      top.linkTo(searchAndFilter.bottom, margin = 8.dp)
                      start.linkTo(parent.start)
                      end.linkTo(parent.end)
                  }) { eventId ->
                  navigationActions.navController.navigate("${Route.EVENT_DETAILS}/${eventId}")
              }
          } else {
              Log.d("HomeScreen", "All events: ${uiState.eventList.allEvents}")

              EventList(
                  uiState.eventList.allEvents,
                  Modifier.testTag("eventList").fillMaxWidth().constrainAs(eventList) {
                      top.linkTo(searchAndFilter.bottom, margin = 8.dp)
                      start.linkTo(parent.start)
                      end.linkTo(parent.end)
                  }) { eventId ->
                  navigationActions.navController.navigate("${Route.EVENT_DETAILS}/${eventId}")
              }
          }
      } else {
        if (uiState.isSearchActive || uiState.isFilterActive) {
            EventMap(
                uiState.eventList.filteredEvents,
                navigationActions,
                Modifier.testTag("map").fillMaxWidth().constrainAs(eventMap) {
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
      }
    } else {
      // "Upcoming" tab content
      // TODO: Implement upcoming events
      Toast.makeText(context, "Upcoming events not yet available", Toast.LENGTH_SHORT).show()
      viewModel.onTabChanged(Tab.BROWSE)
    }

    if (uiState.isFilterDialogOpen) {
      FilterPopUp(
          onFreeSwitchChanged = { viewModel.onFreeSwitchChanged() },
          onFilterApply = { viewModel.onFilterApply() },
          uiState = uiState,
          onRadiusQueryChanged = { viewModel.onRadiusQueryChanged(it) },
          modifier = Modifier
              .height(320.dp)
              .width(230.dp)
              .testTag("filterPopUp")
              .constrainAs(filterPopUp) {
                  top.linkTo(searchAndFilter.bottom)
                  end.linkTo(parent.end)
              },
      )
    }

    ViewToggleFab(
        modifier =
            Modifier.padding(16.dp).testTag("viewToggleFab").constrainAs(viewToggle) {
              bottom.linkTo(bottomNav.top)
              absoluteRight.linkTo(parent.absoluteRight)
            },
        onClick = { viewModel.onViewListStatusChanged() },
        iconVector = getIconFromViewListBool(uiState.viewList))

    BottomNavigationMenu(
        onTabSelected = { tab -> navigationActions.navigateTo(tab) },
        tabList = TOP_LEVEL_DESTINATIONS,
        selectedItem = getTopLevelDestination(Route.HOME),
        modifier =
        Modifier
            .testTag("bottomNavMenu")
            .constrainAs(bottomNav) {
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            })
  }
}

fun getTabIndexFromTabEnum(tab: Tab): Int {
  val selectedTabIndex =
      if (tab == Tab.BROWSE) {
        0
      } else {
        1
      }
  return selectedTabIndex
}

@Preview(showBackground = true, showSystemUi = true)
@ExcludeFromJacocoGeneratedReport
@Composable
fun HomeScreenPreview() {
  val mockEventRepo = MockEventRepository()
  val mockUserRepo = MockUserRepository()
  HomeScreen(
      EventsOverviewViewModel(mockEventRepo, mockUserRepo),
      NavigationActions(rememberNavController()))
}
