package me.khruslan.cryptograph.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

@Composable
internal fun CryptoGraphTheme(content: @Composable () -> Unit) {
    val colorScheme = if (isSystemInDarkTheme()) {
        darkColorScheme()
    } else {
        lightColorScheme()
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CryptoGraphTypography
    ) {
        content()
    }
}

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

private val DefaultTypography = Typography()

private val CryptoGraphTypography = Typography(
    headlineLarge = DefaultTypography.headlineLarge.copy(fontFamily = NunitoFontFamily),
    headlineMedium = DefaultTypography.headlineMedium.copy(fontFamily = NunitoFontFamily),
    headlineSmall = DefaultTypography.headlineSmall.copy(fontFamily = NunitoFontFamily),
    titleLarge = DefaultTypography.titleLarge.copy(fontFamily = Exo2FontFamily),
    titleMedium = DefaultTypography.titleMedium.copy(fontFamily = Exo2FontFamily),
    titleSmall = DefaultTypography.titleSmall.copy(fontFamily = Exo2FontFamily),
    bodyLarge = DefaultTypography.bodyLarge.copy(fontFamily = NunitoFontFamily),
    bodyMedium = DefaultTypography.bodyMedium.copy(fontFamily = NunitoFontFamily),
    bodySmall = DefaultTypography.bodySmall.copy(fontFamily = NunitoFontFamily),
    labelLarge = DefaultTypography.labelLarge.copy(fontFamily = NunitoFontFamily),
    labelMedium = DefaultTypography.labelMedium.copy(fontFamily = NunitoFontFamily),
    labelSmall = DefaultTypography.labelSmall.copy(fontFamily = NunitoFontFamily)
)