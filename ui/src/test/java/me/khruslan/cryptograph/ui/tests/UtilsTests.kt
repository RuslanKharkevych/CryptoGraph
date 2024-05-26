package me.khruslan.cryptograph.ui.tests

import com.google.common.truth.Truth.assertThat
import me.khruslan.cryptograph.data.common.DataException
import me.khruslan.cryptograph.data.common.ErrorType
import me.khruslan.cryptograph.ui.R
import me.khruslan.cryptograph.ui.common.displayMessageRes
import org.junit.Test

internal class UtilsTests {
    
    @Test
    fun `Get display message - network error`() {
        val exception = object : DataException(ErrorType.Network) {}
        val expectedDisplayMessageRes = R.string.network_error_msg
        val actualDisplayMessageRes = exception.displayMessageRes
        assertThat(expectedDisplayMessageRes).isEqualTo(actualDisplayMessageRes)
    }

    @Test
    fun `Get display message - server error`() {
        val exception = object : DataException(ErrorType.Server) {}
        val expectedDisplayMessageRes = R.string.server_error_msg
        val actualDisplayMessageRes = exception.displayMessageRes
        assertThat(expectedDisplayMessageRes).isEqualTo(actualDisplayMessageRes)
    }

    @Test
    fun `Get display message - database error`() {
        val exception = object : DataException(ErrorType.Database) {}
        val expectedDisplayMessageRes = R.string.database_error_msg
        val actualDisplayMessageRes = exception.displayMessageRes
        assertThat(expectedDisplayMessageRes).isEqualTo(actualDisplayMessageRes)
    }

    @Test
    fun `Get display message - internal error`() {
        val exception = object : DataException(ErrorType.Internal) {}
        val expectedDisplayMessageRes = R.string.internal_error_msg
        val actualDisplayMessageRes = exception.displayMessageRes
        assertThat(expectedDisplayMessageRes).isEqualTo(actualDisplayMessageRes)
    }
}