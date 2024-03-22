package com.github.se.eventradar

import androidx.navigation.NavController
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.ui.navigation.TOP_LEVEL_DESTINATIONS
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class NavigationActionsUnitTest {
  @Test
  fun testNavigationActions() = run {
    val mockNavController = mockk<NavController>()
    val navigationActions = NavigationActions(mockNavController)

    every { mockNavController.navigate(route = any(), builder = any()) } answers {}
    every { mockNavController.popBackStack() } answers { true }

    // Mock navigateTo
    navigationActions.navigateTo(TOP_LEVEL_DESTINATIONS[0])
    verify { mockNavController.navigate(TOP_LEVEL_DESTINATIONS[0].route, builder = any()) }

    // Mock goBack
    navigationActions.goBack()
    verify { mockNavController.popBackStack() }
    confirmVerified(mockNavController)
  }
}
