package me.khruslan.cryptograph.ui.common

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.core.graphics.toColorInt
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

internal fun String?.toColor(): Color {
    return if (this != null) {
        Color(toColorInt())
    } else {
        Color.Unspecified
    }
}

@Composable
internal fun previewPlaceholder(icon: ImageVector): Painter? {
    return if (LocalInspectionMode.current) {
        rememberVectorPainter(icon)
    } else {
        null
    }
}