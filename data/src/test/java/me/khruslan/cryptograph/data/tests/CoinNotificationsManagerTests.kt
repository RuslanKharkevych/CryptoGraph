package me.khruslan.cryptograph.data.tests

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import me.khruslan.cryptograph.data.fakes.FakeCoinsRepository
import me.khruslan.cryptograph.data.fakes.FakeNotificationsRepository
import me.khruslan.cryptograph.data.fixtures.STUB_COIN_NOTIFICATIONS
import me.khruslan.cryptograph.data.managers.CoinNotificationsManager
import me.khruslan.cryptograph.data.managers.CoinNotificationsManagerImpl
import me.khruslan.cryptograph.data.managers.CoinNotificationsMapper
import org.junit.Before
import org.junit.Test

internal class CoinNotificationsManagerTests {

    private lateinit var coinNotificationsManager: CoinNotificationsManager

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        val dispatcher = UnconfinedTestDispatcher()

        coinNotificationsManager = CoinNotificationsManagerImpl(
            coinsRepository = FakeCoinsRepository(),
            notificationsRepository = FakeNotificationsRepository(),
            mapper = CoinNotificationsMapper(dispatcher)
        )
    }

    @Test
    fun `Get coin notifications`() = runTest {
        coinNotificationsManager.getCoinNotifications(null).test {
            assertThat(awaitItem()).isEqualTo(STUB_COIN_NOTIFICATIONS)
        }
    }

    @Test
    fun `Filter coin notifications`() = runTest {
        val coinNotification = STUB_COIN_NOTIFICATIONS[0]
        coinNotificationsManager.getCoinNotifications(coinNotification.coin.id).test {
            assertThat(awaitItem()).containsExactly(coinNotification)
        }
    }
}