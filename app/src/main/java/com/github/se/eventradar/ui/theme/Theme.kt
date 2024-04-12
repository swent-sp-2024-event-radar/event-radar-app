package com.github.se.eventradar.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/* Color scheme for event radar app
 *  Primary :         main color used for branding and key UI elements
 *  Secondary :       color used for accents and complementary UI elements
 *  Tertiary :        color used for additional accents and UI elements
 *  Surface :         are the background colors for the whole screen
 *  [...]Containers : are color for the containers containing primary, secondary or tertiary elements
 *  on[...] :         are used for components situated on a component of the same color name without the "on"
 *                    Eg : For text on a "primary color background" you give it the "onPrimary" color
 *
 *  If you have any doubts, you can find examples of UI and look at their material here :
 *       https://www.figma.com/file/yO8KgaCa6F39yUdyPSfkpn/Material-3-Design-Kit-(Community)?type=design&node-id=53923-27456&mode=design&t=ir0UAE4XpwgRJMYn-0
 * */

private val DarkColorScheme =
    darkColorScheme(
        // Primary colors : main color used for branding and key UI elements
        primary = P_80,
        onPrimary = P_20,
        primaryContainer = P_30,
        onPrimaryContainer = P_90,
        // Secondary colors : color used for accents and complementary UI elements
        secondary = S_80,
        onSecondary = S_20,
        secondaryContainer = S_30,
        onSecondaryContainer = S_90,
        // Tertiary colors : color used for additional accents and UI elements
        tertiary = T_80,
        onTertiary = T_20,
        tertiaryContainer = T_30,
        onTertiaryContainer = T_90,
        // Error colors : used to indicate errors or warnings
        error = E_80,
        onError = E_20,
        errorContainer = E_30,
        onErrorContainer = E_90,
        // Surface colors : are the background colors for the whole screen
        surfaceDim = N_6,
        surface = N_6,
        surfaceBright = N_24,
        onSurface = N_90,
        onSurfaceVariant = NV_80,
        // Surface containers colors : used for visual differentiation and emphasis based on their
        // importance or prominence in the UI layout
        surfaceContainerLowest = N_4,
        surfaceContainerLow = N_10,
        surfaceContainer = N_12,
        surfaceContainerHigh = N_17,
        surfaceContainerHighest = N_22,
        // Outline : typically for a separation line
        outline = NV_60,
        outlineVariant = NV_30,
        // Inverse : used for readability on dark background
        inverseSurface = N_90,
        inverseOnSurface = N_20,
        inversePrimary = P_40,
        // Scrim : color of the translucent overlay placed on top of content to indicate that it is
        // not currently interactive
        scrim = N_0,
    )

private val LightColorScheme =
    lightColorScheme(
        // Primary colors : main color used for branding and key UI elements
        primary = P_40,
        onPrimary = P_100,
        primaryContainer = P_90,
        onPrimaryContainer = P_10,
        // Secondary colors : color used for accents and complementary UI elements
        secondary = S_40,
        onSecondary = S_100,
        secondaryContainer = S_90,
        onSecondaryContainer = S_10,
        // Tertiary colors : color used for additional accents and UI elements
        tertiary = T_40,
        onTertiary = T_100,
        tertiaryContainer = T_90,
        onTertiaryContainer = T_10,
        // Error colors : used to indicate errors or warnings
        error = E_40,
        onError = E_100,
        errorContainer = E_90,
        onErrorContainer = E_10,
        // Surface colors : are the background colors for the whole screen
        surfaceDim = N_87,
        surface = N_98,
        surfaceBright = N_98,
        onSurface = N_10,
        onSurfaceVariant = NV_30,
        // Surface containers colors : used for visual differentiation and emphasis based on their
        // importance or prominence in the UI layout
        surfaceContainerLowest = N_100,
        surfaceContainerLow = N_96,
        surfaceContainer = N_94,
        surfaceContainerHigh = N_92,
        surfaceContainerHighest = N_90,
        // Outline : typically for a separation line
        outline = NV_50,
        outlineVariant = NV_80,
        // Inverse : used for readability on dark background
        inverseSurface = N_20,
        inverseOnSurface = N_95,
        inversePrimary = P_80,
        // Scrim : color of the translucent overlay placed on top of content to indicate that it is
        // not currently interactive
        scrim = N_0,
    )

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
  val colorScheme =
      when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
          val context = LocalContext.current
          if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
      }
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as Activity).window
      window.statusBarColor = colorScheme.primary.toArgb()
      WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
    }
  }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
