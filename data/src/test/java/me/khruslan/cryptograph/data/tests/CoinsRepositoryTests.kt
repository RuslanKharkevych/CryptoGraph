package me.khruslan.cryptograph.data.tests

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import me.khruslan.cryptograph.data.coins.CoinsRepository
import me.khruslan.cryptograph.data.coins.CoinsRepositoryImpl
import me.khruslan.cryptograph.data.fakes.FakeCoinsService
import me.khruslan.cryptograph.data.fakes.FakeCoinsStore
import me.khruslan.cryptograph.data.fakes.STUB_COINS
import org.junit.Before
import org.junit.Test

internal class CoinsRepositoryTests {

    private lateinit var coinsRepository: CoinsRepository

    @Before
    fun setUp() {
        coinsRepository = CoinsRepositoryImpl(
            localDataSource = FakeCoinsStore(),
            remoteDataSource = FakeCoinsService()
        )
    }

    @Test
    fun `Get coins`() = runTest {
        coinsRepository.coins.test {
            assertThat(awaitItem()).isEqualTo(STUB_COINS)
        }
    }

    @Test
    fun `Pin coin`() = runTest {
        val coin = STUB_COINS[1]
        coinsRepository.pinCoin(coin.id)

        coinsRepository.coins.test {
            val expectedCoin = coin.copy(isPinned = true)
            assertThat(awaitItem()).contains(expectedCoin)
        }
    }

    @Test
    fun `Unpin coin`() = runTest {
        val coinId = STUB_COINS[2].id
        coinsRepository.pinCoin(coinId)
        coinsRepository.unpinCoin(coinId)

        coinsRepository.coins.test {
            assertThat(awaitItem()).isEqualTo(STUB_COINS)
        }
    }
}