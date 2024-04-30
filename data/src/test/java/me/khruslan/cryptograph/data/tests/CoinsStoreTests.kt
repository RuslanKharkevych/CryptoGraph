package me.khruslan.cryptograph.data.tests

import com.google.common.truth.Truth.assertThat
import me.khruslan.cryptograph.data.coins.local.CoinsStore
import me.khruslan.cryptograph.data.coins.local.PinnedCoinDto
import me.khruslan.cryptograph.data.rules.ObjectBoxRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.UUID

class CoinsStoreTests {

    @get:Rule
    val objectBoxRule = ObjectBoxRule()

    private lateinit var coinsStore: CoinsStore

    @Before
    fun setUp() {
        coinsStore = CoinsStore(objectBoxRule.getBox())
    }

    @Test
    fun `Pin coin`() {
        val uuid = UUID.randomUUID().toString()
        coinsStore.pinCoin(uuid)

        val coins = coinsStore.getPinnedCoins()
        val expectedCoin = PinnedCoinDto(id = 1, coinUuid = uuid)
        assertThat(coins).contains(expectedCoin)
    }

    @Test
    fun `Unpin coin`() {
        val uuid = UUID.randomUUID().toString()
        coinsStore.pinCoin(uuid)
        coinsStore.unpinCoin(uuid)

        val coins = coinsStore.getPinnedCoins()
        assertThat(coins).isEmpty()
    }
}