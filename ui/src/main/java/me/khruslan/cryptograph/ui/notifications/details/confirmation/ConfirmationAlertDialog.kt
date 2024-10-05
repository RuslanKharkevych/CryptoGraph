package me.khruslan.cryptograph.ui.notifications.details.confirmation

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import me.khruslan.cryptograph.base.Logger
import me.khruslan.cryptograph.ui.R
import me.khruslan.cryptograph.ui.core.CryptoGraphTheme

private const val LOG_TAG = "ConfirmationAlertDialog"

@Composable
internal fun ConfirmationAlertDialog(
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    fun confirmAndDismiss() {
        Logger.info(LOG_TAG, "Confirmed")
        onConfirm()
        onDismiss()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Text(
                text = message,
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
        ConfirmationAlertDialog(
            message = stringResource(R.string.delete_notification_alert_msg),
            onConfirm = {},
            onDismiss = {}
        )
    }
}