package me.khruslan.cryptograph.data.notifications.repository.local

import io.objectbox.Box
import io.objectbox.exception.DbException
import io.objectbox.kotlin.flow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import me.khruslan.cryptograph.base.Logger
import me.khruslan.cryptograph.data.core.DatabaseException

private const val LOG_TAG = "NotificationsLocalDataSource"

internal interface NotificationsLocalDataSource {
    fun getNotifications(coinUuid: String? = null): Flow<List<NotificationDto>>
    suspend fun getNotification(id: Long): NotificationDto
    suspend fun addOrUpdateNotification(notification: NotificationDto)
    suspend fun deleteNotification(id: Long)
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

        return queryBuilder.build().flow()
    }

    override suspend fun getNotification(id: Long): NotificationDto {
        return withContext(dispatcher) {
            try {
                box.get(id).also { notification ->
                    Logger.info(LOG_TAG, "Fetched $notification")
                }
            } catch (e: DbException) {
                Logger.error(LOG_TAG, "Failed to get notification by ID: $id", e)
                throw DatabaseException(e)
            }
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

    override suspend fun deleteNotification(id: Long) {
        withContext(dispatcher) {
            try {
                deleteNotificationInternal(id)
                Logger.info(LOG_TAG, "Deleted notification by ID: $id")
            } catch (e: DbException) {
                Logger.error(LOG_TAG, "Failed to delete notification by ID: $id", e)
                throw DatabaseException(e)
            }
        }
    }

    private fun deleteNotificationInternal(id: Long) {
        if (!box.remove(id)) {
            throw DbException("No notification exists with the given ID")
        }
    }
}
