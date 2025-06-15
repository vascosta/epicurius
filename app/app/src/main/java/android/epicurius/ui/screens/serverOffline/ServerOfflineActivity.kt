package android.epicurius.ui.screens.serverOffline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class ServerOfflineActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ServerOfflineScreen()
        }
    }
}
