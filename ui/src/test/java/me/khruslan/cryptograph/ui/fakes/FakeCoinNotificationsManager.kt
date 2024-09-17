package me.khruslan.cryptograph.ui.fakes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import me.khruslan.cryptograph.data.common.DataException
import me.khruslan.cryptograph.data.common.ErrorType
import me.khruslan.cryptograph.data.fixtures.STUB_COIN_NOTIFICATIONS
import me.khruslan.cryptograph.data.managers.CoinNotification
import me.khruslan.cryptograph.data.managers.CoinNotificationsManager

class FakeCoinNotificationsManager : CoinNotificationsManager {
    var isNetworkReachable = true

    override fun getCoinNotifications(coinId: String?): Flow<List<CoinNotification>> {
        checkIfNetworkIsReachable()
        return flowOf(STUB_COIN_NOTIFICATIONS.filter { coinId?.equals(it.coin.id) ?: true })
    }

    private fun checkIfNetworkIsReachable() {
        if (!isNetworkReachable) throw object : DataException(ErrorType.Network) {}
    }
}