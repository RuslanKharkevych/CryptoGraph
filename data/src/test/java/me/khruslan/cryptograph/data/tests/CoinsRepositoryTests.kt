package me.khruslan.cryptograph.data.tests

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import me.khruslan.cryptograph.data.coins.CoinsRepository
import me.khruslan.cryptograph.data.coins.CoinsRepositoryImpl
import me.khruslan.cryptograph.data.mocks.InMemoryCoinsStore
import me.khruslan.cryptograph.data.mocks.MOCK_COINS
import me.khruslan.cryptograph.data.mocks.MockCoinsService
import org.junit.Before
import org.junit.Test

class CoinsRepositoryTests {

    private lateinit var coinsRepository: CoinsRepository

    @Before
    fun setUp() {
        coinsRepository = CoinsRepositoryImpl(
            localDataSource = InMemoryCoinsStore(),
            remoteDataSource = MockCoinsService()
        )
    }

    @Test
    fun `Get coins`() = runTest {
        coinsRepository.coins.test {
            assertThat(awaitItem()).isEqualTo(MOCK_COINS)
        }
    }

    @Test
    fun `Pin coin`() = runTest {
        val coin = MOCK_COINS[1]
        coinsRepository.pinCoin(coin.id)

        coinsRepository.coins.test {
            val expectedCoin = coin.copy(isPinned = true)
            assertThat(awaitItem()).contains(expectedCoin)
        }
    }

    @Test
    fun `Unpin coin`() = runTest {
        val coinId = MOCK_COINS[2].id
        coinsRepository.pinCoin(coinId)
        coinsRepository.unpinCoin(coinId)

        coinsRepository.coins.test {
            assertThat(awaitItem()).isEqualTo(MOCK_COINS)
        }
    }
}