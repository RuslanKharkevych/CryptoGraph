package me.khruslan.cryptograph.ui.preferences.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.khruslan.cryptograph.base.AppVersion
import me.khruslan.cryptograph.data.core.DataException
import me.khruslan.cryptograph.data.preferences.ChartPeriod
import me.khruslan.cryptograph.data.preferences.ChartStyle
import me.khruslan.cryptograph.data.preferences.Preferences
import me.khruslan.cryptograph.data.preferences.PreferencesRepository
import me.khruslan.cryptograph.data.preferences.Theme
import me.khruslan.cryptograph.ui.R

internal class PreferencesViewModel(
    appVersion: AppVersion,
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {

    private val _preferencesState = MutablePreferencesState(appVersion.name)
    val preferencesState: PreferencesState = _preferencesState

    init {
        loadPreferences()
    }

    fun updateTheme(theme: Theme) {
        viewModelScope.launch {
            try {
                preferencesRepository.updateTheme(theme)
            } catch (_: DataException) {
                _preferencesState.warningMessageRes = R.string.update_theme_warning_msg
            }
        }
    }

    fun updateChartStyle(chartStyle: ChartStyle) {
        viewModelScope.launch {
            try {
                preferencesRepository.updateChartStyle(chartStyle)
            } catch (_: DataException) {
                _preferencesState.warningMessageRes = R.string.update_chart_style_warning_msg
            }
        }
    }

    fun updateChartPeriod(chartPeriod: ChartPeriod) {
        viewModelScope.launch {
            try {
                preferencesRepository.updateChartPeriod(chartPeriod)
            } catch (_: DataException) {
                _preferencesState.warningMessageRes = R.string.update_chart_period_warning_msg
            }
        }
    }

    fun warningShown() {
        _preferencesState.warningMessageRes = null
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            preferencesRepository.preferencesFlow.collect { preferences ->
                _preferencesState.preferences = preferences
            }
        }
    }
}

internal interface PreferencesState {
    val preferences: Preferences?
    val appVersion: String
    val warningMessageRes: Int?
}

internal class MutablePreferencesState(override val appVersion: String) : PreferencesState {
    override var preferences: Preferences? by mutableStateOf(null)
    override var warningMessageRes: Int? by mutableStateOf(null)
}