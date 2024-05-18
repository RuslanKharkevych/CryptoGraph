package me.khruslan.cryptograph.data.fakes

import me.khruslan.cryptograph.data.coins.Coin
import me.khruslan.cryptograph.data.coins.local.PinnedCoinDto
import me.khruslan.cryptograph.data.coins.mapper.CoinsMapper
import me.khruslan.cryptograph.data.coins.remote.CoinDto
import me.khruslan.cryptograph.data.fixtures.COINS

internal class FakeCoinsMapper : CoinsMapper {
    override suspend fun mapCoins(
        allCoins: List<CoinDto>,
        pinnedCoins: List<PinnedCoinDto>,
    ): List<Coin> {
        return allCoins.map { coinDto ->
            val isPinned = pinnedCoins.any { it.coinUuid == coinDto.uuid }
            COINS.first { it.id == coinDto.uuid }.copy(isPinned = isPinned)
        }.sortedWith(compareByDescending<Coin> { it.isPinned }.thenBy { it.rank })
    }
}