package me.khruslan.cryptograph.ui.util.state

import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import me.khruslan.cryptograph.base.Logger

private const val LOG_TAG = "AlertState"

internal interface AlertState {
    val isVisible: Boolean

    fun show()
    fun dismiss()
}

@VisibleForTesting
internal class AlertStateImpl(val tag: String, isVisible: Boolean = false) : AlertState {
    override var isVisible by mutableStateOf(isVisible)

    override fun show() {
        isVisible = true
        Logger.info(LOG_TAG, "$tag alert shown")
    }

    override fun dismiss() {
        isVisible = false
        Logger.info(LOG_TAG, "$tag alert dismissed")
    }
}

@Composable
internal fun rememberAlertState(tag: String): AlertState {
    return rememberSaveable(saver = alertStateSaver) {
        AlertStateImpl(tag)
    }
}

private val alertStateSaver
    get() = listSaver(
        save = { alertState ->
            listOf(
                alertState.tag,
                alertState.isVisible
            )
        },
        restore = { list ->
            AlertStateImpl(
                list[0] as String,
                list[1] as Boolean
            )
        }
    )