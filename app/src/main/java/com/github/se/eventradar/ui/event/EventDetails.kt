package com.github.se.eventradar.ui.event

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.eventradar.R
import com.github.se.eventradar.ui.theme.MyApplicationTheme


// Temporary text field values
private val headerDescription: String = "Description"
private val headerDistance: String = "Distance from you"
private val headerDate: String = "Date"
private val headerCategory: String = "Category"
private val headerTime: String = "Time"

private val contentDescription: String =
    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc et ornare  dui. Integer convallis purus odio, vitae mattis erat ultricies non.  Donec et magna hendrerit, molestie lorem vel, facilisis augue. "
private val contentDistance: String = "xx km"
private val contentDate: String = "dd/mm/yyyy"
private val contentCategory: String = "Cat x"
private val contentTime: String = "xx:xx"

//
private val widthPadding = 34.dp
private val imageHeight = 191.dp

// Temporary elements font sizes
private val titleTextSize = 32.sp
private val contentTextSize = 14.sp

// Temporary colors scheme of the screen
private val DT_fieldTitleColor = Color(0xFF79747E)
private val DT_fieldContentColor = Color(0xFFFFFFFF)
private val DT_titleColor = Color(0xFFFFFFFF)

private val DT_backgroundColor = Color(0xFF1B191B) // TODO take the actual from figma
private val DT_lightBackgroundColor = Color(0xFF24292F)

private val DT_special1Color = Color(0xFFAA23AA)


private val eventImage = R.drawable.ic_launcher_background

@Composable
// TODO new header for nav and viewModel
// fun EventDetails(EventviewViewModel: ViewModel = viewModel(), navigationActions: NavigationActions){
fun EventDetails() {


    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = painterResource(eventImage),
            contentDescription = "Event Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(imageHeight),
            contentScale = ContentScale.FillWidth

        )

        // go back button
        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.Start)
                .testTag("backButton"),
            colors =
            ButtonDefaults.buttonColors(
                contentColor = DT_titleColor,
                containerColor = Color(0x00FFFFFF),
            ),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.back_arrow),
                contentDescription = "Back navigation arrow",
                tint = DT_titleColor,
                modifier = Modifier
                    .width(24.dp)
                    .height(24.dp)
                    .align(Alignment.CenterVertically)

            )
        }


        Text(
            text = "Event Title",
            color = DT_titleColor,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Bold,
            fontSize = titleTextSize,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )


        Column(
            modifier = Modifier
                .padding(start = widthPadding, end = widthPadding)
        ) {
            Text(text = headerDescription, color = DT_fieldTitleColor, fontSize = contentTextSize)
            Text(
                text = contentDescription,
                color = DT_fieldContentColor,
                fontSize = contentTextSize
            )
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = widthPadding, end = widthPadding)
                .height(64.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = headerDistance, color = DT_fieldTitleColor, fontSize = contentTextSize)
                Text(
                    text = contentDistance,
                    color = DT_fieldContentColor,
                    fontSize = contentTextSize
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = headerDate, color = DT_fieldTitleColor, fontSize = contentTextSize)
                Text(text = contentDate, color = DT_fieldContentColor, fontSize = contentTextSize)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = widthPadding, end = widthPadding)
                .height(64.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = headerCategory, color = DT_fieldTitleColor, fontSize = contentTextSize)
                Text(
                    text = contentCategory,
                    color = DT_fieldContentColor,
                    fontSize = contentTextSize
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = headerTime, color = DT_fieldTitleColor, fontSize = contentTextSize)
                Text(text = contentTime, color = DT_fieldContentColor, fontSize = contentTextSize)
            }
        }

        // register button
        FloatingActionButton(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .padding(bottom = 16.dp, end = 16.dp)
                .align(Alignment.End), // Ajustez l'espacement par le bas selon vos besoins
            containerColor = DT_lightBackgroundColor,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.menu),
                contentDescription = "register to event button",
                modifier = Modifier.size(32.dp),
                tint = DT_special1Color,
            )

        }

        //TODO add BottomNavBar (placeholder)
        Row(
            modifier = Modifier
                .background(DT_fieldContentColor)
                .height(80.dp)
                .fillMaxWidth()
        ) {
            Text(text = "add bottom nav bar")
        }
    }

}


@Preview(showBackground = true)
@Composable
fun EventDetailsPreview() {
    MyApplicationTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = DT_backgroundColor
        ) {
            EventDetails()
        }
    }
}