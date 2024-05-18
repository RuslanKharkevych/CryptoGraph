package me.khruslan.cryptograph.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class CryptoGraphActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CryptoGraphTheme {
                CryptoGraphNavHost()
            }
        }
    }
}