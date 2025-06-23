package com.jery.feedchart.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBDC2FF),
    onPrimary = Color(0xFF223178),
    primaryContainer = Color(0xFF39488F),
    onPrimaryContainer = Color(0xFFDEE1FF),
    secondary = Color(0xFFC3C5DD),
    onSecondary = Color(0xFF2D3042),
    secondaryContainer = Color(0xFF434759),
    onSecondaryContainer = Color(0xFFDFE1F9),
    tertiary = Color(0xFFE2BADD),
    onTertiary = Color(0xFF452749),
    tertiaryContainer = Color(0xFF5D3E61),
    onTertiaryContainer = Color(0xFFFED7FF),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF1B1B1F),
    onBackground = Color(0xFFE3E2E6),
    surface = Color(0xFF1B1B1F),
    onSurface = Color(0xFFE3E2E6),
    surfaceVariant = Color(0xFF45464F),
    onSurfaceVariant = Color(0xFFC5C6D0),
    outline = Color(0xFF8F909A),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFFE3E2E6),
    inverseOnSurface = Color(0xFF1B1B1F),
    inversePrimary = Color(0xFF5264AA),
    surfaceDim = Color(0xFF1B1B1F),
    surfaceBright = Color(0xFF45464F),
    surfaceContainerLowest = Color(0xFF16161A),
    surfaceContainerLow = Color(0xFF1F1E23),
    surfaceContainer = Color(0xFF232227),
    surfaceContainerHigh = Color(0xFF2E2D32),
    surfaceContainerHighest = Color(0xFF39383C),
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF5264AA),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFDEE1FF),
    onPrimaryContainer = Color(0xFF0D1C4D),
    secondary = Color(0xFF5A5F72),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE3E5FB),
    onSecondaryContainer = Color(0xFF171C2C),
    tertiary = Color(0xFF765579),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFED7FF),
    onTertiaryContainer = Color(0xFF2C1332),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFEFBFF),
    onBackground = Color(0xFF1B1B1F),
    surface = Color(0xFFFEFBFF),
    onSurface = Color(0xFF1B1B1F),
    surfaceVariant = Color(0xFFE1E2EC),
    onSurfaceVariant = Color(0xFF45464F),
    outline = Color(0xFF767680),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFF303034),
    inverseOnSurface = Color(0xFFF2F0F4),
    inversePrimary = Color(0xFFBDC2FF),
    surfaceDim = Color(0xFFDBD9DE),
    surfaceBright = Color(0xFFFEFBFF),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF5F3F8),
    surfaceContainer = Color(0xFFEFEDF2),
    surfaceContainerHigh = Color(0xFFE9E7ED),
    surfaceContainerHighest = Color(0xFFE3E2E6),
)

private val typography = Typography.copy(
    headlineMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        fontFamily = FontFamily.Serif
    ),
    labelSmall = TextStyle(
        fontSize = 16.sp,
        fontFamily = FontFamily.Serif
    )
)

@Composable
fun FeedChartTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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
        typography = typography,
        shapes = MaterialTheme.shapes.copy(
            small = RoundedCornerShape(24.dp),
            medium = RoundedCornerShape(24.dp),
            large = RoundedCornerShape(24.dp)
        ),
        content = content
    )
}