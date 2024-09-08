package me.khruslan.cryptograph.ui.coins.shared

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import me.khruslan.cryptograph.ui.R
import me.khruslan.cryptograph.ui.core.CryptoGraphTheme
import me.khruslan.cryptograph.ui.util.StarOutline

@Composable
internal fun PinCoinButton(
    isPinned: Boolean,
    onPin: () -> Unit,
    onUnpin: () -> Unit,
) {
    if (isPinned) {
        IconButton(onClick = onUnpin) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = stringResource(R.string.pin_coin_btn_desc)
            )
        }
    } else {
        IconButton(onClick = onPin) {
            Icon(
                imageVector = Icons.Default.StarOutline,
                contentDescription = stringResource(R.string.unpin_coin_btn_desc)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PinCoinButtonPreview() {
    var selected by remember {
        mutableStateOf(false)
    }

    CryptoGraphTheme {
        PinCoinButton(
            isPinned = selected,
            onPin = { selected = true },
            onUnpin = { selected = false }
        )
    }
}