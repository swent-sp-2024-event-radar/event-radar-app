package com.github.se.eventradar.ui.event

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.github.se.eventradar.R
import com.github.se.eventradar.ui.BottomNavigationMenu
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.ui.navigation.TOP_LEVEL_DESTINATIONS
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Temporary sizes. Needs to be responsive...
private val widthPadding = 34.dp
private val imageHeight = 191.dp
private val titleTextSize = 32.sp
private val contentTextSize = 14.sp

// Temporary text field values to be replaced with some ViewModel.stateVar.collectAsState()
val eventImage = R.drawable.ic_launcher_background
val eventTitle = "Event Title"
val headerDescription = "Description"
val headerDistance = "Distance from you"
val headerDate = "Date"
val headerCategory = "Category"
val headerTime = "Time"
val contentDescription =
    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc et ornare  dui. Integer convallis purus odio, vitae mattis erat ultricies non.  Donec et magna hendrerit, molestie lorem vel, facilisis augue. "
val contentDistance = "xx km"
val contentDate = "dd/mm/yyyy"
val contentCategory = "Cat x"
val contentTime = "xx:xx"

val titleTextStyle =
    TextStyle(
        fontSize = titleTextSize,
        fontFamily = FontFamily(Font(R.font.roboto)),
        fontWeight = FontWeight.Bold,
        lineHeight = 20.sp,
    )

val subTitleTextStyle =
    TextStyle(
        fontSize = contentTextSize,
        fontFamily = FontFamily(Font(R.font.roboto)),
        fontWeight = FontWeight.Bold,
    )

val standardTextStyle =
    TextStyle(
        fontSize = contentTextSize,
        fontFamily = FontFamily(Font(R.font.roboto)),
        fontWeight = FontWeight.Normal,
    )

@Composable
fun EventDescription(modifier: Modifier, contentColor: Color, titleColor: Color) {
  Column(modifier = modifier) {
    Text(
        text = headerDescription,
        style = subTitleTextStyle,
        color = titleColor,
        modifier = Modifier.testTag("descriptionTitle"))
    Text(
        text = contentDescription,
        style = standardTextStyle,
        color = contentColor,
        modifier = Modifier.testTag("descriptionContent"))
  }
}

@Composable
fun EventDistance(modifier: Modifier, contentColor: Color, titleColor: Color) {
  Column(modifier = modifier) {
    Text(
        text = headerDistance,
        style = subTitleTextStyle,
        color = titleColor,
        modifier = Modifier.testTag("distanceTitle"))
    Text(
        text = contentDistance,
        style = standardTextStyle,
        color = contentColor,
        modifier = Modifier.testTag("distanceContent"))
  }
}

@Composable
fun EventDate(modifier: Modifier, contentColor: Color, titleColor: Color) {
  Column(modifier = modifier) {
    Text(
        text = headerDate,
        style = subTitleTextStyle,
        color = titleColor,
        modifier = Modifier.testTag("dateTitle"))
    Text(
        text = contentDate,
        style = standardTextStyle,
        color = contentColor,
        modifier = Modifier.testTag("dateContent"))
  }
}

@Composable
fun EventCategory(modifier: Modifier, contentColor: Color, titleColor: Color) {
  Column(modifier = modifier, verticalArrangement = Arrangement.SpaceBetween) {
    Text(
        text = headerCategory,
        style = subTitleTextStyle,
        color = titleColor,
        modifier = Modifier.testTag("categoryTitle"))
    Text(
        text = contentCategory,
        style = standardTextStyle,
        color = contentColor,
        modifier = Modifier.testTag("categoryContent"))
  }
}

@Composable
fun EventTime(modifier: Modifier, contentColor: Color, titleColor: Color) {
  Column(modifier = modifier) {
    Text(
        text = headerTime,
        style = subTitleTextStyle,
        color = titleColor,
        modifier = Modifier.testTag("timeTitle"))
    Text(
        text = "start $contentTime",
        style = standardTextStyle,
        color = contentColor,
        modifier = Modifier.testTag("timeStartContent"))
    Text(
        text = "end $contentTime",
        style = standardTextStyle,
        color = contentColor,
        modifier = Modifier.testTag("timeEndContent"))
  }
}

