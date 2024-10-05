package me.khruslan.cryptograph.data.notifications.managers

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.NotificationManagerCompat.IMPORTANCE_DEFAULT
import me.khruslan.cryptograph.base.LaunchOptions
import me.khruslan.cryptograph.base.Logger
import me.khruslan.cryptograph.base.NotificationUtil
import me.khruslan.cryptograph.data.R
import me.khruslan.cryptograph.data.notifications.interactors.completed.CompletedNotification
import me.khruslan.cryptograph.data.notifications.repository.NotificationTrigger
import kotlin.random.Random

private const val LOG_TAG = "PushNotificationsManager"

private const val COMPLETED_NOTIFICATIONS_GROUP_KEY = "CompletedNotifications"
private const val COMPLETED_NOTIFICATIONS_SUMMARY_ID = 1
private const val PENDING_INTENT_REQUEST_CODE = 0

internal class PushNotificationsManager(
    private val context: Context,
    private val launchOptions: LaunchOptions
) {
    private val notificationManager = NotificationManagerCompat.from(context)

    @SuppressLint("MissingPermission")
    fun postCompletedNotifications(completedNotifications: List<CompletedNotification>) {
        if (!NotificationUtil.notificationsEnabled(context)) {
            Logger.info(LOG_TAG, "Push notifications disabled")
            return
        }

        val notificationChannel = buildNotificationChannel()
        notificationManager.createNotificationChannel(notificationChannel)
        Logger.debug(LOG_TAG, "Created notification channel")

        val pendingIntent = createPendingIntent()
        val notifications = buildNotifications(completedNotifications, pendingIntent)
        notifications.forEach { notification ->
            notificationManager.notify(generateNotificationId(), notification)
        }
        Logger.info(LOG_TAG, "Posted push notifications: $notifications")

        val summaryNotification = buildSummaryNotification(completedNotifications, pendingIntent)
        notificationManager.notify(COMPLETED_NOTIFICATIONS_SUMMARY_ID, summaryNotification)
        Logger.debug(LOG_TAG, "Posted summary notification: $summaryNotification")
    }

    private fun buildNotificationChannel(): NotificationChannelCompat {
        return NotificationChannelCompat.Builder(NotificationUtil.CHANNEL_ID, IMPORTANCE_DEFAULT)
            .setName(context.getString(R.string.data_notification_channel_name))
            .setDescription(context.getString(R.string.data_notification_channel_desc))
            .build()
    }

    private fun buildNotifications(
        completedNotifications: List<CompletedNotification>,
        pendingIntent: PendingIntent,
    ): List<Notification> {
        return completedNotifications.map { notification ->
            NotificationCompat.Builder(context, NotificationUtil.CHANNEL_ID)
                .setSmallIcon(R.drawable.data_ic_push_notification)
                .setContentTitle(notification.title)
                .setContentText(notification.text)
                .setContentIntent(pendingIntent)
                .setGroup(COMPLETED_NOTIFICATIONS_GROUP_KEY)
                .build()
        }
    }

    private fun buildSummaryNotification(
        completedNotifications: List<CompletedNotification>,
        pendingIntent: PendingIntent,
    ): Notification {
        val contentTitle = getSummaryNotificationTitle(completedNotifications)
        val contentText = getSummaryNotificationText(completedNotifications)
        val style = getSummaryNotificationStyle(completedNotifications)

        return NotificationCompat.Builder(context, NotificationUtil.CHANNEL_ID)
            .setSmallIcon(R.drawable.data_ic_push_notification)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setContentIntent(pendingIntent)
            .setStyle(style)
            .setGroup(COMPLETED_NOTIFICATIONS_GROUP_KEY)
            .setGroupSummary(true)
            .build()
    }

    private fun generateNotificationId(): Int {
        return Random.nextInt(COMPLETED_NOTIFICATIONS_SUMMARY_ID + 1, Int.MAX_VALUE)
    }

    private fun createPendingIntent(): PendingIntent {
        return PendingIntent.getActivity(
            context,
            PENDING_INTENT_REQUEST_CODE,
            launchOptions.notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun getSummaryNotificationTitle(
        completedNotifications: List<CompletedNotification>,
    ): String {
        val notificationsCount = completedNotifications.count()

        return if (notificationsCount == 1) {
            completedNotifications.single().title
        } else {
            context.getString(R.string.data_summary_notification_title, notificationsCount)
        }
    }

    private fun getSummaryNotificationText(
        completedNotifications: List<CompletedNotification>,
    ): String {
        return if (completedNotifications.count() == 1) {
            completedNotifications.single().text
        } else {
            context.getString(R.string.data_summary_notification_text)
        }
    }

    private fun getSummaryNotificationStyle(
        completedNotifications: List<CompletedNotification>,
    ): NotificationCompat.Style? {
        if (completedNotifications.count() == 1) return null

        val style = NotificationCompat.InboxStyle()
        completedNotifications.forEach { notification ->
            style.addLine(notification.title)
        }

        return style
    }

    private val CompletedNotification.text: String
        get() {
            val resId = when (trigger) {
                is NotificationTrigger.PriceLessThan ->
                    R.string.data_notification_trigger_price_less_than_desc

                is NotificationTrigger.PriceMoreThan ->
                    R.string.data_notification_trigger_price_more_than_desc
            }
            val priceString = trigger.targetPrice.toBigDecimal().toPlainString()

            return context.getString(resId, coinName, priceString)
        }
}