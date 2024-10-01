package me.khruslan.cryptograph.data.tests

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import me.khruslan.cryptograph.data.fakes.FakeCoinsRepository
import me.khruslan.cryptograph.data.fakes.FakeNotificationsRepository
import me.khruslan.cryptograph.data.fixtures.STUB_COIN_NOTIFICATIONS
import me.khruslan.cryptograph.data.interactors.combine.CoinNotificationsInteractor
import me.khruslan.cryptograph.data.interactors.combine.CoinNotificationsInteractorImpl
import me.khruslan.cryptograph.data.interactors.combine.CoinNotificationsMapper
import org.junit.Before
import org.junit.Test

internal class CoinNotificationsInteractorTests {

    private lateinit var interactor: CoinNotificationsInteractor

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        val dispatcher = UnconfinedTestDispatcher()

        interactor = CoinNotificationsInteractorImpl(
            coinsRepository = FakeCoinsRepository(),
            notificationsRepository = FakeNotificationsRepository(),
            mapper = CoinNotificationsMapper(dispatcher)
        )
    }

    @Test
    fun `Get coin notifications`() = runTest {
        interactor.getCoinNotifications().test {
            assertThat(awaitItem()).isEqualTo(STUB_COIN_NOTIFICATIONS)
        }
    }

    @Test
    fun `Filter coin notifications`() = runTest {
        val coinNotification = STUB_COIN_NOTIFICATIONS[0]
        interactor.getCoinNotifications(coinNotification.coin.id).test {
            assertThat(awaitItem()).containsExactly(coinNotification)
        }
    }
}