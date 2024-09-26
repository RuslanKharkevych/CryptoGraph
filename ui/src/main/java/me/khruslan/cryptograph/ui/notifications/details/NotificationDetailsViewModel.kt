package me.khruslan.cryptograph.ui.notifications.details

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.khruslan.cryptograph.data.common.DataException
import me.khruslan.cryptograph.data.notifications.Notification
import me.khruslan.cryptograph.data.notifications.NotificationsRepository
import me.khruslan.cryptograph.ui.coins.shared.CoinInfo
import me.khruslan.cryptograph.ui.util.UiState
import me.khruslan.cryptograph.ui.util.displayMessageRes

internal class NotificationDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val notificationsRepository: NotificationsRepository,
) : ViewModel() {

    private val args = NotificationDetailsArgs.fromSavedStateHandle(savedStateHandle)

    private val _notificationDetailsState = MutableNotificationDetailsState(args)
    val notificationDetailsState: NotificationDetailsState = _notificationDetailsState

    init {
        if (args.notificationId == 0L) {
            _notificationDetailsState.notificationState = UiState.Data(null)
        } else {
            loadNotification()
        }
    }

    fun reloadNotification() {
        _notificationDetailsState.notificationState = UiState.Loading
        loadNotification()
    }

    fun deleteNotification() {
        // TODO: Implement logic of deleting notification
    }

    fun updateCoinInfo(coinInfo: CoinInfo) {
        _notificationDetailsState.coinInfo = coinInfo
    }

    private fun loadNotification() {
        viewModelScope.launch {
            try {
                val notification = notificationsRepository.getNotification(args.notificationId)
                _notificationDetailsState.notificationState = UiState.Data(notification)
            } catch (e: DataException) {
                _notificationDetailsState.notificationState = UiState.Error(e.displayMessageRes)
            }
        }
    }
}

@Stable
internal interface NotificationDetailsState {
    val notificationTitle: String?
    val isDeletable: Boolean
    val isCoinEditable: Boolean
    val coinInfo: CoinInfo
    val notificationState: UiState<Notification?>
}

internal class MutableNotificationDetailsState(
    args: NotificationDetailsArgs
) : NotificationDetailsState {
    override val notificationTitle: String? = args.notificationTitle
    override val isDeletable: Boolean = args.notificationId != 0L
    override val isCoinEditable: Boolean = args.coinEditable
    override var coinInfo: CoinInfo by mutableStateOf(CoinInfo.fromArgs(args))
    override var notificationState: UiState<Notification?> by mutableStateOf(UiState.Loading)
}