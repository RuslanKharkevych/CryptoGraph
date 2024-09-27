package me.khruslan.cryptograph.ui.notifications.details.confirmation

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import me.khruslan.cryptograph.ui.R
import me.khruslan.cryptograph.ui.core.CryptoGraphTheme

@Composable
internal fun DeleteNotificationConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    fun confirmAndDismiss() {
        onConfirm()
        onDismiss()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Text(
                text = stringResource(R.string.delete_notification_alert_title),
                style = MaterialTheme.typography.bodyLarge
            )
        },
        confirmButton = {
            TextButton(onClick = ::confirmAndDismiss) {
                Text(
                    text = stringResource(R.string.dialog_positive_btn),
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.dialog_negative_btn))
            }
        }
    )
}

@Composable
@PreviewLightDark
private fun DeleteNotificationConfirmationDialogPreview() {
    CryptoGraphTheme {
        DeleteNotificationConfirmationDialog(
            onConfirm = {},
            onDismiss = {}
        )
    }
}