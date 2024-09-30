package me.khruslan.cryptograph.ui.tests

import androidx.compose.ui.text.input.TextFieldValue
import com.google.common.truth.Truth.assertThat
import me.khruslan.cryptograph.data.coins.Coin
import me.khruslan.cryptograph.data.fixtures.STUB_COINS
import me.khruslan.cryptograph.data.fixtures.STUB_COIN_NOTIFICATIONS
import me.khruslan.cryptograph.data.notifications.Notification
import me.khruslan.cryptograph.ui.coins.shared.CoinInfo
import me.khruslan.cryptograph.ui.notifications.details.NotificationDetailsFormState
import me.khruslan.cryptograph.ui.notifications.details.NotificationDetailsFormStateImpl
import me.khruslan.cryptograph.ui.notifications.details.NotificationTitleState
import me.khruslan.cryptograph.ui.notifications.details.NotificationTriggerPriceState
import me.khruslan.cryptograph.ui.notifications.details.NotificationTriggerType
import org.junit.Assert.fail
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

private val CLOCK: Clock = Clock.fixed(
    Instant.parse("2024-09-26T22:24:32.00Z"),
    ZoneId.of("Australia/Sydney")
)

internal class NotificationDetailsFormStateTests {

    private lateinit var formState: NotificationDetailsFormState

    private fun initFormState(
        coin: Coin = STUB_COINS[0],
        notification: Notification? = null,
    ) {
        formState = NotificationDetailsFormStateImpl(
            coinInfo = CoinInfo.fromCoin(coin),
            notification = notification,
            clock = CLOCK
        )
    }

    @Test
    fun `Prefill fields`() {
        val (coin, notification) = STUB_COIN_NOTIFICATIONS[1]
        initFormState(coin, notification)

        with(formState) {
            assertThat(this.coinInfo).isEqualTo(CoinInfo.fromCoin(coin))
            assertThat(notificationTitle).isEqualTo(TextFieldValue(notification.title))
            assertThat(notificationTitleState).isEqualTo(NotificationTitleState.Default)
            assertThat(triggerType).isEqualTo(NotificationTriggerType.PriceMoreThan)
            assertThat(triggerTypeDropdownExpanded).isFalse()
            assertThat(triggerPrice)
                .isEqualTo(TextFieldValue(notification.trigger.targetPrice.toString()))
            assertThat(triggerPriceState).isEqualTo(NotificationTriggerPriceState.Default)
            assertThat(expirationDate).isEqualTo(notification.expirationDate)
            assertThat(expirationDatePickerVisible).isFalse()
        }
    }

    @Test
    fun `Build notification - success`() {
        val (coin, notification) = STUB_COIN_NOTIFICATIONS[2]
        initFormState(coin)

        with(formState) {
            updateNotificationTitle(TextFieldValue(notification.title))
            updateTriggerType(NotificationTriggerType.PriceLessThan)
            updateTriggerPrice(TextFieldValue(notification.trigger.targetPrice.toString()))
            updateExpirationDate(notification.expirationDate)

            val expectedNotification = notification.copy(id = 0L, createdAt = LocalDate.now(CLOCK))
            var actualNotification: Notification? = null
            buildNotification { actualNotification = it }
            assertThat(actualNotification).isEqualTo(expectedNotification)
        }
    }

    @Test
    fun `Build notification - notification title invalid`() {
        initFormState()
        formState.updateNotificationTitle(TextFieldValue())
        formState.buildNotification(onSuccess = { fail() })

        val expectedState = NotificationTitleState.Blank
        val actualState = formState.notificationTitleState
        assertThat(actualState).isEqualTo(expectedState)
    }

    @Test
    fun `Build notification - trigger price invalid`() {
        initFormState()
        formState.updateTriggerPrice(TextFieldValue())
        formState.buildNotification(onSuccess = { fail() })

        val expectedState = NotificationTriggerPriceState.Empty
        val actualState = formState.triggerPriceState
        assertThat(actualState).isEqualTo(expectedState)
    }

    @Test
    fun `Validate notification title - focused`() {
        initFormState()
        formState.validateNotificationTitle(isFocused = true)

        val expectedState = NotificationTitleState.Default
        val actualState = formState.notificationTitleState
        assertThat(actualState).isEqualTo(expectedState)
    }

    @Test
    fun `Validate notification title - valid`() {
        initFormState()
        formState.updateNotificationTitle(TextFieldValue("Tether USD < 1$"))
        formState.validateNotificationTitle(isFocused = false)

        val expectedState = NotificationTitleState.Default
        val actualState = formState.notificationTitleState
        assertThat(actualState).isEqualTo(expectedState)
    }

    @Test
    fun `Validate notification title - blank`() {
        initFormState()
        formState.updateNotificationTitle(TextFieldValue(" "))
        formState.validateNotificationTitle(isFocused = false)

        val expectedState = NotificationTitleState.Blank
        val actualState = formState.notificationTitleState
        assertThat(actualState).isEqualTo(expectedState)
    }

    @Test
    fun `Update trigger type - trigger price unfocused`() {
        initFormState()
        formState.updateTriggerPrice(TextFieldValue("20000"))
        formState.updateTriggerType(NotificationTriggerType.PriceMoreThan)

        val expectedState = NotificationTriggerPriceState.PriceTooSmall
        val actualState = formState.triggerPriceState
        assertThat(actualState).isEqualTo(expectedState)
    }

