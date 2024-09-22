package me.khruslan.cryptograph.data.fakes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import me.khruslan.cryptograph.data.notifications.local.NotificationDto
import me.khruslan.cryptograph.data.notifications.local.NotificationsLocalDataSource

internal class FakeNotificationsLocalDataSource : NotificationsLocalDataSource {

    private val notificationsFlow = MutableStateFlow(emptyList<NotificationDto>())

    override fun getNotifications(coinUuid: String?): Flow<List<NotificationDto>> {
        return notificationsFlow.map { notifications ->
            notifications.filter { coinUuid?.equals(it.coinUuid) ?: true }
        }
    }

    override suspend fun getNotification(id: Long): NotificationDto {
        return notificationsFlow.value.first { it.id == id }
    }

    override suspend fun addOrUpdateNotification(notification: NotificationDto) {
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

    override suspend fun deleteNotification(notification: NotificationDto) {
        notificationsFlow.update { it - notification }
    }
}