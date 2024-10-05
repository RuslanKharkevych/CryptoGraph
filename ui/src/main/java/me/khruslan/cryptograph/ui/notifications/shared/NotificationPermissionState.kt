package me.khruslan.cryptograph.ui.notifications.shared

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import me.khruslan.cryptograph.base.Logger
import me.khruslan.cryptograph.base.NotificationPermissionStatus
import me.khruslan.cryptograph.base.NotificationUtil
import me.khruslan.cryptograph.ui.R

private const val LOG_TAG = "NotificationPermissionState"

@Stable
internal interface NotificationPermissionState {
    val status: NotificationPermissionStatus
    fun launchPermissionRequest()
    fun openNotificationSettings(channelBlocked: Boolean)
}

private class MutableNotificationPermissionState(
    private val context: Context,
) : NotificationPermissionState {

    override var status by mutableStateOf(getPermissionStatus())
        private set

    var result by mutableStateOf<Boolean?>(null)
        private set

    var errorRes by mutableStateOf<Int?>(null)
        private set

    var launcher: ActivityResultLauncher<String>? = null

    override fun launchPermissionRequest() {
        if (!NotificationUtil.runtimePermissionSupported()) {
            Logger.info(LOG_TAG, "Runtime permission not supported, skipping request")
            result = false
            return
        }

        try {
            launchRuntimePermissionRequest(launcher)
            Logger.info(LOG_TAG, "Launched runtime permission request")
        } catch (e: IllegalStateException) {
            Logger.error(LOG_TAG, "Failed to launch runtime permission request", e)
            errorRes = R.string.notification_permission_request_error_msg
            result = false
        }
    }

    override fun openNotificationSettings(channelBlocked: Boolean) {
        val intent = if (channelBlocked) {
            try {
                createNotificationChannelSettingsIntent()
            } catch (e: IllegalStateException) {
                Logger.error(LOG_TAG, "Failed to open notification channel settings", e)
                createNotificationSettingsIntent()
            }
        } else {
            createNotificationSettingsIntent()
        }

        try {
            context.startActivity(intent)
            Logger.info(LOG_TAG, "Opened notification settings")
        } catch (e: ActivityNotFoundException) {
            Logger.error(LOG_TAG, "Failed to open notification settings", e)
            errorRes = R.string.notification_settings_error_msg
        }
    }

    fun refreshPermissionStatus() {
        status = getPermissionStatus()
        Logger.info(LOG_TAG, "Refreshed permission status: $status")
    }

    fun setPermissionResult(result: Boolean) {
        this.result = result
        refreshPermissionStatus()
    }

    fun resultDispatched() {
        result = null
    }

    fun errorDispatched() {
        errorRes = null
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun launchRuntimePermissionRequest(launcher: ActivityResultLauncher<String>?) {
        checkNotNull(launcher)
        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun getPermissionStatus(): NotificationPermissionStatus {
        return NotificationUtil.getPermissionStatus(context)
    }

    private fun createNotificationSettingsIntent(): Intent {
        val intent = Intent()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        } else {
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS")
                .putExtra("app_package", context.packageName)
                .putExtra("app_uid", context.applicationInfo.uid)
        }
    }

    private fun createNotificationChannelSettingsIntent(): Intent {
        if (!NotificationUtil.notificationChannelsSupported()) {
            throw IllegalStateException("Notification channels are not supported")
        }

        return Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
            .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            .putExtra(Settings.EXTRA_CHANNEL_ID, NotificationUtil.CHANNEL_ID)
    }
}

private object StubNotificationPermissionState : NotificationPermissionState {
    override val status = NotificationPermissionStatus.Granted
    override fun launchPermissionRequest() {}
    override fun openNotificationSettings(channelBlocked: Boolean) {}
}

@Composable
internal fun rememberNotificationPermissionState(
    onResult: ((Boolean) -> Unit)? = null,
    onError: ((Int) -> Unit)? = null,
): NotificationPermissionState {
    if (LocalInspectionMode.current) {
        return StubNotificationPermissionState
    }

    val context = LocalContext.current
    val permissionState = remember { MutableNotificationPermissionState(context) }
    val launcher = rememberPermissionLauncher { result ->
        Logger.info(LOG_TAG, "Received runtime permission result: $result")
        permissionState.setPermissionResult(result)
    }

    permissionState.result?.let { result ->
        LaunchedEffect(result) {
            onResult?.invoke(result)
            permissionState.resultDispatched()
        }
    }

    permissionState.errorRes?.let { resId ->
        LaunchedEffect(resId) {
            onError?.invoke(resId)
            permissionState.errorDispatched()
        }
    }

    OnResumeEffect(permissionState) {
        permissionState.refreshPermissionStatus()
    }

    DisposableEffect(permissionState, launcher) {
        permissionState.launcher = launcher
        onDispose { permissionState.launcher = null }
    }

    return permissionState
}

@Composable
private fun rememberPermissionLauncher(
    onResult: (Boolean) -> Unit,
): ActivityResultLauncher<String>? {
    if (!NotificationUtil.runtimePermissionSupported()) return null

    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = onResult
    )
}

@Composable
private fun OnResumeEffect(key: Any?, onResume: () -> Unit) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val observer = remember(key) {
        LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                onResume()
            }
        }
    }

    DisposableEffect(lifecycle, observer) {
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }
}