package com.github.se.eventradar

import com.github.se.eventradar.model.location.Location
import junit.framework.TestCase.assertEquals
import org.junit.Test

class LocationUnitTest {
  @Test
  fun location_isCorrect() = run {
    val location = Location(46.51890374606943, 6.566587868510539, "EPFL")
    assertEquals(46.51890374606943, location.latitude)
    assertEquals(6.566587868510539, location.longitude)
    assertEquals("EPFL", location.name)
  }
}
