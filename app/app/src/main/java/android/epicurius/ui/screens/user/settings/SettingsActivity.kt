package android.epicurius.ui.screens.user.settings

import android.epicurius.MainActivity
import android.epicurius.ui.screens.utils.navigateTo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                SettingsScreen(
                    onBackButton = { navigateTo<MainActivity>() }
                )
            }
        }
    }
}