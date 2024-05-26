package me.khruslan.cryptograph.ui.tests

import androidx.compose.material.icons.Icons
import com.google.common.truth.Truth.assertThat
import me.khruslan.cryptograph.data.coins.ChangeTrend
import me.khruslan.cryptograph.ui.DarkGreen
import me.khruslan.cryptograph.ui.DarkRed
import me.khruslan.cryptograph.ui.DarkYellow
import me.khruslan.cryptograph.ui.R
import me.khruslan.cryptograph.ui.coins.color
import me.khruslan.cryptograph.ui.coins.contendDescRes
import me.khruslan.cryptograph.ui.coins.icon
import me.khruslan.cryptograph.ui.common.TrendingDown
import me.khruslan.cryptograph.ui.common.TrendingFlat
import me.khruslan.cryptograph.ui.common.TrendingUp
import org.junit.Test

internal class CoinsUtilsTests {

    @Test
    fun `Get change trend icon - up`() {
        val expectedIcon = Icons.AutoMirrored.Default.TrendingUp
        val actualIcon = ChangeTrend.UP.icon
        assertThat(actualIcon).isEqualTo(expectedIcon)
    }

    @Test
    fun `Get change trend icon - down`() {
        val expectedIcon = Icons.AutoMirrored.Default.TrendingDown
        val actualIcon = ChangeTrend.DOWN.icon
        assertThat(actualIcon).isEqualTo(expectedIcon)
    }

    @Test
    fun `Get change trend icon - flat`() {
        val expectedIcon = Icons.AutoMirrored.Default.TrendingFlat
        val actualIcon = ChangeTrend.STEADY_OR_UNKNOWN.icon
        assertThat(actualIcon).isEqualTo(expectedIcon)
    }

    @Test
    fun `Get change trend content description - up`() {
        val expectedContentDescRes = R.string.trending_up_icon_desc
        val actualContentDescRes = ChangeTrend.UP.contendDescRes
        assertThat(actualContentDescRes).isEqualTo(expectedContentDescRes)
    }

    @Test
    fun `Get change trend content description - down`() {
        val expectedContentDescRes = R.string.trending_down_icon_desc
        val actualContentDescRes = ChangeTrend.DOWN.contendDescRes
        assertThat(actualContentDescRes).isEqualTo(expectedContentDescRes)
    }

    @Test
    fun `Get change trend content description - flat`() {
        val expectedContentDescRes = R.string.trending_flat_icon_desc
        val actualContentDescRes = ChangeTrend.STEADY_OR_UNKNOWN.contendDescRes
        assertThat(actualContentDescRes).isEqualTo(expectedContentDescRes)
    }

    @Test
    fun `Get change trend color - up`() {
        val expectedColor = DarkGreen
        val actualColor = ChangeTrend.UP.color
        assertThat(actualColor).isEqualTo(expectedColor)
    }

    @Test
    fun `Get change trend color - down`() {
        val expectedColor = DarkRed
        val actualColor = ChangeTrend.DOWN.color
        assertThat(actualColor).isEqualTo(expectedColor)
    }

    @Test
    fun `Get change trend color - flat`() {
        val expectedColor = DarkYellow
        val actualColor = ChangeTrend.STEADY_OR_UNKNOWN.color
        assertThat(actualColor).isEqualTo(expectedColor)
    }
}