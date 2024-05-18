package me.khruslan.cryptograph.data.fakes

import me.khruslan.cryptograph.data.coins.remote.CoinsRemoteDataSource
import me.khruslan.cryptograph.data.fixtures.DTO_COINS

internal class FakeCoinsService : CoinsRemoteDataSource {
    override suspend fun getCoins() = DTO_COINS
}