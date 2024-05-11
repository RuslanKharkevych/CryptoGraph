package me.khruslan.cryptograph.ui.coins

import androidx.annotation.StringRes
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import me.khruslan.cryptograph.data.coins.Coin

@Stable
internal interface CoinsState {
    val listState: CoinsListState
    @get:StringRes val warningMessageRes: Int?
    val notificationBadgeCount: Int
}

internal class MutableCoinsState : CoinsState {
    override var listState: CoinsListState by mutableStateOf(CoinsListState.Loading)
    override var warningMessageRes: Int? by mutableStateOf(null)
    override var notificationBadgeCount: Int by mutableIntStateOf(0)
}

internal sealed class CoinsListState {
    data object Loading : CoinsListState()
    data class Data(val coins: List<Coin>) : CoinsListState()
    data class Error(@StringRes val messageRes: Int) : CoinsListState()
}