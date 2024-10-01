package me.khruslan.cryptograph.data.interactors.combine

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import me.khruslan.cryptograph.data.coins.CoinsRepository
import me.khruslan.cryptograph.data.notifications.NotificationsRepository

interface CoinNotificationsInteractor {
    fun getCoinNotifications(coinId: String? = null): Flow<List<CoinNotification>>
}

internal class CoinNotificationsInteractorImpl(
    private val coinsRepository: CoinsRepository,
    private val notificationsRepository: NotificationsRepository,
    private val mapper: CoinNotificationsMapper,
) : CoinNotificationsInteractor {

    override fun getCoinNotifications(coinId: String?): Flow<List<CoinNotification>> {
        val coinsFlow = coinsRepository.getCoins(coinId)
        val notificationsFlow = notificationsRepository.getNotifications(coinId)

        return coinsFlow.combine(notificationsFlow) { coins, notifications ->
            mapper.mapCoinNotifications(coins, notifications)
        }
    }
}