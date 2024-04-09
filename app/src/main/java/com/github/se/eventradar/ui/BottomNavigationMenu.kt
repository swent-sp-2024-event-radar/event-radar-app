package com.github.se.eventradar.ui

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.se.eventradar.R
import com.github.se.eventradar.ui.navigation.TopLevelDestination

@Composable
fun BottomNavigationMenu(
    onTabSelected: (TopLevelDestination) -> Unit,
    tabList: List<TopLevelDestination>,
    selectedItem: TopLevelDestination,
    modifier: Modifier = Modifier,
) {
  BottomNavigation(
      backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
      modifier = modifier,
  ) {
    tabList.forEach { tab ->
      BottomNavigationItem(
          icon = {
            Icon(
                painter = painterResource(id = tab.icon),
                contentDescription = null,
                modifier = Modifier.width(24.dp).height(24.dp),
            )
          },
          label = { Text(stringResource(tab.textId)) },
          selected = selectedItem == tab,
          onClick = { onTabSelected(tab) },
          modifier =
              if (tab.textId == R.string.map) Modifier.testTag("mapBottomNav")
              else Modifier.testTag("overviewBottomNav"),
      )
    }
  }
}
