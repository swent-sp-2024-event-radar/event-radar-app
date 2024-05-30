package com.github.se.eventradar.component

import android.app.Activity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.eventradar.model.Location
import com.github.se.eventradar.ui.component.GetUserLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.unmockkAll
import io.mockk.verify
import java.util.concurrent.Executor
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocationPermissionsTest :
    TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()

  // This rule automatic initializes lateinit properties with @MockK, @RelaxedMockK, etc.
  @get:Rule val mockkRule = MockKRule(this)

  @RelaxedMockK lateinit var mockLocationListener: (Location) -> Unit
  @RelaxedMockK lateinit var mockProviderClient: FusedLocationProviderClient

  private lateinit var mockLocation: android.location.Location

  @Before
  fun setUp() {
    mockLocation = android.location.Location("mock")
    mockLocation.latitude = 0.0
    mockLocation.longitude = 0.0

    composeTestRule.setContent {
      val context = LocalContext.current
      GetUserLocation(context, mockLocationListener, mockProviderClient)
    }
  }

  @After
  fun tearDown() {
    unmockkAll()
  }

  @Test
  fun testLocationIsRequestedCalled() = run {
    every { mockProviderClient.lastLocation } returns mockTask(mockLocation)

    verify { mockProviderClient.requestLocationUpdates(any(), any() as LocationCallback, any()) }
  }
}

inline fun <reified T> mockTask(result: T?, exception: Exception? = null): Task<T> {
  return object : Task<T>() {
    override fun isComplete(): Boolean = true

    override fun isSuccessful(): Boolean = exception == null

    override fun addOnFailureListener(p0: OnFailureListener): Task<T> {
      p0.onFailure(exception ?: Exception())
      return this
    }

    override fun addOnFailureListener(p0: Activity, p1: OnFailureListener): Task<T> {
      p1.onFailure(exception ?: Exception())
      return this
    }

    override fun addOnFailureListener(p0: Executor, p1: OnFailureListener): Task<T> {
      p1.onFailure(exception ?: Exception())
      return this
    }

    override fun getException(): Exception? {
      return exception
    }

    override fun addOnSuccessListener(p0: Executor, p1: OnSuccessListener<in T>): Task<T> {
      p1.onSuccess(result)
      return this
    }

    override fun addOnSuccessListener(p0: Activity, p1: OnSuccessListener<in T>): Task<T> {
      p1.onSuccess(result)
      return this
    }

    override fun addOnSuccessListener(p0: OnSuccessListener<in T>): Task<T> {
      p0.onSuccess(result)
      return this
    }

    override fun getResult(): T? = result

    override fun <X : Throwable?> getResult(p0: Class<X>): T {
      return result ?: throw exception ?: Exception()
    }

    override fun isCanceled(): Boolean {
      return false
    }
  }
}
