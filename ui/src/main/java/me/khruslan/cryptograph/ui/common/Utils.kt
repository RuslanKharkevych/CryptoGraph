package me.khruslan.cryptograph.ui.common

import androidx.annotation.StringRes
import me.khruslan.cryptograph.data.common.DataException
import me.khruslan.cryptograph.data.common.ErrorType
import me.khruslan.cryptograph.ui.R

@get:StringRes
internal val DataException.displayMessageRes
    get() = when (errorType) {
        ErrorType.Network -> R.string.network_error_msg
        ErrorType.Server -> R.string.server_error_msg
        ErrorType.Database -> R.string.database_error_msg
    }