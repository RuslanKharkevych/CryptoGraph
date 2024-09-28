package me.khruslan.cryptograph.ui.util

import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

internal interface AlertState {
    val isVisible: Boolean

    fun show()
    fun dismiss()
}

@VisibleForTesting
internal class AlertStateImpl(isVisible: Boolean = false) : AlertState {
    override var isVisible by mutableStateOf(isVisible)

    override fun show() {
        isVisible = true
    }

    override fun dismiss() {
        isVisible = false
    }
}

@Composable
internal fun rememberAlertState(): AlertState {
    return rememberSaveable(saver = alertStateSaver) {
        AlertStateImpl()
    }
}

private val alertStateSaver
    get() = listSaver(
        save = { alertState ->
            listOf(alertState.isVisible)
        },
        restore = { list ->
            AlertStateImpl(list[0] as Boolean)
        }
    )