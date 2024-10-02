package me.khruslan.cryptograph.ui.coins.shared

import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import me.khruslan.cryptograph.ui.R
import me.khruslan.cryptograph.ui.core.CryptoGraphTheme
import me.khruslan.cryptograph.ui.util.preview.PreviewLightDarkWithBackground

@Composable
internal fun NotificationsAction(unreadNotificationsCount: Int, onClick: () -> Unit) {
    BadgedBox(
        badge = {
            if (unreadNotificationsCount > 0) {
                Badge(modifier = Modifier.offset(x = (-7).dp, y = 6.dp)) {
                    Text(unreadNotificationsCount.toString())
                }
            }
        }
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = stringResource(R.string.notifications_action_desc),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
@PreviewLightDarkWithBackground
private fun NotificationsActionPreview() {
    CryptoGraphTheme {
        NotificationsAction(
            unreadNotificationsCount = 1,
            onClick = {}
        )
    }
}