package me.khruslan.cryptograph.ui.core

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import me.khruslan.cryptograph.ui.R

@Composable
internal fun CryptoGraphTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = cryptoGraphColorTheme(),
        typography = CryptoGraphTypography,
        content = content
    )
}

@Composable
private fun cryptoGraphColorTheme(): ColorScheme {
    return if (isSystemInDarkTheme()) {
        darkColorScheme()
    } else {
        lightColorScheme(errorContainer = Error80)
    }.run {
        copy(
            primary = onSurfaceVariant,
            primaryContainer = surfaceContainerHigh
        )
    }
}

private val Error80 = Color(red = 242, green = 184, blue = 181)

internal val DarkGreen = Color(0xFF00796B)
internal val DarkRed = Color(0xFFB71C1C)
internal val DarkYellow = Color(0xFF827717)

private val Exo2FontFamily = FontFamily(
    Font(R.font.exo2_medium, FontWeight.Medium)
)

private val NunitoFontFamily = FontFamily(
    Font(R.font.nunito_regular),
    Font(R.font.nunito_medium, FontWeight.Medium),
    Font(R.font.nunito_bold, FontWeight.Bold)
)

private val CryptoGraphTypography = Typography().run {
    copy(
        headlineLarge = headlineLarge.copy(fontFamily = NunitoFontFamily),
        headlineMedium = headlineMedium.copy(fontFamily = NunitoFontFamily),
        headlineSmall = headlineSmall.copy(fontFamily = NunitoFontFamily),
        titleLarge = titleLarge.copy(fontFamily = Exo2FontFamily),
        titleMedium = titleMedium.copy(fontFamily = Exo2FontFamily),
        titleSmall = titleSmall.copy(fontFamily = Exo2FontFamily),
        bodyLarge = bodyLarge.copy(fontFamily = NunitoFontFamily),
        bodyMedium = bodyMedium.copy(fontFamily = NunitoFontFamily),
        bodySmall = bodySmall.copy(fontFamily = NunitoFontFamily),
        labelLarge = labelLarge.copy(fontFamily = NunitoFontFamily),
        labelMedium = labelMedium.copy(fontFamily = NunitoFontFamily),
        labelSmall = labelSmall.copy(fontFamily = NunitoFontFamily)
    )
}