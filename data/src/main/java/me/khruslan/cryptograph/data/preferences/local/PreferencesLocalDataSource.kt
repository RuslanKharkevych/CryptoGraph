package me.khruslan.cryptograph.data.preferences.local

import io.objectbox.Box
import io.objectbox.exception.DbException
import io.objectbox.kotlin.toFlow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import me.khruslan.cryptograph.base.Logger
import me.khruslan.cryptograph.data.core.DatabaseException

private const val LOG_TAG = "PreferencesLocalDataSource"

internal interface PreferencesLocalDataSource {
    val preferencesFlow: Flow<PreferencesDto>
    suspend fun getPreferences(): PreferencesDto
    suspend fun updateTheme(themeValue: Int)
    suspend fun updateChartStyle(chartStyleValue: Int)
    suspend fun updateChartPeriod(chartPeriodValue: Int)
}

internal class PreferencesLocalDataSourceImpl(
    private val box: Box<PreferencesDto>,
    private val dispatcher: CoroutineDispatcher,
) : PreferencesLocalDataSource {

    @OptIn(ExperimentalCoroutinesApi::class)
    override val preferencesFlow: Flow<PreferencesDto>
        get() {
            return box.query().build().subscribe().toFlow().map { preferences ->
                preferences.firstOrDefault()
            }.onEach { preferences ->
                Logger.info(LOG_TAG, "Observed preferences: $preferences")
            }
        }

    override suspend fun getPreferences(): PreferencesDto {
        return withContext(dispatcher) {
            try {
                getPreferencesInternal()
            } catch (e: DbException) {
                Logger.error(LOG_TAG, "Failed to load preferences", e)
                PreferencesDto()
            }
        }
    }

    override suspend fun updateTheme(themeValue: Int) {
        withContext(dispatcher) {
            try {
                updatePreferences { preferences ->
                    preferences.copy(themeValue = themeValue)
                }
                Logger.info(LOG_TAG, "Updated theme value: $themeValue")
            } catch (e: DbException) {
                Logger.error(LOG_TAG, "Failed to update theme value: $themeValue", e)
                throw DatabaseException(e)
            }
        }
    }

    override suspend fun updateChartStyle(chartStyleValue: Int) {
        withContext(dispatcher) {
            try {
                updatePreferences { preferences ->
                    preferences.copy(chartStyleValue = chartStyleValue)
                }
                Logger.info(LOG_TAG, "Updated chart style value: $chartStyleValue")
            } catch (e: DbException) {
                Logger.error(LOG_TAG, "Failed to update chart style value: $chartStyleValue", e)
                throw DatabaseException(e)
            }
        }
    }

    override suspend fun updateChartPeriod(chartPeriodValue: Int) {
        withContext(dispatcher) {
            try {
                updatePreferences { preferences ->
                    preferences.copy(chartPeriodValue = chartPeriodValue)
                }
                Logger.info(LOG_TAG, "Updated chart period value: $chartPeriodValue")
            } catch (e: DbException) {
                Logger.error(LOG_TAG, "Failed to update chart period value: $chartPeriodValue", e)
                throw DatabaseException(e)
            }
        }
    }

    private fun getPreferencesInternal(): PreferencesDto {
        return box.all.firstOrDefault()
    }

    private inline fun updatePreferences(transformation: (PreferencesDto) -> PreferencesDto) {
        val preferences = getPreferencesInternal()
        box.put(transformation(preferences))
    }

    private fun List<PreferencesDto>.firstOrDefault(): PreferencesDto {
        return firstOrNull() ?: PreferencesDto()
    }
}