package com.github.se.eventradar.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.eventradar.ui.component.ViewToggleFab
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReusableComponentsTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun viewToggleFabTest() {
    var viewList = true
    composeTestRule.setContent {
      ViewToggleFab(onClick = { viewList = false }, iconVector = Icons.Default.Place)
    }
    composeTestRule.onNodeWithContentDescription("Icon").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Icon").performClick()
    assert(viewList == false)
  }
}
