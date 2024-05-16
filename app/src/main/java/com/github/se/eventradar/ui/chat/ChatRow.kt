package com.github.se.eventradar.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

@Composable
fun SentMessageRow(text: String) {
  val screenWidthDp = LocalConfiguration.current.screenWidthDp
  val maxWidth = (0.7f * screenWidthDp).dp

  // Whole column that contains chat bubble and padding on start or end
  Column(
      horizontalAlignment = Alignment.End,
      modifier =
          Modifier.fillMaxWidth()
              .wrapContentHeight()
              .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 10.dp)
              .testTag("sentColumn")) {
        // This is chat bubble
        ChatBubbleConstraints(
            modifier =
                Modifier.clip(
                        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable {}
                    .testTag("sentChatBubble"),
            maxWidth = maxWidth,
            content = {
              TextMessageInsideBubble(
                  modifier =
                      Modifier.padding(start = 6.dp, end = 6.dp, top = 6.dp, bottom = 6.dp)
                          .testTag("sentChatBubbleText"),
                  text = text,
                  color = MaterialTheme.colorScheme.onPrimaryContainer,
                  style = MaterialTheme.typography.bodyLarge)
            })
      }
}

@Composable
fun ReceivedMessageRow(
    text: String,
) {
  val screenWidthDp = LocalConfiguration.current.screenWidthDp
  val maxWidth = (0.7f * screenWidthDp).dp

  Column(
      horizontalAlignment = Alignment.Start,
      modifier =
          Modifier.fillMaxWidth()
              .wrapContentHeight()
              .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 10.dp)
              .testTag("receivedColumn")) {
        // ChatBubble
        ChatBubbleConstraints(
            modifier =
                Modifier.clip(
                        RoundedCornerShape(bottomEnd = 16.dp, topEnd = 16.dp, bottomStart = 16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable {}
                    .testTag("receivedChatBubble"),
            maxWidth = maxWidth,
            content = {
              TextMessageInsideBubble(
                  modifier =
                      Modifier.padding(start = 6.dp, end = 6.dp, top = 6.dp, bottom = 6.dp)
                          .testTag("receivedChatBubbleText"),
                  text = text,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  style = MaterialTheme.typography.bodyLarge)
            })
      }
}

@Composable
fun ChatBubbleConstraints(
    modifier: Modifier = Modifier,
    maxWidth: Dp,
    content: @Composable () -> Unit = {},
) {
  SubcomposeLayout(modifier = modifier) { constraints ->
    var recompositionIndex = 0
    val constrainedMaxWidth = Constraints(maxWidth = maxWidth.roundToPx())

    var placeables: List<Placeable> =
        subcompose(recompositionIndex++, content).map { it.measure(constrainedMaxWidth) }
    val columnSize =
        placeables.fold(IntSize.Zero) { currentMax: IntSize, placeable: Placeable ->
          IntSize(
              width = maxOf(currentMax.width, placeable.width),
              height = currentMax.height + placeable.height)
        }
    if (placeables.isNotEmpty() && (placeables.size > 1)) {
      placeables =
          subcompose(recompositionIndex, content).map { measurable: Measurable ->
            measurable.measure(Constraints(columnSize.width, constraints.maxWidth))
          }
    }
    layout(columnSize.width, columnSize.height) {
      var yPos = 0
      placeables.forEach { placeable: Placeable ->
        placeable.placeRelative(0, yPos)
        yPos += placeable.height
      }
    }
  }
}

@Composable
fun TextMessageInsideBubble(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    style: TextStyle = LocalTextStyle.current,
    onMeasure: ((ChatRowData) -> Unit)? = null
) {
  val chatRowData = remember { ChatRowData() }
  val content =
      @Composable {
        Text(
            modifier = modifier.wrapContentSize(),
            text = text,
            color = color,
            style = style,
            onTextLayout = { textLayoutResult: TextLayoutResult ->
              // maxWidth of text constraint returns parent maxWidth - horizontal padding
              chatRowData.lineCount = textLayoutResult.lineCount
              chatRowData.lastLineWidth = textLayoutResult.getLineRight(chatRowData.lineCount - 1)
              chatRowData.textWidth = textLayoutResult.size.width
            })
      }

  Layout(modifier = modifier, content = content) {
      measurables: List<Measurable>,
      constraints: Constraints ->
    if (measurables.size != 1)
        throw IllegalArgumentException("There should be 1 component for this layout")

    val placeable: Placeable = measurables.first().measure(Constraints(0, constraints.maxWidth))

    // calculate chat row dimensions are not  based on message positions
    if ((chatRowData.rowWidth == 0 || chatRowData.rowHeight == 0) || chatRowData.text != text) {
      // Constrain with max width instead of longest sibling
      // since this composable can be longest of siblings after calculation
      chatRowData.parentWidth = constraints.maxWidth
      chatRowData.rowWidth = placeable.width
      chatRowData.rowHeight = placeable.height
      // Parent width of this chat row is either result of width calculation
      // or quote or other sibling width if they are longer than calculated width.
      // minWidth of Constraint equals (text width + horizontal padding)
      chatRowData.parentWidth =
          chatRowData.rowWidth.coerceAtLeast(minimumValue = constraints.minWidth)
    }

    // Send measurement results if requested by Composable
    onMeasure?.invoke(chatRowData)

    layout(width = chatRowData.parentWidth, height = chatRowData.rowHeight) {
      placeable.placeRelative(0, 0)
    }
  }
}
