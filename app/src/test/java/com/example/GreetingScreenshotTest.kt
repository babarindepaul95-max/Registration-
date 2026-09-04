package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.local.ChurchEvent
import com.example.ui.screens.LandingScreen
import com.example.ui.theme.ChurchTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun church_landing_screenshot() {
    val sampleEvents = listOf(
      ChurchEvent(
        eventId = 1,
        eventName = "Children's Day Party",
        category = "CHILDREN",
        description = "Fun games, bouncy castles, face painting, and Bible story theater for all kids.",
        eventDate = "October 10, 2026 • 11:00 AM",
        venue = "Church Family Field & Joy Hall",
        status = "OPEN"
      )
    )

    composeTestRule.setContent {
      ChurchTheme {
        LandingScreen(
          events = sampleEvents,
          onRegisterNowClick = {},
          onLoginClick = {},
          onEventSelected = {},
          onAdminPortalClick = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}

