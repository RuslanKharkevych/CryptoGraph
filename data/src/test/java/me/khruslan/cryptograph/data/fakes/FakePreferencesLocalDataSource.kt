package me.khruslan.cryptograph.data.fakes

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import me.khruslan.cryptograph.data.fixtures.STUB_DTO_PREFERENCES
import me.khruslan.cryptograph.data.preferences.local.PreferencesDto
import me.khruslan.cryptograph.data.preferences.local.PreferencesLocalDataSource

internal class FakePreferencesLocalDataSource : PreferencesLocalDataSource {

    override val preferencesFlow = MutableStateFlow(STUB_DTO_PREFERENCES)

    override suspend fun getPreferences(): PreferencesDto {
        return preferencesFlow.value
    }

    override suspend fun updateTheme(themeValue: Int) {
        preferencesFlow.update { preferences ->
            preferences.copy(themeValue = themeValue)
        }
    }

    override suspend fun updateChartStyle(chartStyleValue: Int) {
        preferencesFlow.update { preferences ->
            preferences.copy(chartStyleValue = chartStyleValue)
        }
    }

    override suspend fun updateChartPeriod(chartPeriodValue: Int) {
        preferencesFlow.update { preferences ->
            preferences.copy(chartPeriodValue = chartPeriodValue)
        }
    }
}