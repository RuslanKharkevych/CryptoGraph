package me.khruslan.cryptograph.data.tests

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import me.khruslan.cryptograph.data.fakes.FakePreferencesLocalDataSource
import me.khruslan.cryptograph.data.preferences.ChartPeriod
import me.khruslan.cryptograph.data.preferences.ChartStyle
import me.khruslan.cryptograph.data.preferences.PreferencesRepository
import me.khruslan.cryptograph.data.preferences.PreferencesRepositoryImpl
import me.khruslan.cryptograph.data.preferences.Theme
import me.khruslan.cryptograph.data.preferences.mapper.PreferencesMapper
import org.junit.Before
import org.junit.Test

internal class PreferencesRepositoryTests {

    private lateinit var repository: PreferencesRepository

    @Before
    fun setUp() {
        repository = PreferencesRepositoryImpl(
            localDataSource = FakePreferencesLocalDataSource(),
            mapper = PreferencesMapper()
        )
    }

    @Test
    fun `Update theme`() = runTest {
        val theme = Theme.Dark
        repository.updateTheme(theme)

        repository.preferences.test {
            val preferences = awaitItem()
            assertThat(preferences.theme).isEqualTo(theme)
        }
    }

    @Test
    fun `Update chart style`() = runTest {
        val chartStyle = ChartStyle.Line
        repository.updateChartStyle(chartStyle)

        repository.preferences.test {
            val preferences = awaitItem()
            assertThat(preferences.chartStyle).isEqualTo(chartStyle)
        }
    }

    @Test
    fun `Update chart period`() = runTest {
        val chartPeriod = ChartPeriod.ThreeYears
        repository.updateChartPeriod(chartPeriod)

        repository.preferences.test {
            val preferences = awaitItem()
            assertThat(preferences.chartPeriod).isEqualTo(chartPeriod)
        }
    }
}