package me.khruslan.cryptograph.ui.notifications.main

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.khruslan.cryptograph.base.Logger
import me.khruslan.cryptograph.data.core.DataException
import me.khruslan.cryptograph.data.notifications.interactors.coin.CoinNotification
import me.khruslan.cryptograph.data.notifications.interactors.coin.CoinNotificationsInteractor
import me.khruslan.cryptograph.data.notifications.interactors.completed.CompletedNotificationsInteractor
import me.khruslan.cryptograph.ui.util.UiState
import me.khruslan.cryptograph.ui.util.displayMessageRes

private const val LOG_TAG = "NotificationsViewModel"

internal class NotificationsViewModel(
    savedStateHandle: SavedStateHandle,
    private val coinNotificationsInteractor: CoinNotificationsInteractor,
    private val completedNotificationsInteractor: CompletedNotificationsInteractor,
) : ViewModel() {

    private val args = NotificationsArgs.fromSavedStateHandle(savedStateHandle)

    private val _notificationsState = MutableNotificationsState(args)
    val notificationsState: NotificationsState = _notificationsState

    init {
        loadNotifications()
    }

    fun reloadNotifications() {
        _notificationsState.listState = UiState.Loading
        loadNotifications()
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            try {
                coinNotificationsInteractor
                    .getCoinNotifications(args.coinId)
                    .collect { notifications ->
                        Logger.info(LOG_TAG, "Observed ${notifications.count()} notification(s)")
                        _notificationsState.listState = UiState.Data(notifications)
                        refreshNotifications()
                    }
            } catch (e: DataException) {
                _notificationsState.listState = UiState.Error(e.displayMessageRes)
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
internal interface NotificationsState {
    val coinName: String?
    val listState: UiState<List<CoinNotification>>
}

internal class MutableNotificationsState(args: NotificationsArgs) : NotificationsState {
    override val coinName: String? = args.coinName
    override var listState: UiState<List<CoinNotification>> by mutableStateOf(UiState.Loading)
}