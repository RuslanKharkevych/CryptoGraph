package me.khruslan.cryptograph.ui.notifications.shared

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import me.khruslan.cryptograph.base.Logger
import me.khruslan.cryptograph.ui.R

private const val LOG_TAG = "NotificationPermissionState"

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private const val NOTIFICATION_PERMISSION = Manifest.permission.POST_NOTIFICATIONS

@Stable
internal interface NotificationPermissionState {
    val status: PermissionStatus
    fun launchPermissionRequest()
    fun openNotificationSettings()
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
        if (!runtimePermissionSupported()) {
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

    override fun openNotificationSettings() {
        val intent = Intent()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        } else {
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS")
                .putExtra("app_package", context.packageName)
                .putExtra("app_uid", context.applicationInfo.uid)
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

    fun runtimePermissionGranted(): Boolean {
        return runtimePermissionSupported() && status == PermissionStatus.Granted
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
        launcher.launch(NOTIFICATION_PERMISSION)
    }

    // TODO: Improve to always check areNotificationsEnabled();
    //  also check if channel is not disabled
    private fun getPermissionStatus(): PermissionStatus {
        if (runtimePermissionSupported()) {
            return getRuntimePermissionStatus()
        }

        val notificationManager = NotificationManagerCompat.from(context)
        return if (notificationManager.areNotificationsEnabled()) {
            PermissionStatus.Granted
        } else {
            PermissionStatus.Denied(shouldShowRationale = false)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun getRuntimePermissionStatus(): PermissionStatus {
        val hasPermission = context.checkRuntimeNotificationPermission()
        return if (hasPermission) {
            PermissionStatus.Granted
        } else {
            val shouldShowRationale = context.shouldShowRationale()
            PermissionStatus.Denied(shouldShowRationale)
        }
    }
}

@Composable
internal fun rememberNotificationPermissionState(
    onResult: ((Boolean) -> Unit)? = null,
    onError: ((Int) -> Unit)? = null,
): NotificationPermissionState {
    val context = LocalContext.current
    val permissionState = remember { MutableNotificationPermissionState(context) }
    val launcher = rememberPermissionLauncher { result ->
        Logger.info(LOG_TAG, "Received permission result: $result")
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
        if (!permissionState.runtimePermissionGranted()) {
            permissionState.refreshPermissionStatus()
        }
    }

    DisposableEffect(permissionState, launcher) {
        permissionState.launcher = launcher
        onDispose { permissionState.launcher = null }
    }

    return permissionState
}

internal sealed class PermissionStatus {
    data object Granted : PermissionStatus()
    data class Denied(val shouldShowRationale: Boolean) : PermissionStatus()
}

internal val PermissionStatus.shouldShowRationale
    get() = this is PermissionStatus.Denied && shouldShowRationale

internal val PermissionStatus.deniedOrNeverAsked
    get() = this is PermissionStatus.Denied && !shouldShowRationale

@Composable
private fun rememberPermissionLauncher(
    onResult: (Boolean) -> Unit,
): ActivityResultLauncher<String>? {
    if (!runtimePermissionSupported()) return null

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

private fun runtimePermissionSupported(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private fun Context.checkRuntimeNotificationPermission(): Boolean {
    val permissionResult = ContextCompat.checkSelfPermission(this, NOTIFICATION_PERMISSION)
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

    return ActivityCompat.shouldShowRequestPermissionRationale(activity, NOTIFICATION_PERMISSION)
}

private fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("Activity not found")
}