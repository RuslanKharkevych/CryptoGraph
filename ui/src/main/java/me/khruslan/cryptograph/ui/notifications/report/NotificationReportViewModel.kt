package me.khruslan.cryptograph.ui.notifications.report

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.khruslan.cryptograph.data.core.DataException
import me.khruslan.cryptograph.data.notifications.Notification
import me.khruslan.cryptograph.data.notifications.NotificationStatus
import me.khruslan.cryptograph.data.notifications.NotificationsRepository
import me.khruslan.cryptograph.ui.R
import me.khruslan.cryptograph.ui.coins.shared.CoinInfo
import me.khruslan.cryptograph.ui.util.UiState
import me.khruslan.cryptograph.ui.util.displayMessageRes

internal class NotificationReportViewModel(
    savedStateHandle: SavedStateHandle,
    private val notificationsRepository: NotificationsRepository,
) : ViewModel() {

    private val args = NotificationReportArgs.fromSavedStateHandle(savedStateHandle)

    private val _notificationReportState = MutableNotificationReportState(args)
    val notificationReportState: NotificationReportState = _notificationReportState

    private var notificationDeleting = false

    init {
        loadNotification()
    }

    fun reloadNotification() {
        _notificationReportState.notificationState = UiState.Loading
        loadNotification()
    }

    fun deleteNotification() {
        if (notificationDeleting) return
        notificationDeleting = true

        viewModelScope.launch {
            try {
                notificationsRepository.deleteNotification(args.notificationId)
                _notificationReportState.notificationDeleted = true
            } catch (_: DataException) {
                _notificationReportState.warningMessageRes =
                    R.string.delete_notification_warning_msg
                notificationDeleting = false
            }
        }
    }

    fun warningShown() {
        _notificationReportState.warningMessageRes = null
    }

    private fun loadNotification() {
        viewModelScope.launch {
            try {
                val notification = notificationsRepository.getNotification(args.notificationId)
                _notificationReportState.notificationState = UiState.Data(notification)
            } catch (e: DataException) {
                _notificationReportState.notificationState = UiState.Error(e.displayMessageRes)
            }
        }
    }
}

@Stable
internal interface NotificationReportState {
    val coinInfo: CoinInfo
    val notificationStatus: NotificationStatus
    val notificationState: UiState<Notification>
    val notificationDeleting: Boolean
    val notificationDeleted: Boolean
    val warningMessageRes: Int?
}

internal class MutableNotificationReportState(
    args: NotificationReportArgs
) : NotificationReportState {
    override val coinInfo = CoinInfo.fromArgs(args)
    override val notificationStatus = args.notificationStatus
    override var notificationState by mutableStateOf<UiState<Notification>>(UiState.Loading)
    override var notificationDeleting by mutableStateOf(false)
    override var notificationDeleted by mutableStateOf(false)
    override var warningMessageRes by mutableStateOf<Int?>(null)
}