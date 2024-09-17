package me.khruslan.cryptograph.data.coins

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import me.khruslan.cryptograph.data.coins.local.CoinsLocalDataSource
import me.khruslan.cryptograph.data.coins.mapper.CoinsMapper
import me.khruslan.cryptograph.data.coins.remote.CoinsRemoteDataSource

interface CoinsRepository {
    fun getCoins(id: String? = null): Flow<List<Coin>>
    suspend fun pinCoin(id: String)
    suspend fun unpinCoin(id: String)
    suspend fun getCoinHistory(id: String): List<CoinPrice>
}

internal class CoinsRepositoryImpl(
    private val localDataSource: CoinsLocalDataSource,
    private val remoteDataSource: CoinsRemoteDataSource,
    private val mapper: CoinsMapper,
) : CoinsRepository {

    override fun getCoins(id: String?): Flow<List<Coin>> {
        val allCoinsFlow = flow { emit(remoteDataSource.getCoins(id)) }
        val pinnedCoinsFlow = localDataSource.pinnedCoins

        return allCoinsFlow.combine(pinnedCoinsFlow) { allCoins, pinnedCoins ->
            mapper.mapCoins(allCoins, pinnedCoins)
        }
    }

    override suspend fun pinCoin(id: String) {
        localDataSource.pinCoin(id)
    }

    override suspend fun unpinCoin(id: String) {
        localDataSource.unpinCoin(id)
    }

    override suspend fun getCoinHistory(id: String): List<CoinPrice> {
        val history = remoteDataSource.getCoinHistory(id)
        return mapper.mapCoinHistory(history)
    }
}