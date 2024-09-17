package me.khruslan.cryptograph.ui.util.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry

internal class NavInterceptor(private val navBackStackEntry: NavBackStackEntry) {

    private val lifecycleResumed
        get() = navBackStackEntry.lifecycle.currentState == Lifecycle.State.RESUMED

    operator fun invoke(action: () -> Unit): () -> Unit = {
        if (lifecycleResumed) action()
    }

    operator fun <T> invoke(action: (T) -> Unit): (T) -> Unit = { arg1 ->
        if (lifecycleResumed) action(arg1)
    }
}

@Composable
internal fun rememberNavInterceptor(navBackStackEntry: NavBackStackEntry): NavInterceptor {
    return remember(navBackStackEntry) {
        NavInterceptor(navBackStackEntry)
    }
}