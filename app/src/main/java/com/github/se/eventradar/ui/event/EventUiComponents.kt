package com.github.se.eventradar.ui.event

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.github.se.eventradar.R
import com.github.se.eventradar.model.event.EventUiState
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class EventComponentsStyle(
  val titleColor : Color,
  val subTitleColor: Color,
  val contentColor: Color,
  val titleStyle : TextStyle = TextStyle(
    fontSize = 32.sp,
    fontFamily = FontFamily(Font(R.font.roboto)),
    fontWeight = FontWeight.Bold,
    lineHeight = 20.sp,
  ),
  val subTitleStyle: TextStyle = TextStyle(
    fontSize = 14.sp,
    fontFamily = FontFamily(Font(R.font.roboto)),
    fontWeight = FontWeight.Bold,
  ),
  val contentStyle: TextStyle = TextStyle(
    fontSize = 14.sp,
    fontFamily = FontFamily(Font(R.font.roboto)),
    fontWeight = FontWeight.Normal,
  )
)

fun formatDateTime(dateTime: LocalDateTime): String {
  val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm")
  return dateTime.format(formatter)
}


@Composable
fun EventDescription(modifier: Modifier, eventUiState: EventUiState, style: EventComponentsStyle) {
  Column(modifier = modifier) {
    Text(
      text = stringResource(id = R.string.event_description),
      style = style.subTitleStyle,
      color = style.subTitleColor,
      modifier = Modifier.testTag("descriptionTitle"))
    Text(
      text = eventUiState.description,
      style = style.contentStyle,
      color = style.contentColor,
      modifier = Modifier.testTag("descriptionContent"))
  }
}

@Composable
fun EventDistance(modifier: Modifier, eventUiState: EventUiState, style: EventComponentsStyle) {
  Column(modifier = modifier) {
    Text(
      text = stringResource(id = R.string.event_distance),
      style = style.subTitleStyle,
      color = style.subTitleColor,
      modifier = Modifier.testTag("distanceTitle"))
    Text(
      // TODO implement a function that convert event location to distance from user
      // TODO require user data class
      text = eventUiState.location.address,
      style = style.contentStyle,
      color = style.contentColor,
      modifier = Modifier.testTag("distanceContent"))
  }
}

@Composable
fun EventCategory(modifier: Modifier, eventUiState: EventUiState, style: EventComponentsStyle) {
  Column(modifier = modifier, verticalArrangement = Arrangement.SpaceBetween) {
    Text(
      text = stringResource(id = R.string.event_categories),
      style = style.subTitleStyle,
      color = style.subTitleColor,
      modifier = Modifier.testTag("categoryTitle"))
    Text(
      text = eventUiState.category.toString(), // TODO is that the correct way to convert category object to string ?
      style = style.contentStyle,
      color = style.contentColor,
      modifier = Modifier.testTag("categoryContent"))
  }
}

@Composable
fun EventDateTime(modifier: Modifier, eventUiState: EventUiState, style: EventComponentsStyle) {
  Column(modifier = modifier) {
    Text(
      text = stringResource(id = R.string.event_date_and_time),
      style = style.subTitleStyle,
      color = style.titleColor,
      modifier = Modifier.testTag("timeTitle"))
    Text(
      text = "${stringResource(id = R.string.event_dt_start)}: ${formatDateTime(eventUiState.start)}",
      style = style.contentStyle,
      color = style.contentColor,
      modifier = Modifier.testTag("timeStartContent"))
    Text(
      text = "${stringResource(id = R.string.event_dt_end)}: ${formatDateTime(eventUiState.end)}",
      style = style.contentStyle,
      color = style.contentColor,
      modifier = Modifier.testTag("timeEndContent"))
  }
}



