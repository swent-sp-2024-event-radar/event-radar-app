package com.github.se.eventradar.ui.chat

import android.graphics.fonts.FontFamily
import android.graphics.fonts.FontStyle
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

@Composable
fun SentMessageRow(
    text: String,
    messageTime: String,
    messageRead: Boolean
) {
    // Whole column that contains chat bubble and padding on start or end
    Column(
        horizontalAlignment = Alignment.End,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(
                start = 10.dp,
                end = 10.dp,
                top = 10.dp,
                bottom = 10.dp
            )
            .testTag("sentColumn")
    ) {
        // This is chat bubble
        ChatBubbleConstraints(
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .clickable { }
                .testTag("sentChatBubble"),
            content = {
                TextMessageInsideBubble(
                    modifier = Modifier
                        .padding(
                            start = 10.dp,
                            end = 10.dp,
                            top = 10.dp,
                            bottom = 10.dp)
                        .testTag("sentChatBubbleText"),
                    text = text,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.bodyLarge,
                    messageStat = {
                        MessageTimeText(
                            modifier = Modifier.wrapContentSize(),
                            messageTime = messageTime,
                            messageRead = messageRead
                        )
                    }
                )
            }
        )
    }
}

@Composable
fun MessageTimeText(
    modifier: Modifier = Modifier,
    messageTime: String,
    messageRead: Boolean
) {
    Row(
        modifier = modifier.testTag("sentChatBubbleTimeRow"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = messageTime,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = modifier.testTag("messageTimeRowText"))

        Icon(
            modifier = Modifier
                .size(18.dp)
                .padding(start = 4.dp)
                .testTag("messageTimeRowIcon"),
            imageVector = Icons.Default.Done,
            // TO DO: Change color based on messageRead
            tint = if (messageRead) Color.Blue
            else Color.Gray,
            contentDescription = "messageStatus"
        )

    }
}

@Composable
fun ReceivedMessageRow(
    text: String,
    messageTime: String,
) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(
                start = 10.dp,
                end = 10.dp,
                top = 10.dp,
                bottom = 10.dp
            )
            .testTag("receivedColumn")
    ) {
        //ChatBubble
        ChatBubbleConstraints(
            modifier = Modifier
                .clip(RoundedCornerShape(bottomEnd = 16.dp, topEnd = 16.dp, bottomStart = 16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { }
                .testTag("receivedChatBubble"),
            content = {
                TextMessageInsideBubble(
                    modifier = Modifier.padding(
                        start = 10.dp,
                        end = 10.dp,
                        top = 10.dp,
                        bottom = 10.dp
                    )
                        .testTag("receivedChatBubbleText"),
                    text = text,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge,
                    messageStat = {
                        Text(
                            modifier = Modifier.padding(end = 10.dp).testTag("receivedMessageText"),
                            text = messageTime,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                )
            }
        )
    }
}

@Composable
fun ChatBubbleConstraints(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {},
) {
    SubcomposeLayout(modifier = modifier) { constraints ->
        var recompositionIndex = 0
        var placeables: List<Placeable> = subcompose(recompositionIndex++, content).map {
            it.measure(constraints)
        }
        val columnSize =
            placeables.fold(IntSize.Zero) { currentMax: IntSize, placeable: Placeable ->
                IntSize(
                    width = maxOf(currentMax.width, placeable.width),
                    height = currentMax.height + placeable.height
                )
            }
        if (placeables.isNotEmpty() && (placeables.size > 1)) {
            placeables = subcompose(recompositionIndex, content).map { measurable: Measurable ->
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
    fontSize: TextUnit = TextUnit.Unspecified,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    style: TextStyle = LocalTextStyle.current,
    messageStat: @Composable () -> Unit,
    onMeasure: ((ChatRowData) -> Unit)? = null
) {
    val chatRowData = remember { ChatRowData() }
    val content = @Composable {

        Text(
            modifier = modifier
                .wrapContentSize(),
            text = text,
            color = color,
            fontSize = fontSize,
            letterSpacing = letterSpacing,
            textDecoration = textDecoration,
            textAlign = textAlign,
            lineHeight = lineHeight,
            overflow = overflow,
            softWrap = softWrap,
            style = style,
            maxLines = maxLines,
            onTextLayout = { textLayoutResult: TextLayoutResult ->
                // maxWidth of text constraint returns parent maxWidth - horizontal padding
                chatRowData.lineCount = textLayoutResult.lineCount
                chatRowData.lastLineWidth =
                    textLayoutResult.getLineRight(chatRowData.lineCount - 1)
                chatRowData.textWidth = textLayoutResult.size.width
            }
        )

        messageStat()
    }

    Layout(
        modifier = modifier,
        content = content
    ) { measurables: List<Measurable>, constraints: Constraints ->

        if (measurables.size != 2)
            throw IllegalArgumentException("There should be 2 components for this layout")

//        println("⚠️ CHAT constraints: $constraints")

        val placeables: List<Placeable> = measurables.map { measurable ->
            // Measure each child maximum constraints since message can cover all of the available
            // space by parent
            measurable.measure(Constraints(0, constraints.maxWidth))
        }

        val message = placeables.first()
        val status = placeables.last()

        // calculate chat row dimensions are not  based on message and status positions
        if ((chatRowData.rowWidth == 0 || chatRowData.rowHeight == 0) || chatRowData.text != text) {
            // Constrain with max width instead of longest sibling
            // since this composable can be longest of siblings after calculation
            chatRowData.parentWidth = constraints.maxWidth
            calculateChatWidthAndHeight(chatRowData, message, status)
            // Parent width of this chat row is either result of width calculation
            // or quote or other sibling width if they are longer than calculated width.
            // minWidth of Constraint equals (text width + horizontal padding)
            chatRowData.parentWidth =
                chatRowData.rowWidth.coerceAtLeast(minimumValue = constraints.minWidth)
        }

//        println("⚠️⚠️ CHAT after calculation-> CHAT_ROW_DATA: $chatRowData")

        // Send measurement results if requested by Composable
        onMeasure?.invoke(chatRowData)

        layout(width = chatRowData.parentWidth, height = chatRowData.rowHeight) {

            message.placeRelative(0, 0)
            // set left of status relative to parent because other elements could result this row
            // to be long as longest composable
            status.placeRelative(
                chatRowData.parentWidth - status.width,
                chatRowData.rowHeight - status.height
            )
        }
    }
}