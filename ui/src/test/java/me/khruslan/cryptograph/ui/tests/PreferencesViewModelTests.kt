package me.khruslan.cryptograph.ui.tests

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import me.khruslan.cryptograph.base.AppVersion
import me.khruslan.cryptograph.data.fixtures.STUB_PREFERENCES
import me.khruslan.cryptograph.data.preferences.ChartPeriod
import me.khruslan.cryptograph.data.preferences.ChartStyle
import me.khruslan.cryptograph.data.preferences.Theme
import me.khruslan.cryptograph.ui.R
import me.khruslan.cryptograph.ui.fakes.FakePreferencesRepository
import me.khruslan.cryptograph.ui.preferences.main.PreferencesViewModel
import me.khruslan.cryptograph.ui.rules.MainDispatcherRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private const val APP_VERSION_NAME = "1.0.0"

internal class PreferencesViewModelTests {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakePreferencesRepository: FakePreferencesRepository
    private lateinit var viewModel: PreferencesViewModel

    @Before
    fun setUp() {
        val appVersion = AppVersion(APP_VERSION_NAME)
        fakePreferencesRepository = FakePreferencesRepository()
        viewModel = PreferencesViewModel(appVersion, fakePreferencesRepository)
    }

    @Test
    fun `Get app version`() {
        val appVersion = viewModel.preferencesState.appVersion
        assertThat(appVersion).isEqualTo(APP_VERSION_NAME)
    }

    @Test
    fun `Update theme - success`() = runTest {
        val theme = Theme.Light
        viewModel.updateTheme(theme)

        val expectedPreferences = STUB_PREFERENCES.copy(theme = theme)
        val actualPreferences = viewModel.preferencesState.preferences
        assertThat(actualPreferences).isEqualTo(expectedPreferences)
    }

    @Test
    fun `Update theme - failure`() = runTest {
        fakePreferencesRepository.isDatabaseCorrupted = true
        viewModel.updateTheme(Theme.Dark)

        val expectedWarningMessageRes = R.string.update_theme_warning_msg
        val actualWarningMessageRes = viewModel.preferencesState.warningMessageRes
        assertThat(actualWarningMessageRes).isEqualTo(expectedWarningMessageRes)
    }

    @Test
    fun `Update chart style - success`() = runTest {
        val chartStyle = ChartStyle.Graph
        viewModel.updateChartStyle(chartStyle)

        val expectedPreferences = STUB_PREFERENCES.copy(chartStyle = chartStyle)
        val actualPreferences = viewModel.preferencesState.preferences
        assertThat(actualPreferences).isEqualTo(expectedPreferences)
    }

    @Test
    fun `Update chart style - failure`() = runTest {
        fakePreferencesRepository.isDatabaseCorrupted = true
        viewModel.updateChartStyle(ChartStyle.Default)

        val expectedWarningMessageRes = R.string.update_chart_style_warning_msg
        val actualWarningMessageRes = viewModel.preferencesState.warningMessageRes
        assertThat(actualWarningMessageRes).isEqualTo(expectedWarningMessageRes)
    }

    @Test
    fun `Update chart period - success`() = runTest {
        val chartPeriod = ChartPeriod.ThreeMonths
        viewModel.updateChartPeriod(chartPeriod)

        val expectedPreferences = STUB_PREFERENCES.copy(chartPeriod = chartPeriod)
        val actualPreferences = viewModel.preferencesState.preferences
        assertThat(actualPreferences).isEqualTo(expectedPreferences)
    }

    @Test
    fun `Update chart period - failure`() = runTest {
        fakePreferencesRepository.isDatabaseCorrupted = true
        viewModel.updateChartPeriod(ChartPeriod.FiveYears)

        val expectedWarningMessageRes = R.string.update_chart_period_warning_msg
        val actualWarningMessageRes = viewModel.preferencesState.warningMessageRes
        assertThat(actualWarningMessageRes).isEqualTo(expectedWarningMessageRes)
    }

    @Test
    fun `Warning shown`() {
        fakePreferencesRepository.isDatabaseCorrupted = true
        viewModel.updateTheme(Theme.Light)
        viewModel.warningShown()

        assertThat(viewModel.preferencesState.warningMessageRes).isNull()
    }
}