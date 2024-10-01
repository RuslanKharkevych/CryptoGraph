package me.khruslan.cryptograph.ui.coins.picker

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.khruslan.cryptograph.data.coins.Coin
import me.khruslan.cryptograph.data.coins.CoinsRepository
import me.khruslan.cryptograph.data.common.DataException
import me.khruslan.cryptograph.data.interactors.notifications.completed.CompletedNotificationsInteractor
import me.khruslan.cryptograph.ui.util.UiState
import me.khruslan.cryptograph.ui.util.displayMessageRes

internal class CoinPickerViewModel(
    savedStateHandle: SavedStateHandle,
    private val coinsRepository: CoinsRepository,
    private val completedNotificationsInteractor: CompletedNotificationsInteractor,
) : ViewModel() {

    private val args = CoinPickerArgs.fromSavedStateHandle(savedStateHandle)

    private val _coinsState = MutableCoinsState(args)
    val coinsState: CoinsState = _coinsState

    init {
        loadCoins()
    }

    fun reloadCoins() {
        _coinsState.listState = UiState.Loading
        loadCoins()
    }

    private fun loadCoins() {
        viewModelScope.launch {
            try {
                coinsRepository.getCoins().collect { coins ->
                    _coinsState.listState = UiState.Data(coins)
                    refreshNotifications()
                }
            } catch (e: DataException) {
                _coinsState.listState = UiState.Error(e.displayMessageRes)
            }
        }
    }

    private fun refreshNotifications() {
        viewModelScope.launch {
            completedNotificationsInteractor.tryRefreshCompletedNotifications()
        }
    }
}

@Stable
internal interface CoinsState {
    val selectedCoinId: String?
    val listState: UiState<List<Coin>>
}

internal class MutableCoinsState(args: CoinPickerArgs) : CoinsState {
    override val selectedCoinId: String? = args.selectedCoinId
    override var listState: UiState<List<Coin>> by mutableStateOf(UiState.Loading)
}