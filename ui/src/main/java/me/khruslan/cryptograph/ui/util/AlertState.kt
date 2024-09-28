package me.khruslan.cryptograph.ui.util

import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

internal interface AlertState {
    val isVisible: Boolean

    fun show()
    fun dismiss()
}

@VisibleForTesting
internal class AlertStateImpl : AlertState {
    override var isVisible by mutableStateOf(false)

    override fun show() {
        isVisible = true
    }

    override fun dismiss() {
        isVisible = false
    }
}

// TODO: Remember saveable
@Composable
internal fun rememberAlertState(): AlertState {
    return remember {
        AlertStateImpl()
    }
}