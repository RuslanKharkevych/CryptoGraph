package me.khruslan.cryptograph.ui.fakes

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import me.khruslan.cryptograph.data.common.DataException
import me.khruslan.cryptograph.data.common.ErrorType
import me.khruslan.cryptograph.data.fixtures.STUB_PREFERENCES
import me.khruslan.cryptograph.data.preferences.ChartPeriod
import me.khruslan.cryptograph.data.preferences.ChartStyle
import me.khruslan.cryptograph.data.preferences.Preferences
import me.khruslan.cryptograph.data.preferences.PreferencesRepository
import me.khruslan.cryptograph.data.preferences.Theme

internal class FakePreferencesRepository : PreferencesRepository {

    var isDatabaseCorrupted = false

    override val preferencesFlow = MutableStateFlow(STUB_PREFERENCES)

    override suspend fun getPreferences(): Preferences {
        return preferencesFlow.value
    }

    override suspend fun updateTheme(theme: Theme) {
        checkIfDataIsValid()
        preferencesFlow.update { it.copy(theme = theme) }
    }

    override suspend fun updateChartStyle(chartStyle: ChartStyle) {
        checkIfDataIsValid()
        preferencesFlow.update { it.copy(chartStyle = chartStyle) }
    }

    override suspend fun updateChartPeriod(chartPeriod: ChartPeriod) {
        checkIfDataIsValid()
        preferencesFlow.update { it.copy(chartPeriod = chartPeriod) }
    }

    private fun checkIfDataIsValid() {
        if (isDatabaseCorrupted) throw object : DataException(ErrorType.Database) {}
    }
}