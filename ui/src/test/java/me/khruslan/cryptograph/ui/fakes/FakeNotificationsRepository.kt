package me.khruslan.cryptograph.ui.fakes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import me.khruslan.cryptograph.data.fixtures.STUB_NOTIFICATIONS
import me.khruslan.cryptograph.data.notifications.Notification
import me.khruslan.cryptograph.data.notifications.NotificationsRepository

internal class FakeNotificationsRepository : NotificationsRepository {

    private val notificationsFlow = MutableStateFlow(STUB_NOTIFICATIONS)

    override fun getNotifications(coinId: String?): Flow<List<Notification>> {
        return notificationsFlow.map { notifications ->
            notifications.filter { coinId?.equals(it.coinId) ?: true }
        }
    }

    override suspend fun addOrUpdateNotification(notification: Notification) {
        notificationsFlow.update { notifications ->
            val index = notifications.indexOfFirst { it.id == notification.id }
            if (index == -1) {
                notifications + notification
            } else {
                notifications.toMutableList().also {
                    it[index] = notification
                }
            }
        }
    }

    override suspend fun deleteNotification(notification: Notification) {
        notificationsFlow.update { it - notification }
    }
}