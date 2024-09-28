package me.khruslan.cryptograph.ui.tests

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import me.khruslan.cryptograph.data.preferences.ChartPeriod
import me.khruslan.cryptograph.data.preferences.ChartStyle
import me.khruslan.cryptograph.ui.coins.history.CoinHistoryChartState
import me.khruslan.cryptograph.ui.coins.history.CoinHistoryChartStateImpl
import me.khruslan.cryptograph.ui.data.CoinHistoryChartStateDataProvider
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Enclosed::class)
internal class CoinHistoryChartStateTests {

    private companion object {
        private lateinit var dataProvider: CoinHistoryChartStateDataProvider

        @BeforeClass
        @JvmStatic
        fun setUp() {
            dataProvider = CoinHistoryChartStateDataProvider()
        }
    }

    abstract class CoreTests {
        protected lateinit var chartState: CoinHistoryChartState

        @OptIn(ExperimentalCoroutinesApi::class)
        @Before
        fun setUp() {
            chartState = CoinHistoryChartStateImpl(
                chartData = dataProvider.chartData,
                externalScope = TestScope(),
                dispatcher = UnconfinedTestDispatcher(),
                locale = dataProvider.locale,
                clock = dataProvider.clock
            )
        }
    }

    @RunWith(Parameterized::class)
    class StyleTests(private val style: ChartStyle) : CoreTests() {

        companion object {
            @JvmStatic
            @Parameterized.Parameters(name = "{0}")
            fun data(): Collection<Array<Any>> {
                return ChartStyle.entries.map { arrayOf(it) }
            }
        }

        @Test
        fun `Update style to `() {
            chartState.updateStyle(style)
            assertThat(chartState.style).isEqualTo(style)
        }
    }

    @RunWith(Parameterized::class)
    class PeriodTests(private val period: ChartPeriod) : CoreTests() {

        companion object {
            @JvmStatic
            @Parameterized.Parameters(name = "{0}")
            fun data(): Collection<Array<Any>> {
                return ChartPeriod.entries.map { arrayOf(it) }
            }
        }

        @Test
        fun `Update period to `() {
            chartState.updatePeriod(period)
            assertThat(chartState.period).isEqualTo(period)

            val expectedEntries = dataProvider.getChartEntriesFilteredByPeriod(period)
            val actualEntries = chartState.model.entries
            assertThat(actualEntries).isEqualTo(expectedEntries)

            val expectedSpacing = dataProvider.getBottomAxisSpacingForPeriod(period)
            val actualSpacing = chartState.bottomAxisSpacing
            assertThat(actualSpacing).isEqualTo(expectedSpacing)
        }
    }
}