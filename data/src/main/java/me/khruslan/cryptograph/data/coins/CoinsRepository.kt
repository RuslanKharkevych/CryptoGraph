package me.khruslan.cryptograph.data.coins

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import me.khruslan.cryptograph.data.coins.local.CoinsLocalDataSource
import me.khruslan.cryptograph.data.coins.remote.CoinsRemoteDataSource
import me.khruslan.cryptograph.data.coins.remote.toCoin

interface CoinsRepository {
    val coins: Flow<List<Coin>>
    suspend fun pinCoin(id: String)
    suspend fun unpinCoin(id: String)
}

internal class CoinsRepositoryImpl(
    private val localDataSource: CoinsLocalDataSource,
    private val remoteDataSource: CoinsRemoteDataSource
) : CoinsRepository {

    override val coins: Flow<List<Coin>>
        get() {
            val allCoinsFlow = flow { emit(remoteDataSource.getCoins()) }
            val pinnedCoinsFlow = localDataSource.pinnedCoins

            return allCoinsFlow.combine(pinnedCoinsFlow) { allCoins, pinnedCoins ->
                allCoins.map { coinDto ->
                    val isPinned = pinnedCoins.any { it.coinUuid == coinDto.uuid }
                    coinDto.toCoin(isPinned)
                }.sortedWith(compareByDescending<Coin> { it.isPinned }.thenBy { it.rank })
            }
        }

    override suspend fun pinCoin(id: String) {
        localDataSource.pinCoin(id)
    }

    override suspend fun unpinCoin(id: String) {
        localDataSource.unpinCoin(id)
    }
}