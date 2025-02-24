package me.khruslan.cryptograph.data.tests

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import me.khruslan.cryptograph.data.preferences.local.ChartPeriodValue
import me.khruslan.cryptograph.data.preferences.local.ChartStyleValue
import me.khruslan.cryptograph.data.preferences.local.PreferencesDto
import me.khruslan.cryptograph.data.preferences.local.PreferencesLocalDataSource
import me.khruslan.cryptograph.data.preferences.local.PreferencesLocalDataSourceImpl
import me.khruslan.cryptograph.data.preferences.local.ThemeValue
import me.khruslan.cryptograph.data.rules.ObjectBoxRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test

internal class PreferencesLocalDataSourceTests {

    @get:Rule
    val objectBoxRule = ObjectBoxRule()

    private lateinit var dataSource: PreferencesLocalDataSource

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        dataSource = PreferencesLocalDataSourceImpl(
            box = objectBoxRule.getBox(),
            dispatcher = UnconfinedTestDispatcher()
        )
    }

    @Test
    fun `Get preferences`() = runTest {
        val expectedPreferences = PreferencesDto()
        val actualPreferences = dataSource.getPreferences()

        assertThat(actualPreferences).isEqualTo(expectedPreferences)
    }

    @Test
    fun `Update theme`() = runTest {
        val themeValue = ThemeValue.LIGHT
        dataSource.updateTheme(themeValue)

        dataSource.preferencesFlow.test {
            val preferences = awaitItem()
            assertThat(preferences.themeValue).isEqualTo(themeValue)
        }
    }

    @Test
    fun `Update chart style`() = runTest {
        val chartStyleValue = ChartStyleValue.LINE_CHART
        dataSource.updateChartStyle(chartStyleValue)

        dataSource.preferencesFlow.test {
            val preferences = awaitItem()
            assertThat(preferences.chartStyleValue).isEqualTo(chartStyleValue)
        }
    }

    @Test
    fun `Update chart period`() = runTest {
        val chartPeriodValue = ChartPeriodValue.THREE_MONTHS
        dataSource.updateChartPeriod(chartPeriodValue)

        dataSource.preferencesFlow.test {
            val preferences = awaitItem()
            assertThat(preferences.chartPeriodValue).isEqualTo(chartPeriodValue)
        }
    }
}