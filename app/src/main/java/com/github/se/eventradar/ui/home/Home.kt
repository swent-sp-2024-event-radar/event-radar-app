package com.github.se.eventradar.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

  LaunchedEffect(Unit) { viewModel.getEvents() }

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
        selectedTabIndex = getTabIndexFromTabEnum(uiState.tab),
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
              selected = getTabIndexFromTabEnum(uiState.tab) == 0,
              onClick = {
                viewModel.onTabChanged(Tab.BROWSE)
                viewModel.getEvents()
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
              selected = getTabIndexFromTabEnum(uiState.tab) == 1,
              onClick = {
                viewModel.onTabChanged(Tab.UPCOMING)
                viewModel.getUpcomingEvents()
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
        onSearchQueryChange = { newQuery -> viewModel.onSearchQueryChange(newQuery) },
        onSearchButtonClick = {
          // Handle search button click
        },
        showFilterPopUp = uiState.isFilterDialogOpen,
        setShowFilterPopUp = { viewModel.changeFilterDialogOpen() },
        modifier =
            Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("searchBarAndFilter")
                .constrainAs(searchAndFilter) {
                  top.linkTo(tabs.bottom, margin = 8.dp)
                  start.linkTo(parent.start)
                  end.linkTo(parent.end)
                })

    if (getTabIndexFromTabEnum(uiState.tab) == 0) {
      if (uiState.viewList) {
        EventList(
            uiState.eventList.allEvents,
            Modifier.testTag("eventList").fillMaxWidth().constrainAs(eventList) {
              top.linkTo(searchAndFilter.bottom, margin = 8.dp)
              start.linkTo(parent.start)
              end.linkTo(parent.end)
            }) { eventId ->
              navigationActions.navController.navigate("${Route.EVENT_DETAILS}/${eventId}")
            }
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
      if (uiState.eventList.allEvents.isEmpty() && uiState.userLoggedIn) {
        Text(
            "You have no upcoming events",
            modifier =
                Modifier.testTag("noUpcomingEventsText").constrainAs(eventList) {
                  top.linkTo(searchAndFilter.bottom, margin = 8.dp)
                  start.linkTo(parent.start)
                  end.linkTo(parent.end)
                })
      } else if (!uiState.userLoggedIn) {
        Text(
            "Please log in",
            modifier =
                Modifier.testTag("pleaseLogInText").constrainAs(eventList) {
                  top.linkTo(searchAndFilter.bottom, margin = 8.dp)
                  start.linkTo(parent.start)
                  end.linkTo(parent.end)
                })
      } else {
        if (uiState.viewList) {
          EventList(
              events = uiState.eventList.allEvents,
              modifier =
                  Modifier.testTag("eventList").fillMaxWidth().constrainAs(eventList) {
                    top.linkTo(searchAndFilter.bottom, margin = 8.dp)
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
    }
    // Note for now, the filter dialog is always open to verify the UI
    if (!uiState.isFilterDialogOpen) {
      FilterPopUp(
          onRadiusChange = {
            // Handle radius change
          },
          onFreeSelectionChange = {
            // Handle free selection change
          },
          onCategorySelectionChange = {
            // Handle category selection change
          },
          modifier =
              Modifier.height(320.dp).width(230.dp).testTag("filterPopUp").constrainAs(
                  filterPopUp) {
                    top.linkTo(searchAndFilter.bottom)
                    end.linkTo(parent.end)
                  })
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
            Modifier.testTag("bottomNavMenu").constrainAs(bottomNav) {
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
