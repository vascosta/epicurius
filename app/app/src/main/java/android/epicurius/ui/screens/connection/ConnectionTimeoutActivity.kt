package android.epicurius.ui.screens.connection

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class ConnectionTimeoutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ConnectionTimeoutScreen()
        }
    }
}
