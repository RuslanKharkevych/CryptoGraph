package me.khruslan.cryptograph.ui.notifications.main

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.khruslan.cryptograph.data.common.DataException
import me.khruslan.cryptograph.data.interactors.combine.CoinNotification
import me.khruslan.cryptograph.data.interactors.combine.CoinNotificationsInteractor
import me.khruslan.cryptograph.ui.util.UiState
import me.khruslan.cryptograph.ui.util.displayMessageRes

internal class NotificationsViewModel(
    savedStateHandle: SavedStateHandle,
    private val coinNotificationsInteractor: CoinNotificationsInteractor,
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
                        _notificationsState.listState = UiState.Data(notifications)
                    }
            } catch (e: DataException) {
                _notificationsState.listState = UiState.Error(e.displayMessageRes)
            }
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