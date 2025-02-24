package me.khruslan.cryptograph.ui.coins.main

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.khruslan.cryptograph.base.Logger
import me.khruslan.cryptograph.data.coins.Coin
import me.khruslan.cryptograph.data.coins.CoinsRepository
import me.khruslan.cryptograph.data.core.DataException
import me.khruslan.cryptograph.data.notifications.interactors.completed.CompletedNotificationsInteractor
import me.khruslan.cryptograph.data.notifications.repository.NotificationsRepository
import me.khruslan.cryptograph.ui.R
import me.khruslan.cryptograph.ui.util.UiState
import me.khruslan.cryptograph.ui.util.displayMessageRes

private const val LOG_TAG = "CoinsViewModel"

internal class CoinsViewModel(
    private val coinsRepository: CoinsRepository,
    private val notificationsRepository: NotificationsRepository,
    private val completedNotificationsInteractor: CompletedNotificationsInteractor,
) : ViewModel() {

    private val _coinsState = MutableCoinsState()
    val coinsState: CoinsState = _coinsState

    init {
        loadCoins()
        loadUnreadNotifications()
    }

    fun reloadCoins() {
        _coinsState.listState = UiState.Loading
        loadCoins()
    }

    fun pinCoin(id: String) {
        viewModelScope.launch {
            try {
                coinsRepository.pinCoin(id)
            } catch (_: DataException) {
                _coinsState.warningMessageRes = R.string.pin_coin_warning_msg
            }
        }
    }

    fun unpinCoin(id: String) {
        viewModelScope.launch {
            try {
                coinsRepository.unpinCoin(id)
            } catch (_: DataException) {
                _coinsState.warningMessageRes = R.string.unpin_coin_warning_msg
            }
        }
    }

    fun warningShown() {
        _coinsState.warningMessageRes = null
    }

    private fun loadCoins() {
        viewModelScope.launch {
            try {
                coinsRepository.getCoins().collect { coins ->
                    Logger.info(LOG_TAG, "Observed ${coins.count()} coins")
                    _coinsState.listState = UiState.Data(coins)
                    refreshNotifications()
                }
            } catch (e: DataException) {
                _coinsState.listState = UiState.Error(e.displayMessageRes)
            }
        }
    }

    private fun loadUnreadNotifications() {
        viewModelScope.launch {
            notificationsRepository.getNotifications().collect { notifications ->
                _coinsState.unreadNotificationsCount = notifications.count { notification ->
                    !notification.isPending
                }
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
    val listState: UiState<List<Coin>>
    val warningMessageRes: Int?
    val unreadNotificationsCount: Int
}

internal class MutableCoinsState : CoinsState {
    override var listState: UiState<List<Coin>> by mutableStateOf(UiState.Loading)
    override var warningMessageRes: Int? by mutableStateOf(null)
    override var unreadNotificationsCount: Int by mutableIntStateOf(0)
}