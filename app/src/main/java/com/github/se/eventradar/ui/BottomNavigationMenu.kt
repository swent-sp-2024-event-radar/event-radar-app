package com.github.se.eventradar.ui

import android.graphics.Rect
import android.view.ViewTreeObserver
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.eventradar.R
import com.github.se.eventradar.ui.navigation.TopLevelDestination

@Composable
fun BottomNavigationMenu(
    onTabSelected: (TopLevelDestination) -> Unit,
    tabList: List<TopLevelDestination>,
    selectedItem: TopLevelDestination,
    modifier: Modifier = Modifier,
) {
  val isKeyboardOpen by keyboardAsState() // Keyboard.Opened or Keyboard.Closed

  if (isKeyboardOpen == Keyboard.Closed) {
    BottomNavigation(
        backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier,
    ) {
      tabList.forEach { tab ->
        val boxModifier =
            if (tab == selectedItem) {
              Modifier.clip(shape = RoundedCornerShape(50))
                  .background(MaterialTheme.colorScheme.primary)
                  .width(58.dp)
                  .height(36.dp)
            } else Modifier

        val labelText = if (selectedItem == tab) stringResource(tab.textId) else ""

        val iconColor =
            if (selectedItem == tab) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurfaceVariant

        BottomNavigationItem(
            icon = { NavBarIcon(tab, boxModifier, iconColor) },
            label = {
              Text(
                  text = labelText,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  fontSize = 11.sp,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis)
            },
            selected = selectedItem == tab,
            onClick = { onTabSelected(tab) },
            modifier =
                Modifier.align(Alignment.CenterVertically)
                    .testTag(
                        when (tab.textId) {
                          R.string.scan_QR -> "scanQRBottomNav"
                          R.string.message_chats -> "messageChatBottomNav"
                          R.string.homeScreen_events -> "homeScreenEventBottomNav"
                          R.string.user_profile -> "userProfileBottomNav"
                          R.string.hosting -> "myHostingBottomNav"
                          else -> "itemBottomNav"
                        }))
      }
    }
  }
}

@Composable
fun NavBarIcon(tab: TopLevelDestination, modifier: Modifier, iconColor: Color) {
  Box(modifier = modifier, contentAlignment = Alignment.Center) {
    Icon(
        painter = painterResource(id = tab.icon),
        tint = iconColor,
        contentDescription = null,
        modifier = Modifier.width(24.dp).height(24.dp),
    )
  }
}

enum class Keyboard {
  Opened,
  Closed
}

@Composable
fun keyboardAsState(): State<Keyboard> {
  val keyboardState = remember { mutableStateOf(Keyboard.Closed) }
  val view = LocalView.current
  DisposableEffect(view) {
    val onGlobalListener =
        ViewTreeObserver.OnGlobalLayoutListener {
          val rect = Rect()
          view.getWindowVisibleDisplayFrame(rect)
          val screenHeight = view.rootView.height
          val keypadHeight = screenHeight - rect.bottom
          keyboardState.value =
              if (keypadHeight > screenHeight * 0.15) {
                Keyboard.Opened
              } else {
                Keyboard.Closed
              }
        }
    view.viewTreeObserver.addOnGlobalLayoutListener(onGlobalListener)

    onDispose { view.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalListener) }
  }

  return keyboardState
}
