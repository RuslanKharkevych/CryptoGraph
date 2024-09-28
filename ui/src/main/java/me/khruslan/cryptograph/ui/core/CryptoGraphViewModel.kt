package me.khruslan.cryptograph.ui.core

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.khruslan.cryptograph.data.preferences.PreferencesRepository
import me.khruslan.cryptograph.data.preferences.Theme

internal class CryptoGraphViewModel(
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {

    private val _appState = MutableAppState(loadTheme())
    val appState: AppState = _appState

    init {
        observeTheme()
    }

    private fun observeTheme() {
        viewModelScope.launch {
            preferencesRepository.preferencesFlow.collect { preferences ->
                _appState.theme = preferences.theme
            }
        }
    }

    private fun loadTheme(): Theme {
        val preferences = runBlocking {
            preferencesRepository.getPreferences()
        }

        return preferences.theme
    }
}

@Stable
internal interface AppState {
    val theme: Theme
}

private class MutableAppState(theme: Theme) : AppState {
    override var theme: Theme by mutableStateOf(theme)
}