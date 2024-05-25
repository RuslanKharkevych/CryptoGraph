package me.khruslan.cryptograph.data.fakes

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import me.khruslan.cryptograph.data.coins.local.CoinsLocalDataSource
import me.khruslan.cryptograph.data.coins.local.PinnedCoinDto

internal class FakeCoinsLocalDataSource : CoinsLocalDataSource {
    override val pinnedCoins = MutableStateFlow<List<PinnedCoinDto>>(emptyList())

    override suspend fun pinCoin(uuid: String) {
        val pinnedCoin = PinnedCoinDto(coinUuid = uuid)
        pinnedCoins.update { it + pinnedCoin }
    }

    override suspend fun unpinCoin(uuid: String) {
        pinnedCoins.update { coins ->
            coins.filter { it.coinUuid != uuid }
        }
    }
}