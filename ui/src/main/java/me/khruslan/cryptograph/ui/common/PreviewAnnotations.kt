package me.khruslan.cryptograph.ui.common

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    name = "Light",
    showBackground = true,
    backgroundColor = 0xFFFFFBFE
)
@Preview(
    name = "Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    showBackground = true,
    backgroundColor = 0xFF1C1B1F
)
internal annotation class PreviewLightDarkWithBackground