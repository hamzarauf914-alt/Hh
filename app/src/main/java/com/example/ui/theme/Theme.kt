package com.example.ui.theme

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
    primary = DarkPrimary,
    onPrimary = Color(0xFF003915),
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = AmberLoyaltyLight,
    onSecondary = Color(0xFF451A03),
    secondaryContainer = Color(0xFF78350F),
    onSecondaryContainer = AmberLoyaltyContainer,
    tertiary = FreshTeal,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onBackground = Color(0xFFE2E8E3),
    onSurface = Color(0xFFE2E8E3),
    outline = Color(0xFF3B4A3E)
)

private val LightColorScheme = lightColorScheme(
    primary = FreshGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = FreshGreenContainer,
    onPrimaryContainer = FreshGreenOnContainer,
    secondary = AmberLoyalty,
    onSecondary = Color.White,
    secondaryContainer = AmberLoyaltyContainer,
    onSecondaryContainer = AmberLoyaltyOnContainer,
    tertiary = FreshTeal,
    onTertiary = Color.White,
    tertiaryContainer = FreshTealContainer,
    onTertiaryContainer = FreshTealOnContainer,
    background = WarmBackground,
    surface = SurfaceCard,
    surfaceVariant = SurfaceVariantLight,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline = OutlineBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Default to our branded grocery colors for consistency and distinctiveness
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
