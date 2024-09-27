package me.khruslan.cryptograph.data.fixtures

import me.khruslan.cryptograph.data.preferences.ChartPeriod
import me.khruslan.cryptograph.data.preferences.ChartStyle
import me.khruslan.cryptograph.data.preferences.Preferences
import me.khruslan.cryptograph.data.preferences.Theme
import me.khruslan.cryptograph.data.preferences.local.ChartPeriodValue
import me.khruslan.cryptograph.data.preferences.local.ChartStyleValue
import me.khruslan.cryptograph.data.preferences.local.PreferencesDto
import me.khruslan.cryptograph.data.preferences.local.ThemeValue

val PREVIEW_PREFERENCES
    get() = STUB_PREFERENCES

internal val STUB_PREFERENCES
    get() = Preferences(
        theme = Theme.Dark,
        chartStyle = ChartStyle.Default,
        chartPeriod = ChartPeriod.OneMonth
    )

internal val STUB_DTO_PREFERENCES
    get() = PreferencesDto(
        themeValue = ThemeValue.DARK,
        chartStyleValue = ChartStyleValue.DEFAULT,
        chartPeriodValue = ChartPeriodValue.ONE_MONTH
    )