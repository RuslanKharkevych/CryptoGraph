package me.khruslan.cryptograph.data.coins.local

import io.objectbox.Box
import io.objectbox.exception.DbException
import io.objectbox.kotlin.flow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import me.khruslan.cryptograph.base.Logger
import me.khruslan.cryptograph.data.core.DatabaseException

private const val LOG_TAG = "CoinsLocalDataSource"

internal interface CoinsLocalDataSource {
    val pinnedCoins: Flow<List<PinnedCoinDto>>
    suspend fun pinCoin(uuid: String)
    suspend fun unpinCoin(uuid: String)
}

internal class CoinsLocalDataSourceImpl(
    private val box: Box<PinnedCoinDto>,
    private val dispatcher: CoroutineDispatcher
) : CoinsLocalDataSource {

    @OptIn(ExperimentalCoroutinesApi::class)
    override val pinnedCoins: Flow<List<PinnedCoinDto>>
        get() = box.query().build().flow()

    override suspend fun pinCoin(uuid: String) {
        return withContext(dispatcher) {
            try {
                val pinnedCoin = PinnedCoinDto(coinUuid = uuid)
                box.put(pinnedCoin)
                Logger.info(LOG_TAG, "Saved pinned coin: $uuid")
            } catch (e: DbException) {
                Logger.error(LOG_TAG, "Failed to save pinned coin: $uuid", e)
                throw DatabaseException(e)
            }
        }
    }

    override suspend fun unpinCoin(uuid: String) {
        return withContext(dispatcher) {
            try {
                box.query(PinnedCoinDto_.coinUuid.equal(uuid)).build().use { query ->
                    box.remove(query.find())
                }
                Logger.info(LOG_TAG, "Removed pinned coin: $uuid")
            } catch (e: DbException) {
                Logger.error(LOG_TAG, "Failed to remove pinned coin: $uuid", e)
                throw DatabaseException(e)
            }
        }
    }
}