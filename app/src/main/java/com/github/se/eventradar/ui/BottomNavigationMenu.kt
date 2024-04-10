package com.github.se.eventradar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.eventradar.R
import com.github.se.eventradar.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.eventradar.ui.navigation.TopLevelDestination
import com.github.se.eventradar.ui.theme.MyApplicationTheme

// TODO link these to app theme
private val iconColor = Color(0xFFFFFFFF)
private val lightBackgroundColor = Color(0xFF24292F)
private val selectedColor = Color(0xFFB422D9)

@Composable
fun BottomNavigationMenu(
  onTabSelected: (TopLevelDestination) -> Unit,
  tabList: List<TopLevelDestination>,
  selectedItem: TopLevelDestination,
  modifier: Modifier = Modifier,
) {
  BottomNavigation(
    backgroundColor = lightBackgroundColor,
    modifier = modifier,
  ) {
    tabList.forEach { tab ->
      val boxModifier =
        if (tab == selectedItem) {
          Modifier
            .clip(shape = RoundedCornerShape(50))
            .background(selectedColor)
            .width(58.dp)
            .height(36.dp)
        } else Modifier

      val t = if (selectedItem == tab) stringResource(tab.textId) else ""

      BottomNavigationItem(
        icon = { NavBarIcon(tab, boxModifier) },
        label = { Text(text = t, color = iconColor, fontSize = 12.sp) },
        selected = selectedItem == tab,
        onClick = { onTabSelected(tab) },
        modifier =
        Modifier
          .align(Alignment.CenterVertically)
          .testTag(
            when (tab.textId) {
              R.string.scan_QR -> "scanQRBottomNav"
              R.string.message_chats -> "messageChatBottomNav"
              R.string.homeScreen_events -> "homeScreenEventBottomNav"
              R.string.user_profile -> "userProfileBottomNav"
              R.string.my_hosted_events -> "myHostingBottomNav"
              else -> "itemBottomNav"
            }
          )
      )
    }
  }
}

@Composable
fun NavBarIcon(tab: TopLevelDestination, modifier: Modifier) {
  Box(modifier = modifier, contentAlignment = Alignment.Center) {
    Icon(
      painter = painterResource(id = tab.icon),
      tint = iconColor,
      contentDescription = null,
      modifier = Modifier
        .width(24.dp)
        .height(24.dp),
    )
  }
}

@Preview(showBackground = true)
@Composable
fun EventDetailsPreview() {
  MyApplicationTheme {
    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFFFFFFF)) {
      val selectedItem = TOP_LEVEL_DESTINATIONS[2]
      BottomNavigationMenu(
        onTabSelected = { },
        tabList = TOP_LEVEL_DESTINATIONS,
        selectedItem = selectedItem
      )
    }
  }
}
