package me.khruslan.cryptograph.ui.core

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import org.koin.androidx.viewmodel.ext.android.viewModel

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
    }
}