package me.khruslan.cryptograph.data.preferences

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.khruslan.cryptograph.data.preferences.local.PreferencesLocalDataSource
import me.khruslan.cryptograph.data.preferences.mapper.PreferencesMapper

interface PreferencesRepository {
    val preferencesFlow: Flow<Preferences>
    suspend fun getPreferences(): Preferences
    suspend fun updateTheme(theme: Theme)
    suspend fun updateChartStyle(chartStyle: ChartStyle)
    suspend fun updateChartPeriod(chartPeriod: ChartPeriod)
}

internal class PreferencesRepositoryImpl(
    private val localDataSource: PreferencesLocalDataSource,
    private val mapper: PreferencesMapper,
) : PreferencesRepository {

    override val preferencesFlow: Flow<Preferences>
        get() = localDataSource.preferencesFlow.map { preferences ->
            mapper.mapPreferences(preferences)
        }

    override suspend fun getPreferences(): Preferences {
        val preferences = localDataSource.getPreferences()
        return mapper.mapPreferences(preferences)
    }

    override suspend fun updateTheme(theme: Theme) {
        val themeValue = mapper.mapTheme(theme)
        localDataSource.updateTheme(themeValue)
    }

    override suspend fun updateChartStyle(chartStyle: ChartStyle) {
        val chartStyleValue = mapper.mapChartStyle(chartStyle)
        localDataSource.updateChartStyle(chartStyleValue)
    }

    override suspend fun updateChartPeriod(chartPeriod: ChartPeriod) {
        val chartPeriodValue = mapper.mapChartPeriod(chartPeriod)
        localDataSource.updateChartPeriod(chartPeriodValue)
    }
}