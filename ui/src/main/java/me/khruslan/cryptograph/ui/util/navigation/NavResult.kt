package me.khruslan.cryptograph.ui.util.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController

internal fun <T> NavController.setNavResult(key: String, result: T) {
    checkNotNull(previousBackStackEntry).savedStateHandle[key] = result
}

@Composable
internal fun <T> NavResultEffect(
    navBackStackEntry: NavBackStackEntry,
    key: String,
    onResult: (T) -> Unit,
) {
    val savedStateHandle = navBackStackEntry.savedStateHandle
    val result by savedStateHandle.getStateFlow<T?>(key, null).collectAsState()

    result?.let {
        onResult(it)
        savedStateHandle[key] = null
    }
}