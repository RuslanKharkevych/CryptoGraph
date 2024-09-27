package me.khruslan.cryptograph.data.fakes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import me.khruslan.cryptograph.data.fixtures.STUB_DTO_PREFERENCES
import me.khruslan.cryptograph.data.preferences.local.PreferencesDto
import me.khruslan.cryptograph.data.preferences.local.PreferencesLocalDataSource

internal class FakePreferencesLocalDataSource : PreferencesLocalDataSource {

    private val _preferences = MutableStateFlow(STUB_DTO_PREFERENCES)

    override val preferences: Flow<PreferencesDto>
        get() = _preferences

    override suspend fun updateTheme(themeValue: Int) {
        _preferences.update { preferences ->
            preferences.copy(themeValue = themeValue)
        }
    }

    override suspend fun updateChartStyle(chartStyleValue: Int) {
        _preferences.update { preferences ->
            preferences.copy(chartStyleValue = chartStyleValue)
        }
    }

    override suspend fun updateChartPeriod(chartPeriodValue: Int) {
        _preferences.update { preferences ->
            preferences.copy(chartPeriodValue = chartPeriodValue)
        }
    }
}