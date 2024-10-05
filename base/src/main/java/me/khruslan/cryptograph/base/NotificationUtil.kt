package me.khruslan.cryptograph.base

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

object NotificationUtil {
    private const val LOG_TAG = "NotificationUtil"

    const val CHANNEL_ID = "CoinPriceChanges"

    fun notificationsEnabled(context: Context): Boolean {
        if (runtimePermissionSupported() && !context.runtimePermissionGranted()) return false
        return context.getNotificationsStatus() == NotificationPermissionStatus.Granted
    }

    fun getPermissionStatus(activityContext: Context): NotificationPermissionStatus {
        return if (runtimePermissionSupported()) {
            activityContext.getRuntimePermissionStatus()
        } else {
            activityContext.getNotificationsStatus()
        }
    }

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU)
    fun runtimePermissionSupported(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    }

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
    fun notificationChannelsSupported(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun Context.getRuntimePermissionStatus(): NotificationPermissionStatus {
        return if (runtimePermissionGranted()) {
            getNotificationsStatus()
        } else {
            NotificationPermissionStatus.Denied(shouldShowRationale())
        }
    }

    private fun Context.getNotificationsStatus(): NotificationPermissionStatus {
        val notificationManager = NotificationManagerCompat.from(this)

        return when {
            !notificationManager.areNotificationsEnabled() -> NotificationPermissionStatus.Denied()
            notificationChannelsSupported() -> notificationManager.getNotificationChannelStatus()
            else -> NotificationPermissionStatus.Granted
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun NotificationManagerCompat.getNotificationChannelStatus()
            : NotificationPermissionStatus {

        val notificationChannel = getNotificationChannel(CHANNEL_ID)

        return if (notificationChannel?.isBlocked() == true) {
            NotificationPermissionStatus.Denied(channelBlocked = true)
        } else {
            NotificationPermissionStatus.Granted
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun NotificationChannel.isBlocked(): Boolean {
        return importance == NotificationManager.IMPORTANCE_NONE
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun Context.runtimePermissionGranted(): Boolean {
        val permissionResult = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        )

        return permissionResult == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun Context.shouldShowRationale(): Boolean {
        val activity = try {
            findActivity()
        } catch (e: IllegalStateException) {
            Logger.error(LOG_TAG, "Failed to get permission rationale status", e)
            return false
        }

        return ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            Manifest.permission.POST_NOTIFICATIONS
        )
    }

    private fun Context.findActivity(): Activity {
        var context = this
        while (context is ContextWrapper) {
            if (context is Activity) return context
            context = context.baseContext
        }
        throw IllegalStateException("Activity not found")
    }
}

sealed class NotificationPermissionStatus {
    data object Granted : NotificationPermissionStatus()

    data class Denied(
        val shouldShowRationale: Boolean = false,
        val channelBlocked: Boolean = false,
    ) : NotificationPermissionStatus()

    val deniedOrNeverAsked
        get() = this is Denied && !shouldShowRationale
}