@Composable
fun EventDetails(navigationActions: NavigationActions) {

  val fieldTitleColor = MaterialTheme.colorScheme.onSurfaceVariant
  val fieldContentColor = MaterialTheme.colorScheme.onSurface

  Scaffold(
      modifier = Modifier.testTag("EventDetailsScreen"),
      topBar = {},
      bottomBar = {
        BottomNavigationMenu(
            onTabSelected = { tab -> navigationActions.navigateTo(tab) },
            tabList = TOP_LEVEL_DESTINATIONS,
            selectedItem = TOP_LEVEL_DESTINATIONS[0],
            modifier = Modifier.testTag("bottomNavMenu"))
      },
      floatingActionButton = {
        // register button
        FloatingActionButton(
            onClick = { /*TODO*/},
            modifier = Modifier.padding(bottom = 16.dp, end = 16.dp).testTag("ticketButton"),
            // .align(Alignment.End),
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ) {
          Icon(
              painter = painterResource(id = R.drawable.ticket),
              contentDescription = "register to event button",
              modifier = Modifier.size(32.dp),
              tint = MaterialTheme.colorScheme.primary,
          )
        }
      }) { innerPadding ->
        ConstraintLayout(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
          val (image, backButton, title, description, distance, date, category, time) = createRefs()
          Image(
              painter = painterResource(eventImage),
              contentDescription = "Event Image",
              modifier =
                  Modifier.fillMaxWidth()
                      .height(imageHeight)
                      .constrainAs(image) {
                        top.linkTo(parent.top, margin = 0.dp)
                        start.linkTo(parent.start, margin = 0.dp)
                      }
                      .testTag("eventImage"),
              contentScale = ContentScale.FillWidth)

          // Go back button
          Button(
              onClick = { navigationActions.goBack() },
              modifier =
                  Modifier.wrapContentSize()
                      .constrainAs(backButton) {
                        top.linkTo(image.bottom, margin = 8.dp)
                        start.linkTo(image.start, margin = 4.dp)
                      }
                      // .align(Alignment.Start)
                      .testTag("goBackButton"),
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

          Text(
              text = eventTitle,
              style = titleTextStyle,
              modifier =
                  Modifier.constrainAs(title) {
                        top.linkTo(image.bottom, margin = 32.dp)
                        start.linkTo(image.start)
                        end.linkTo(image.end)
                      }
                      .testTag("eventTitle"),
              color = MaterialTheme.colorScheme.onSurface)

          EventDescription(
              modifier =
                  Modifier.padding(start = widthPadding, end = widthPadding).constrainAs(
                      description) {
                        top.linkTo(title.bottom, margin = 32.dp)
                        start.linkTo(title.start)
                        end.linkTo(title.end)
                      },
              fieldContentColor,
              fieldTitleColor)

          EventDistance(
              modifier =
                  Modifier.constrainAs(distance) {
                    top.linkTo(description.bottom, margin = 32.dp)
                    start.linkTo(parent.start, margin = widthPadding)
                  },
              fieldContentColor,
              fieldTitleColor)
          EventDate(
              modifier =
                  Modifier.constrainAs(date) {
                    top.linkTo(description.bottom, margin = 32.dp)
                    start.linkTo(distance.end, margin = 32.dp)
                  },
              fieldContentColor,
              fieldTitleColor)
          EventCategory(
              modifier =
                  Modifier.constrainAs(category) {
                    top.linkTo(distance.bottom, margin = 32.dp)
                    start.linkTo(parent.start, margin = widthPadding)
                  },
              fieldContentColor,
              fieldTitleColor)
          EventTime(
              modifier =
                  Modifier.constrainAs(time) {
                    top.linkTo(distance.bottom, margin = 32.dp)
                    start.linkTo(date.start)
                  },
              fieldContentColor,
              fieldTitleColor)
        }
      }
}

fun formatDateTime(dateTime: LocalDateTime): String {
  val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm")
  return dateTime.format(formatter)
}

@Composable
fun buyTicket(){

}


