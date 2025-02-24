package me.khruslan.cryptograph.data.preferences

data class Preferences(
    val theme: Theme,
    val chartStyle: ChartStyle,
    val chartPeriod: ChartPeriod,
)

enum class Theme {
    Auto,
    Light,
    Dark
}

enum class ChartStyle {
    ColumnChart,
    LineChart
}

enum class ChartPeriod {
    OneWeek,
    TwoWeeks,
    OneMonth,
    ThreeMonths,
    SixMonths,
    OneYear,
    ThreeYears,
    FiveYears
}