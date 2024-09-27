package me.khruslan.cryptograph.ui.tests

import com.google.common.truth.Truth.assertThat
import me.khruslan.cryptograph.ui.util.AlertState
import me.khruslan.cryptograph.ui.util.AlertStateImpl
import org.junit.Before
import org.junit.Test

class AlertStateTests {

    private lateinit var alertState: AlertState

    @Before
    fun setUp() {
        alertState = AlertStateImpl()
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