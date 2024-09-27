package me.khruslan.cryptograph.ui.notifications.details.confirmation

import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

internal interface ConfirmationAlertState {
    val isVisible: Boolean

    fun show()
    fun dismiss()
}

@VisibleForTesting
internal class ConfirmationAlertStateImpl : ConfirmationAlertState {
    override var isVisible by mutableStateOf(false)

    override fun show() {
        isVisible = true
    }

    override fun dismiss() {
        isVisible = false
    }
}

@Composable
internal fun rememberConfirmationAlertState(): ConfirmationAlertState {
    return remember {
        ConfirmationAlertStateImpl()
    }
}