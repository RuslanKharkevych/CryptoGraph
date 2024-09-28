package me.khruslan.cryptograph.ui.coins.history

import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.entry.ChartEntry
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.khruslan.cryptograph.base.Logger
import me.khruslan.cryptograph.data.coins.CoinPrice
import me.khruslan.cryptograph.data.preferences.ChartPeriod
import me.khruslan.cryptograph.data.preferences.ChartStyle
import me.khruslan.cryptograph.ui.util.getCurrentLocale
import java.time.Clock
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Locale

private const val LOG_TAG = "CoinHistoryChartState"

private const val MAX_ENTRIES_COUNT = 25
private const val MAX_BOTTOM_AXIS_ITEMS_COUNT = 7

private const val DAY_OF_MONTH_PATTERN = "d MMM"
private const val MONTH_OF_YEAR_PATTERN = "MMM yy"

internal interface CoinHistoryChartState {
    val model: ChartEntryModel
    val style: ChartStyle
    val period: ChartPeriod
    val dateFormatter: AxisValueFormatter<AxisPosition.Horizontal.Bottom>
    val bottomAxisSpacing: Int

    fun updateStyle(style: ChartStyle)
    fun updatePeriod(period: ChartPeriod)
}

@VisibleForTesting
internal class CoinHistoryChartStateImpl(
    private val chartData: List<CoinPrice>,
    private val externalScope: CoroutineScope,
    private val dispatcher: CoroutineDispatcher,
    private val locale: Locale,
    private val clock: Clock,
) : CoinHistoryChartState {

    override var style by mutableStateOf(ChartStyle.Column)
        private set

    override var period by mutableStateOf(ChartPeriod.OneWeek)
        private set

    override val model
        get() = entryModelProducer.requireModel()

    override val bottomAxisSpacing
        get() = calculateItemSpacing(
            itemsCount = filteredData.entries.count(),
            maxCount = MAX_BOTTOM_AXIS_ITEMS_COUNT
        )

    override val dateFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
        try {
            resolveFormattedDateForBottomAxisValue(value)
        } catch (e: IllegalStateException) {
            Logger.error(LOG_TAG, "Failed to resolve formatted date for bottom axis value", e)
            ""
        }
    }

    private val entryModelProducer = ChartEntryModelProducer(
        emptyList<ChartEntry>(),
        dispatcher = dispatcher
    )

    private var filteredData = emptyMap<Int, CoinPrice>()

    init {
        updateEntries()
    }

    override fun updateStyle(style: ChartStyle) {
        this.style = style
    }

    override fun updatePeriod(period: ChartPeriod) {
        this.period = period
        updateEntries()
    }

    private fun updateEntries() {
        val datePeriod = period.datePeriod
        externalScope.launch(dispatcher) {
            filterChartDataByDatePeriod(datePeriod)
            val entries = filteredData.map { entryOf(it.key, it.value.price) }
            entryModelProducer.setEntriesSuspending(entries).await()
        }
    }

    private fun filterChartDataByDatePeriod(datePeriod: Period) {
        filteredData = chartData
            .filterByDatePeriod(datePeriod)
            .pruneToMaxCount()
            .associateByIndexReversed()
    }

    private fun List<CoinPrice>.filterByDatePeriod(datePeriod: Period): List<CoinPrice> {
        val minDate = LocalDate.now(clock).minus(datePeriod)
        return filter { it.date.isAfter(minDate) }
    }

    private fun List<CoinPrice>.pruneToMaxCount(): List<CoinPrice> {
        val spacing = calculateItemSpacing(count(), MAX_ENTRIES_COUNT)
        return filterIndexed { index, _ -> index % spacing == 0 }
    }

    private fun List<CoinPrice>.associateByIndexReversed(): Map<Int, CoinPrice> {
        return withIndex().associateBy({ count() - it.index }, { it.value })
    }

    private fun resolveFormattedDateForBottomAxisValue(value: Float): String {
        val item = filteredData[value.toInt()] ?: throw IllegalStateException(
            "Couldn't find an item for value $value in $filteredData"
        )
        val datePattern = getDatePattern()
        val dateFormatter = DateTimeFormatter.ofPattern(datePattern, locale)
        return item.date.format(dateFormatter)
    }

    private fun getDatePattern(): String {
        return if (period.datePeriod.years >= 1) {
            MONTH_OF_YEAR_PATTERN
        } else {
            DAY_OF_MONTH_PATTERN
        }
    }

    private fun calculateItemSpacing(itemsCount: Int, maxCount: Int): Int {
        var spacing = 0
        spacing += itemsCount / maxCount
        if (itemsCount % maxCount != 0) spacing++
        return spacing
    }
}

private val ChartPeriod.datePeriod
    get() = when (this) {
        ChartPeriod.OneWeek -> Period.ofWeeks(1)
        ChartPeriod.TwoWeeks -> Period.ofWeeks(2)
        ChartPeriod.OneMonth -> Period.ofMonths(1)
        ChartPeriod.ThreeMonths -> Period.ofMonths(3)
        ChartPeriod.SixMonths -> Period.ofMonths(6)
        ChartPeriod.OneYear -> Period.ofYears(1)
        ChartPeriod.ThreeYears -> Period.ofYears(3)
        ChartPeriod.FiveYears -> Period.ofYears(5)
    }

// TODO: Style and period must survive configuration changes
@Composable
internal fun rememberCoinHistoryChartState(coinHistory: List<CoinPrice>): CoinHistoryChartState {
    val coroutineScope = rememberCoroutineScope()
    val locale = getCurrentLocale()

    return remember {
        CoinHistoryChartStateImpl(
            chartData = coinHistory,
            externalScope = coroutineScope,
            dispatcher = Dispatchers.Default,
            locale = locale,
            clock = Clock.systemUTC()
        )
    }
}