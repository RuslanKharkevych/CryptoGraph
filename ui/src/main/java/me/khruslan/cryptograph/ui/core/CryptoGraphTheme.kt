package me.khruslan.cryptograph.ui.core

import android.app.UiModeManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.getSystemService
import me.khruslan.cryptograph.data.preferences.Theme
import me.khruslan.cryptograph.ui.R

@Composable
internal fun CryptoGraphTheme(
    theme: Theme = Theme.Auto,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current

    LaunchedEffect(theme) {
        context.setTheme(theme)
    }

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

internal fun Context.setTheme(theme: Theme) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        setThemeApi31(theme)
    } else {
        setThemeApi23(theme)
    }
}

@RequiresApi(Build.VERSION_CODES.S)
private fun Context.setThemeApi31(theme: Theme) {
    val uiModeManager = getSystemService<UiModeManager>()
    if (uiModeManager != null) {
        val nightMode = getNightModeApi31(theme)
        uiModeManager.setApplicationNightMode(nightMode)
    } else {
        setThemeApi23(theme)
    }
}

private fun setThemeApi23(theme: Theme) {
    val nightMode = getNightModeApi23(theme)
    AppCompatDelegate.setDefaultNightMode(nightMode)
}

private fun getNightModeApi31(theme: Theme): Int {
    return when (theme) {
        Theme.Auto -> UiModeManager.MODE_NIGHT_AUTO
        Theme.Light -> UiModeManager.MODE_NIGHT_NO
        Theme.Dark -> UiModeManager.MODE_NIGHT_YES
    }
}

private fun getNightModeApi23(theme: Theme): Int {
    return when (theme) {
        Theme.Auto -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        Theme.Light -> AppCompatDelegate.MODE_NIGHT_NO
        Theme.Dark -> AppCompatDelegate.MODE_NIGHT_YES
    }
}