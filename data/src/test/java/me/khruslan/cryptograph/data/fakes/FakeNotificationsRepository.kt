package me.khruslan.cryptograph.data.fakes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import me.khruslan.cryptograph.data.fixtures.STUB_NOTIFICATIONS
import me.khruslan.cryptograph.data.notifications.Notification
import me.khruslan.cryptograph.data.notifications.NotificationsRepository

class FakeNotificationsRepository : NotificationsRepository {

    override fun getNotifications(coinId: String?): Flow<List<Notification>> {
        return MutableStateFlow(STUB_NOTIFICATIONS.filter { coinId?.equals(it.coinId) ?: true })
    }

    override suspend fun addOrUpdateNotification(notification: Notification) {}

    override suspend fun deleteNotification(notification: Notification) {}
}