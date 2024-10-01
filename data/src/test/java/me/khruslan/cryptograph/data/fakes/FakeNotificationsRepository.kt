package me.khruslan.cryptograph.data.fakes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import me.khruslan.cryptograph.data.common.DataException
import me.khruslan.cryptograph.data.common.ErrorType
import me.khruslan.cryptograph.data.fixtures.STUB_NOTIFICATIONS
import me.khruslan.cryptograph.data.notifications.Notification
import me.khruslan.cryptograph.data.notifications.NotificationsRepository

class FakeNotificationsRepository : NotificationsRepository {

    var isDatabaseCorrupted = false

    private val notificationsFlow = MutableStateFlow(STUB_NOTIFICATIONS)

    override fun getNotifications(coinId: String?): Flow<List<Notification>> {
        checkIfDataIsValid()
        return notificationsFlow.map { notifications ->
            notifications.filter { coinId?.equals(it.coinId) ?: true }
        }
    }

    override suspend fun getNotification(id: Long): Notification {
        throw UnsupportedOperationException()
    }

    override suspend fun addOrUpdateNotification(notification: Notification) {
        checkIfDataIsValid()
        notificationsFlow.update { notifications ->
            if (notification.id == 0L) {
                notifications + notification
            } else {
                val index = notifications.indexOfFirst { it.id == notification.id }
                notifications.toMutableList().also { it[index] = notification }
            }
        }
    }

    override suspend fun deleteNotification(id: Long) {
        throw UnsupportedOperationException()
    }

    fun deleteAllNotifications() {
        notificationsFlow.value = emptyList()
    }

    private fun checkIfDataIsValid() {
        if (isDatabaseCorrupted) throw object : DataException(ErrorType.Database) {}
    }
}