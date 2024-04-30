package me.khruslan.cryptograph.data.coins.local

import io.objectbox.Box
import io.objectbox.exception.DbException
import me.khruslan.cryptograph.data.common.DatabaseException

internal interface CoinsLocalDataSource {
    fun getPinnedCoins(): List<PinnedCoinDto>
    fun pinCoin(uuid: String)
    fun unpinCoin(uuid: String)
}

internal class CoinsStore(private val box: Box<PinnedCoinDto>) : CoinsLocalDataSource {

    override fun getPinnedCoins(): List<PinnedCoinDto> {
        return try {
            box.all
        } catch (e: DbException) {
            throw DatabaseException(e)
        }
    }

    override fun pinCoin(uuid: String) {
        try {
            val pinnedCoin = PinnedCoinDto(coinUuid = uuid)
            box.put(pinnedCoin)
        } catch (e: DbException) {
            throw DatabaseException(e)
        }
    }

    override fun unpinCoin(uuid: String) {
        try {
            box.query(PinnedCoinDto_.coinUuid.equal(uuid)).build().use { query ->
                box.remove(query.find())
            }
        } catch (e: DbException) {
            throw DatabaseException(e)
        }
    }
}