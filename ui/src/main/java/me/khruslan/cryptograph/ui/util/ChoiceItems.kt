package me.khruslan.cryptograph.ui.util

import androidx.annotation.StringRes
import me.khruslan.cryptograph.data.preferences.ChartPeriod
import me.khruslan.cryptograph.data.preferences.ChartStyle
import me.khruslan.cryptograph.data.preferences.Theme
import me.khruslan.cryptograph.ui.R

internal object ChoiceItems {

    val Themes
        get() = Theme.entries.map { theme ->
            ChoiceItem(theme, theme.labelRes)
        }

    val ChartStyles
        get() = ChartStyle.entries.map { chartStyle ->
            ChoiceItem(chartStyle, chartStyle.labelRes)
        }

    val ChartPeriods
        get() = ChartPeriod.entries.map { chartPeriod ->
            ChoiceItem(chartPeriod, chartPeriod.labelRes)
        }

    private val Theme.labelRes
        get() = when (this) {
            Theme.SystemDefault -> R.string.theme_system_default_label
            Theme.Light -> R.string.theme_light_label
            Theme.Dark -> R.string.theme_dark_label
        }

    private val ChartStyle.labelRes
        get() = when (this) {
            ChartStyle.Column -> R.string.chart_style_column_label
            ChartStyle.Line -> R.string.chart_style_line_label
        }

    private val ChartPeriod.labelRes
        get() = when (this) {
            ChartPeriod.OneWeek -> R.string.chart_period_one_week_label
            ChartPeriod.TwoWeeks -> R.string.chart_period_two_weeks_label
            ChartPeriod.OneMonth -> R.string.chart_period_one_month_label
            ChartPeriod.ThreeMonths -> R.string.chart_period_three_months_label
            ChartPeriod.SixMonths -> R.string.chart_period_six_months_label
            ChartPeriod.OneYear -> R.string.chart_period_one_year_label
            ChartPeriod.ThreeYears -> R.string.chart_period_three_years_label
            ChartPeriod.FiveYears -> R.string.chart_period_five_years_label
        }
}

internal data class ChoiceItem<T>(
    val value: T,
    @StringRes val labelRes: Int,
)