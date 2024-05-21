package me.khruslan.cryptograph.ui.common

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

private const val BACKGROUND_COLOR_LIGHT = 0xFFFFFBFE
private const val BACKGROUND_COLOR_DARK = 0xFF1C1B1F

private const val DEVICE_PHONE_LANDSCAPE =
    "spec:width = 411dp, height = 891dp, orientation = landscape, dpi = 420"

@Preview(
    name = "Light",
    showBackground = true,
    backgroundColor = BACKGROUND_COLOR_LIGHT
)
@Preview(
    name = "Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    backgroundColor = BACKGROUND_COLOR_DARK
)
internal annotation class PreviewLightDarkWithBackground

@Preview(
    name = "Phone - Portrait - Light",
    device = Devices.PHONE
)
@Preview(
    name = "Phone - Portrait - Dark",
    device = Devices.PHONE,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    name = "Phone - Landscape - Light",
    device = DEVICE_PHONE_LANDSCAPE
)
@Preview(
    name = "Phone - Landscape - Dark",
    device = DEVICE_PHONE_LANDSCAPE,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    name = "Tablet - Light",
    device = Devices.TABLET
)
@Preview(
    name = "Tablet - Dark",
    device = Devices.TABLET,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
internal annotation class PreviewScreenSizesLightDark