package me.khruslan.cryptograph.data.notifications.local

import io.objectbox.Box
import io.objectbox.exception.DbException
import io.objectbox.kotlin.toFlow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import me.khruslan.cryptograph.base.Logger
import me.khruslan.cryptograph.data.common.DatabaseException

private const val LOG_TAG = "NotificationsLocalDataSource"

internal interface NotificationsLocalDataSource {
    fun getNotifications(coinUuid: String? = null): Flow<List<NotificationDto>>
    suspend fun addOrUpdateNotification(notification: NotificationDto)
    suspend fun deleteNotification(notification: NotificationDto)
}

internal class NotificationsLocalDataSourceImpl(
    private val box: Box<NotificationDto>,
    private val dispatcher: CoroutineDispatcher
) : NotificationsLocalDataSource {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getNotifications(coinUuid: String?): Flow<List<NotificationDto>> {
        val queryBuilder = if (coinUuid != null) {
            box.query(NotificationDto_.coinUuid.equal(coinUuid))
        } else {
            box.query()
        }

        return queryBuilder.build().subscribe().toFlow().onEach { notifications ->
            Logger.info(LOG_TAG, "Observed notifications: $notifications")
        }
    }

    override suspend fun addOrUpdateNotification(notification: NotificationDto) {
        withContext(dispatcher) {
            try {
                box.put(notification)
                Logger.info(LOG_TAG, "Added/updated $notification")
            } catch (e: DbException) {
                Logger.error(LOG_TAG, "Failed to add/update $notification", e)
                throw DatabaseException(e)
            }
        }
    }

    override suspend fun deleteNotification(notification: NotificationDto) {
        withContext(dispatcher) {
            try {
                deleteNotificationInternal(notification)
                Logger.info(LOG_TAG, "Deleted $notification")
            } catch (e: DbException) {
                Logger.error(LOG_TAG, "Failed to delete $notification", e)
                throw DatabaseException(e)
            }
        }
    }

    private fun deleteNotificationInternal(notification: NotificationDto) {
        if (!box.remove(notification)) {
            throw DbException("No notification exists with the given ID")
        }
    }
}
