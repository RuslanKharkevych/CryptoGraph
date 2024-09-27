package me.khruslan.cryptograph.data.preferences.local

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
internal data class PreferencesDto(
    @Id var id: Long = 0,
    var themeValue: Int = 0,
    var chartStyleValue: Int = 0,
    var chartPeriodValue: Int = 0,
)

internal object ThemeValue {
    const val SYSTEM_DEFAULT = 0
    const val LIGHT = 1
    const val DARK = 2
}

internal object ChartStyleValue {
    const val DEFAULT = 0
    const val GRAPH = 1
}

internal object ChartPeriodValue {
    const val ONE_WEEK = 0
    const val TWO_WEEKS = 1
    const val ONE_MONTH = 2
    const val THREE_MONTHS = 3
    const val SIX_MONTHS = 4
    const val ONE_YEAR = 5
    const val THREE_YEARS = 6
    const val FIVE_YEARS = 7
}