    @Test
    fun `Update trigger type - trigger price focused`() {
        initFormState()
        formState.validateTriggerPrice(isFocused = true)
        formState.updateTriggerPrice(TextFieldValue("20000"))
        formState.updateTriggerType(NotificationTriggerType.PriceMoreThan)

        val expectedState = NotificationTriggerPriceState.Default
        val actualState = formState.triggerPriceState
        assertThat(actualState).isEqualTo(expectedState)
    }

    @Test
    fun `Expand trigger dropdown`() {
        initFormState()
        formState.expandTriggerTypeDropdown()

        assertThat(formState.triggerTypeDropdownExpanded).isTrue()
    }

    @Test
    fun `Collapse trigger dropdown`() {
        initFormState()
        formState.expandTriggerTypeDropdown()
        formState.collapseTriggerTypeDropdown()

        assertThat(formState.triggerTypeDropdownExpanded).isFalse()
    }

    @Test
    fun `Update trigger price`() {
        initFormState()

        val triggerPrice = TextFieldValue("2")
        formState.updateTriggerPrice(triggerPrice)
        formState.updateTriggerPrice(TextFieldValue("2,"))
        formState.updateTriggerPrice(TextFieldValue("2-"))

        assertThat(formState.triggerPrice).isEqualTo(triggerPrice)
    }

    @Test
    fun `Validate trigger price - focused`() {
        initFormState()
        formState.validateTriggerPrice(isFocused = true)

        val expectedState = NotificationTriggerPriceState.Default
        val actualState = formState.triggerPriceState
        assertThat(actualState).isEqualTo(expectedState)
    }

    @Test
    fun `Validate trigger price - empty`() {
        initFormState()
        formState.updateTriggerPrice(TextFieldValue())
        formState.validateTriggerPrice(isFocused = false)

        val expectedState = NotificationTriggerPriceState.Empty
        val actualState = formState.triggerPriceState
        assertThat(actualState).isEqualTo(expectedState)
    }

    @Test
    fun `Validate trigger price - invalid format`() {
        initFormState()
        formState.updateTriggerPrice(TextFieldValue("."))
        formState.validateTriggerPrice(isFocused = false)

        val expectedState = NotificationTriggerPriceState.InvalidFormat
        val actualState = formState.triggerPriceState
        assertThat(actualState).isEqualTo(expectedState)
    }

    @Test
    fun `Validate trigger price - coin price unknown`() {
        val coin = STUB_COINS[1].copy(price = null)
        initFormState(coin)

        formState.updateTriggerPrice(TextFieldValue("60000"))
        formState.validateTriggerPrice(isFocused = false)

        val expectedState = NotificationTriggerPriceState.Default
        val actualState = formState.triggerPriceState
        assertThat(actualState).isEqualTo(expectedState)
    }

    @Test
    fun `Validate trigger price - coin price invalid`() {
        val coin = STUB_COINS[2].copy(price = "N/A")
        initFormState(coin)

        formState.updateTriggerPrice(TextFieldValue("55000"))
        formState.validateTriggerPrice(isFocused = false)

        val expectedState = NotificationTriggerPriceState.Default
        val actualState = formState.triggerPriceState
        assertThat(actualState).isEqualTo(expectedState)
    }

    @Test
    fun `Validate trigger price - price less than`() {
        initFormState()
        formState.updateTriggerType(NotificationTriggerType.PriceLessThan)
        formState.updateTriggerPrice(TextFieldValue("50000.0"))
        formState.validateTriggerPrice(isFocused = false)

        val expectedState = NotificationTriggerPriceState.Default
        val actualState = formState.triggerPriceState
        assertThat(actualState).isEqualTo(expectedState)
    }

    @Test
    fun `Validate trigger price - price too big`() {
        initFormState()
        formState.updateTriggerType(NotificationTriggerType.PriceLessThan)
        formState.updateTriggerPrice(TextFieldValue("70000.00"))
        formState.validateTriggerPrice(isFocused = false)

        val expectedState = NotificationTriggerPriceState.PriceTooBig
        val actualState = formState.triggerPriceState
        assertThat(actualState).isEqualTo(expectedState)
    }

    @Test
    fun `Validate trigger price - price more than`() {
        initFormState()
        formState.updateTriggerType(NotificationTriggerType.PriceMoreThan)
        formState.updateTriggerPrice(TextFieldValue("75000"))
        formState.validateTriggerPrice(isFocused = false)

        val expectedState = NotificationTriggerPriceState.Default
        val actualState = formState.triggerPriceState
        assertThat(actualState).isEqualTo(expectedState)
    }

    @Test
    fun `Validate trigger price - price too small`() {
        initFormState()
        formState.updateTriggerType(NotificationTriggerType.PriceMoreThan)
        formState.updateTriggerPrice(TextFieldValue("45000"))
        formState.validateTriggerPrice(isFocused = false)

        val expectedState = NotificationTriggerPriceState.PriceTooSmall
        val actualState = formState.triggerPriceState
        assertThat(actualState).isEqualTo(expectedState)
    }

    @Test
    fun `Show expiration date picker`() {
        initFormState()
        formState.showExpirationDatePicker()

        assertThat(formState.expirationDatePickerVisible).isTrue()
    }

    @Test
    fun `Dismiss expiration date picker`() {
        initFormState()
        formState.showExpirationDatePicker()
        formState.dismissExpirationDatePicker()

        assertThat(formState.expirationDatePickerVisible).isFalse()
    }
}