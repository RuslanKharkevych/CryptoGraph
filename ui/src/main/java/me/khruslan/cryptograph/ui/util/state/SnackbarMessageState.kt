package me.khruslan.cryptograph.ui.util.state

import androidx.annotation.StringRes
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource

@Stable
internal interface SnackbarMessageState {
    fun show(@StringRes messageResId: Int)
}

private class MutableSnackbarMessageState : SnackbarMessageState {

    var messageResId: Int? by mutableStateOf(null)
        private set

    override fun show(@StringRes messageResId: Int) {
        this.messageResId = messageResId
    }

    fun messageShown() {
        messageResId = null
    }
}

@Composable
internal fun SnackbarHostState.rememberMessageState() : SnackbarMessageState {
    val state = remember(this) {
        MutableSnackbarMessageState()
    }

    state.messageResId?.let { resId ->
        val message = stringResource(resId)
        LaunchedEffect(message) {
            showSnackbar(message)
            state.messageShown()
        }
    }

    return state
}