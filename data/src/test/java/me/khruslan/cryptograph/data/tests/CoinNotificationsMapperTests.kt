package me.khruslan.cryptograph.data.tests

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import me.khruslan.cryptograph.data.fixtures.STUB_COINS
import me.khruslan.cryptograph.data.fixtures.STUB_COIN_NOTIFICATIONS
import me.khruslan.cryptograph.data.fixtures.STUB_NOTIFICATIONS
import me.khruslan.cryptograph.data.managers.CoinNotificationsMapper
import me.khruslan.cryptograph.data.managers.CoinNotificationsMapperImpl
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class CoinNotificationsMapperTests {

    private lateinit var mapper: CoinNotificationsMapper

    @Before
    fun setUp() {
        val dispatcher = UnconfinedTestDispatcher()
        mapper = CoinNotificationsMapperImpl(dispatcher)
    }

    @Test
    fun `Map coin notifications`() = runTest {
        val coinNotifications = mapper.mapCoinNotifications(STUB_COINS, STUB_NOTIFICATIONS)
        assertThat(coinNotifications).isEqualTo(STUB_COIN_NOTIFICATIONS)
    }
}