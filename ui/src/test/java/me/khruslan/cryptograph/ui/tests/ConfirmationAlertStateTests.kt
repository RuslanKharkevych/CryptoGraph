package me.khruslan.cryptograph.ui.tests

import com.google.common.truth.Truth.assertThat
import me.khruslan.cryptograph.ui.notifications.details.confirmation.ConfirmationAlertState
import me.khruslan.cryptograph.ui.notifications.details.confirmation.ConfirmationAlertStateImpl
import org.junit.Before
import org.junit.Test

class ConfirmationAlertStateTests {

    private lateinit var alertState: ConfirmationAlertState

    @Before
    fun setUp() {
        alertState = ConfirmationAlertStateImpl()
    }

    @Test
    fun show() {
        alertState.show()
        assertThat(alertState.isVisible).isTrue()
    }

    @Test
    fun dismiss() {
        alertState.show()
        alertState.dismiss()

        assertThat(alertState.isVisible).isFalse()
    }
}