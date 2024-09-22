package me.khruslan.cryptograph.data.notifications

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.khruslan.cryptograph.data.notifications.local.NotificationsLocalDataSource
import me.khruslan.cryptograph.data.notifications.mapper.NotificationsMapper

interface NotificationsRepository {
    fun getNotifications(coinId: String?): Flow<List<Notification>>
    suspend fun getNotification(id: Long): Notification
    suspend fun addOrUpdateNotification(notification: Notification)
    suspend fun deleteNotification(notification: Notification)
}

internal class NotificationsRepositoryImpl(
    private val localDataSource: NotificationsLocalDataSource,
    private val mapper: NotificationsMapper,
) : NotificationsRepository {

    override fun getNotifications(coinId: String?): Flow<List<Notification>> {
        return localDataSource.getNotifications(coinId).map { notifications ->
            mapper.mapNotifications(notifications)
        }
    }

    override suspend fun getNotification(id: Long): Notification {
        val notificationDto = localDataSource.getNotification(id)
        return mapper.mapNotification(notificationDto)
    }

    override suspend fun addOrUpdateNotification(notification: Notification) {
        val notificationDto = mapper.mapNotification(notification)
        localDataSource.addOrUpdateNotification(notificationDto)
    }

    override suspend fun deleteNotification(notification: Notification) {
        val notificationDto = mapper.mapNotification(notification)
        localDataSource.deleteNotification(notificationDto)
    }
}