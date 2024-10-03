package me.khruslan.cryptograph.ui.core

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import me.khruslan.cryptograph.base.Logger
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val LOG_TAG = "CryptoGraphActivity"
private const val EXTRA_CANCEL_NOTIFICATIONS = "me.khruslan.cryptograph.CANCEL_NOTIFICATIONS"

class CryptoGraphActivity : AppCompatActivity() {

    private val viewModel: CryptoGraphViewModel by viewModel()
    private val appState get() = viewModel.appState

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(appState.theme)
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            CryptoGraphTheme(appState.theme) {
                CryptoGraphNavHost()
            }
        }

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (intent.getBooleanExtra(EXTRA_CANCEL_NOTIFICATIONS, false)) {
            Logger.info(LOG_TAG, "Cancelling all push notifications")
            NotificationManagerCompat.from(this).cancelAll()
        }
    }

    companion object {
        fun newNotificationIntent(context: Context): Intent {
            return Intent(context, CryptoGraphActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                .putExtra(EXTRA_CANCEL_NOTIFICATIONS, true)
        }
    }
}