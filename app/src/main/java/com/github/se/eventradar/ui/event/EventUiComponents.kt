package com.github.se.eventradar.ui.event

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.github.se.eventradar.R
import com.github.se.eventradar.model.event.EventUiState

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


@Composable
fun EventDescription(modifier: Modifier, eventUiState: EventUiState, style: EventComponentsStyle) {
  Column(modifier = modifier) {
    Text(
      text = headerDescription,
      style = subTitleTextStyle,
      color = style.subTitleColor,
      modifier = Modifier.testTag("descriptionTitle"))
    Text(
      text = eventUiState.description,
      style = standardTextStyle,
      color = style.contentColor,
      modifier = Modifier.testTag("descriptionContent"))
  }
}

@Composable
fun EventDistance(modifier: Modifier, style: EventComponentsStyle) {
  Column(modifier = modifier) {
    Text(
      text = headerDistance,
      style = subTitleTextStyle,
      color = style.subTitleColor,
      modifier = Modifier.testTag("distanceTitle"))
    Text(
      text = contentDistance,
      style = standardTextStyle,
      color = style.contentColor,
      modifier = Modifier.testTag("distanceContent"))
  }
}

@Composable
fun EventCategory(modifier: Modifier, style: EventComponentsStyle) {
  Column(modifier = modifier, verticalArrangement = Arrangement.SpaceBetween) {
    Text(
      text = headerCategory,
      style = subTitleTextStyle,
      color = style.subTitleColor,
      modifier = Modifier.testTag("categoryTitle"))
    Text(
      text = contentCategory,
      style = standardTextStyle,
      color = style.contentColor,
      modifier = Modifier.testTag("categoryContent"))
  }
}

@Composable
fun EventTimeDate(modifier: Modifier, style: EventComponentsStyle) {
  Column(modifier = modifier) {
    Text(
      text = headerTime,
      style = subTitleTextStyle,
      color = style.titleColor,
      modifier = Modifier.testTag("timeTitle"))
    Text(
      text = "start $contentTime",
      style = standardTextStyle,
      color = style.contentColor,
      modifier = Modifier.testTag("timeStartContent"))
    Text(
      text = "end $contentTime",
      style = standardTextStyle,
      color = style.contentColor,
      modifier = Modifier.testTag("timeEndContent"))
  }
}



