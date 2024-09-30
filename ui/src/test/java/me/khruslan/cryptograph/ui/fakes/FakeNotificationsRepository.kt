package me.khruslan.cryptograph.ui.fakes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import me.khruslan.cryptograph.data.common.DataException
import me.khruslan.cryptograph.data.common.ErrorType
import me.khruslan.cryptograph.data.fixtures.STUB_NOTIFICATIONS
import me.khruslan.cryptograph.data.notifications.Notification
import me.khruslan.cryptograph.data.notifications.NotificationsRepository

internal class FakeNotificationsRepository : NotificationsRepository {

    var isDatabaseCorrupted = false

    var notificationsAdded = 0
    var notificationsDeleted = 0

    val unreadNotificationsCount
        get() = notificationsFlow.value.count { it.unread }

    private val notificationsFlow = MutableStateFlow(STUB_NOTIFICATIONS)

    override fun getNotifications(coinId: String?): Flow<List<Notification>> {
        return notificationsFlow.map { notifications ->
            notifications.filter { coinId?.equals(it.coinId) ?: true }
        }
    }

    override suspend fun getNotification(id: Long): Notification {
        checkIfDataIsValid()
        return notificationsFlow.value.first { it.id == id }
    }

    override suspend fun addOrUpdateNotification(notification: Notification) {
        checkIfDataIsValid()
        notificationsFlow.update { notifications ->
            if (notification.id == 0L) {
                notificationsAdded++
                notifications + notification
            } else {
                val index = notifications.indexOfFirst { it.id == notification.id }
                notifications.toMutableList().also { it[index] = notification }
            }
        }
    }

    override suspend fun deleteNotification(id: Long) {
        checkIfDataIsValid()
        notificationsFlow.update { it - getNotification(id) }
        notificationsDeleted++
    }

    private fun checkIfDataIsValid() {
        if (isDatabaseCorrupted) throw object : DataException(ErrorType.Database) {}
    }
}