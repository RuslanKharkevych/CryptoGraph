package me.khruslan.cryptograph.ui.notifications.main

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.khruslan.cryptograph.data.notifications.Notification
import me.khruslan.cryptograph.data.notifications.NotificationsRepository

internal class NotificationsViewModel(
    private val notificationsRepository: NotificationsRepository,
) : ViewModel() {

    private val _notificationsState = MutableNotificationsState()
    val notificationsState: NotificationsState = _notificationsState

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            notificationsRepository.getNotifications().collect { notifications ->
                _notificationsState.notifications = notifications
            }
        }
    }
}

@Stable
internal interface NotificationsState {
    val notifications: List<Notification>
}

internal class MutableNotificationsState : NotificationsState {
    override var notifications: List<Notification> by mutableStateOf(emptyList())
}