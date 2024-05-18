package me.khruslan.cryptograph.ui.coins

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import me.khruslan.cryptograph.data.coins.ChangeTrend
import me.khruslan.cryptograph.ui.R
import me.khruslan.cryptograph.ui.common.TrendingDown
import me.khruslan.cryptograph.ui.common.TrendingFlat
import me.khruslan.cryptograph.ui.common.TrendingUp

internal val ChangeTrend.icon
    get() = when (this) {
        ChangeTrend.UP -> Icons.AutoMirrored.Default.TrendingUp
        ChangeTrend.DOWN -> Icons.AutoMirrored.Default.TrendingDown
        ChangeTrend.STEADY_OR_UNKNOWN -> Icons.AutoMirrored.Default.TrendingFlat
    }

@get:StringRes
internal val ChangeTrend.contendDescRes
    get() = when (this) {
        ChangeTrend.UP -> R.string.trending_up_icon_desc
        ChangeTrend.DOWN -> R.string.trending_down_icon_desc
        ChangeTrend.STEADY_OR_UNKNOWN -> R.string.trending_flat_icon_desc
    }

// TODO: Update colors
internal val ChangeTrend.color
    get() = when (this) {
        ChangeTrend.UP -> Color.Green
        ChangeTrend.DOWN -> Color.Red
        ChangeTrend.STEADY_OR_UNKNOWN -> Color.Yellow
    }