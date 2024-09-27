package me.khruslan.cryptograph.data.preferences.mapper

import me.khruslan.cryptograph.base.Logger
import me.khruslan.cryptograph.data.preferences.ChartPeriod
import me.khruslan.cryptograph.data.preferences.ChartStyle
import me.khruslan.cryptograph.data.preferences.Preferences
import me.khruslan.cryptograph.data.preferences.Theme
import me.khruslan.cryptograph.data.preferences.local.ChartPeriodValue
import me.khruslan.cryptograph.data.preferences.local.ChartStyleValue
import me.khruslan.cryptograph.data.preferences.local.PreferencesDto
import me.khruslan.cryptograph.data.preferences.local.ThemeValue

private const val LOG_TAG = "PreferencesMapper"

internal class PreferencesMapper {

    fun mapPreferences(preferences: PreferencesDto): Preferences {
        return Preferences(
            theme = mapTheme(preferences.themeValue),
            chartStyle = mapChartStyle(preferences.chartStyleValue),
            chartPeriod = mapChartPeriod(preferences.chartPeriodValue)
        )
    }

    fun mapTheme(theme: Theme): Int {
        return when (theme) {
            Theme.SystemDefault -> ThemeValue.SYSTEM_DEFAULT
            Theme.Light -> ThemeValue.LIGHT
            Theme.Dark -> ThemeValue.DARK
        }
    }

    fun mapChartStyle(chartStyle: ChartStyle): Int {
        return when (chartStyle) {
            ChartStyle.Default -> ChartStyleValue.DEFAULT
            ChartStyle.Graph -> ChartStyleValue.GRAPH
        }
    }

    fun mapChartPeriod(chartPeriod: ChartPeriod): Int {
        return when (chartPeriod) {
            ChartPeriod.OneWeek -> ChartPeriodValue.ONE_WEEK
            ChartPeriod.TwoWeeks -> ChartPeriodValue.TWO_WEEKS
            ChartPeriod.OneMonth -> ChartPeriodValue.ONE_MONTH
            ChartPeriod.ThreeMonths -> ChartPeriodValue.THREE_MONTHS
            ChartPeriod.SixMonths -> ChartPeriodValue.SIX_MONTHS
            ChartPeriod.OneYear -> ChartPeriodValue.ONE_YEAR
            ChartPeriod.ThreeYears -> ChartPeriodValue.THREE_YEARS
            ChartPeriod.FiveYears -> ChartPeriodValue.FIVE_YEARS
        }
    }

    private fun mapTheme(themeValue: Int): Theme {
        return try {
            mapThemeInternal(themeValue)
        } catch (e: IllegalArgumentException) {
            Logger.error(LOG_TAG, "Failed to map theme value: $themeValue", e)
            Theme.SystemDefault
        }
    }

    private fun mapChartStyle(chartStyleValue: Int): ChartStyle {
        return try {
            mapChartStyleInternal(chartStyleValue)
        } catch (e: IllegalArgumentException) {
            Logger.error(LOG_TAG, "Failed to map chart style value: $chartStyleValue", e)
            ChartStyle.Default
        }
    }

    private fun mapChartPeriod(chartPeriodValue: Int): ChartPeriod {
        return try {
            mapChartPeriodInternal(chartPeriodValue)
        } catch (e: IllegalArgumentException) {
            Logger.error(LOG_TAG, "Failed to map chart period value: $chartPeriodValue", e)
            ChartPeriod.OneWeek
        }
    }

    private fun mapThemeInternal(themeValue: Int): Theme {
        return when (themeValue) {
            ThemeValue.SYSTEM_DEFAULT -> Theme.SystemDefault
            ThemeValue.LIGHT -> Theme.Light
            ThemeValue.DARK -> Theme.Dark
            else -> throw IllegalArgumentException("Invalid theme value: $themeValue")
        }
    }

    private fun mapChartStyleInternal(chartStyleValue: Int) : ChartStyle {
        return when (chartStyleValue) {
            ChartStyleValue.DEFAULT -> ChartStyle.Default
            ChartStyleValue.GRAPH -> ChartStyle.Graph
            else -> throw IllegalArgumentException("Invalid char style value: $chartStyleValue")
        }
    }

    private fun mapChartPeriodInternal(chartPeriodValue: Int): ChartPeriod {
        return when (chartPeriodValue) {
            ChartPeriodValue.ONE_WEEK -> ChartPeriod.OneWeek
            ChartPeriodValue.TWO_WEEKS -> ChartPeriod.TwoWeeks
            ChartPeriodValue.ONE_MONTH -> ChartPeriod.OneMonth
            ChartPeriodValue.THREE_MONTHS -> ChartPeriod.ThreeMonths
            ChartPeriodValue.SIX_MONTHS -> ChartPeriod.SixMonths
            ChartPeriodValue.ONE_YEAR -> ChartPeriod.OneYear
            ChartPeriodValue.THREE_YEARS -> ChartPeriod.ThreeYears
            ChartPeriodValue.FIVE_YEARS -> ChartPeriod.FiveYears
            else -> throw IllegalArgumentException("Invalid chart period value: $chartPeriodValue")
        }
    }
}