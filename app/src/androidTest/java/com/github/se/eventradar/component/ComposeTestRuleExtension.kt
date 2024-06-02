package com.github.se.eventradar.component

import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule

// Solution from here :
// https://stackoverflow.com/questions/73191692/test-errormyactivity-has-already-set-content-if-you-have-populated-the-activit

fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.clearAndSetContent(
    content: @Composable () -> Unit
) {
  (this.activity.findViewById<ViewGroup>(android.R.id.content)?.getChildAt(0) as? ComposeView)
      ?.setContent(content) ?: this.setContent(content)
}
