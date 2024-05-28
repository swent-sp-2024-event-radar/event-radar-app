package com.github.se.eventradar.component

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.eventradar.ui.component.GetUserLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocationPermissionsTest :
    TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()

  // This rule automatic initializes lateinit properties with @MockK, @RelaxedMockK, etc.
  @get:Rule val mockkRule = MockKRule(this)

  // Relaxed mocks methods have a default implementation returning values
  @RelaxedMockK lateinit var mockFusedLocationProviderClient: FusedLocationProviderClient

  @RelaxedMockK lateinit var mockLocationCallback: LocationCallback

  @Test
  fun testLocationProviderClientCalled() = run {
    composeTestRule.setContent {
      GetUserLocation(mockFusedLocationProviderClient, mockLocationCallback)
    }

    verify {
      mockFusedLocationProviderClient.requestLocationUpdates(
          any(), any() as LocationCallback, any())
    }
  }
}
