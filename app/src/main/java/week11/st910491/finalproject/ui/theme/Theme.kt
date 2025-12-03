package week11.st910491.finalproject.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlueDark,
    onPrimary = Color.Black,
    secondary = SupportGreenDark,
    onSecondary = Color.Black,
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    error = ErrorRedDark,
    onError = Color.Black
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    secondary = SupportGreen,
    onSecondary = Color.White,
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    error = ErrorRed,
    onError = Color.White
)

private val HighContrastColorScheme = darkColorScheme(
    primary = HighContrastPrimary,
    onPrimary = HighContrastOnPrimary,
    secondary = HighContrastSecondary,
    onSecondary = HighContrastOnSecondary,
    background = HighContrastBackground,
    onBackground = HighContrastText,
    surface = HighContrastSurface,
    onSurface = HighContrastText,
    error = HighContrastError,
    onError = HighContrastOnError
)

@Composable
fun AccessibleShoppingListTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    highContrast: Boolean = false,
    largeText: Boolean = false, // We will use this to scale typography later if needed, or just rely on system font scale
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        highContrast -> HighContrastColorScheme
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // If largeText is enabled, we could programmatically scale the typography here.
    // For now, we'll rely on the user's system font scale, but we can enforce a minimum scale if requested.
    // A simple way is to copy the typography and multiply font sizes.
    val typography = if (largeText) {
        Typography.copy(
            titleLarge = Typography.titleLarge.copy(fontSize = Typography.titleLarge.fontSize * 1.25),
            titleMedium = Typography.titleMedium.copy(fontSize = Typography.titleMedium.fontSize * 1.25),
            bodyLarge = Typography.bodyLarge.copy(fontSize = Typography.bodyLarge.fontSize * 1.25),
            bodyMedium = Typography.bodyMedium.copy(fontSize = Typography.bodyMedium.fontSize * 1.25),
            labelLarge = Typography.labelLarge.copy(fontSize = Typography.labelLarge.fontSize * 1.25)
        )
    } else {
        Typography
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}
