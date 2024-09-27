package me.khruslan.cryptograph.ui.fakes

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import me.khruslan.cryptograph.data.common.DataException
import me.khruslan.cryptograph.data.common.ErrorType
import me.khruslan.cryptograph.data.fixtures.STUB_PREFERENCES
import me.khruslan.cryptograph.data.preferences.ChartPeriod
import me.khruslan.cryptograph.data.preferences.ChartStyle
import me.khruslan.cryptograph.data.preferences.PreferencesRepository
import me.khruslan.cryptograph.data.preferences.Theme

internal class FakePreferencesRepository : PreferencesRepository {

    var isDatabaseCorrupted = false

    override val preferences = MutableStateFlow(STUB_PREFERENCES)

    override suspend fun updateTheme(theme: Theme) {
        checkIfDataIsValid()
        preferences.update { it.copy(theme = theme) }
    }

    override suspend fun updateChartStyle(chartStyle: ChartStyle) {
        checkIfDataIsValid()
        preferences.update { it.copy(chartStyle = chartStyle) }
    }

    override suspend fun updateChartPeriod(chartPeriod: ChartPeriod) {
        checkIfDataIsValid()
        preferences.update { it.copy(chartPeriod = chartPeriod) }
    }

    private fun checkIfDataIsValid() {
        if (isDatabaseCorrupted) throw object : DataException(ErrorType.Database) {}
    }
}