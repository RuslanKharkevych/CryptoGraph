package me.khruslan.cryptograph.ui.data

import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryOf
import me.khruslan.cryptograph.data.coins.CoinPrice
import me.khruslan.cryptograph.ui.coins.history.CoinHistoryChartPeriod
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale
import kotlin.random.Random

internal class CoinHistoryChartStateDataProvider {

    val locale: Locale = Locale.ENGLISH

    val clock: Clock = Clock.fixed(
        Instant.parse("2024-05-26T16:45:42.00Z"),
        ZoneId.of("Europe/Paris")
    )

    val chartData = generateChartData()

    fun getChartEntriesFilteredByPeriod(period: CoinHistoryChartPeriod): List<List<FloatEntry>> {
        val filteredChartData = getChartDataFilteredByPeriod(period)
        return listOf(
            filteredChartData.mapIndexed { index, coinPrice ->
                entryOf(filteredChartData.count() - index, coinPrice.price)
            }
        )
    }

    fun getBottomAxisSpacingForPeriod(period: CoinHistoryChartPeriod): Int {
        return when (period) {
            CoinHistoryChartPeriod.OneWeek -> 1
            CoinHistoryChartPeriod.TwoWeeks -> 2
            CoinHistoryChartPeriod.OneMonth -> 3
            else -> 4
        }
    }

    private fun generateChartData(): List<CoinPrice> {
        val chartData = mutableListOf<CoinPrice>()
        var date = LocalDate.now(clock)
        val minDate = date.minusYears(5)

        while (date.isAfter(minDate)) {
            chartData += CoinPrice(
                date = date,
                price = Random.nextDouble(40_000.0, 60_000.0)
            )
            date = date.minusDays(1)
        }

        return chartData
    }

    private fun getChartDataFilteredByPeriod(period: CoinHistoryChartPeriod): List<CoinPrice> {
        return when (period) {
            CoinHistoryChartPeriod.OneWeek -> chartData.take(7)
            CoinHistoryChartPeriod.TwoWeeks -> chartData.take(14)
            CoinHistoryChartPeriod.OneMonth -> chartData.slice(0..29 step 2)
            CoinHistoryChartPeriod.ThreeMonths -> chartData.slice(0..90 step 4)
            CoinHistoryChartPeriod.SixMonths -> chartData.slice(0..180 step 8)
            CoinHistoryChartPeriod.OneYear -> chartData.slice(0..365 step 15)
            CoinHistoryChartPeriod.ThreeYears -> chartData.slice(0..1095 step 44)
            CoinHistoryChartPeriod.FiveYears -> chartData.slice(0..1825 step 74)
        }
    }
}