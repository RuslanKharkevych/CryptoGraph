package me.khruslan.cryptograph.data.tests

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import me.khruslan.cryptograph.data.coins.local.CoinsLocalDataSource
import me.khruslan.cryptograph.data.coins.local.CoinsLocalDataSourceImpl
import me.khruslan.cryptograph.data.coins.local.PinnedCoinDto
import me.khruslan.cryptograph.data.rules.ObjectBoxRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
internal class CoinsLocalDataSourceTests {

    @get:Rule
    val objectBoxRule = ObjectBoxRule()

    private lateinit var dataSource: CoinsLocalDataSource

    @Before
    fun setUp() {
        dataSource = CoinsLocalDataSourceImpl(
            box = objectBoxRule.getBox(),
            dispatcher = UnconfinedTestDispatcher()
        )
    }

    @Test
    fun `Pin coin`() = runTest {
        val uuid = UUID.randomUUID().toString()
        dataSource.pinCoin(uuid)

        dataSource.pinnedCoins.test {
            val expectedCoin = PinnedCoinDto(id = 1, coinUuid = uuid)
            assertThat(awaitItem()).contains(expectedCoin)
        }
    }

    @Test
    fun `Unpin coin`() = runTest {
        val uuid = UUID.randomUUID().toString()
        dataSource.pinCoin(uuid)
        dataSource.unpinCoin(uuid)

        dataSource.pinnedCoins.test {
            assertThat(awaitItem()).isEmpty()
        }
    }
}