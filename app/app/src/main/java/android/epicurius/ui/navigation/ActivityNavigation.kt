package android.epicurius.ui.navigation

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity

inline fun <reified T : ComponentActivity> Context.navigateTo(
    useFlags: Boolean = false,
    configIntent: (Intent) -> Unit = {}
) {
    val intent = Intent(this, T::class.java)
    if (useFlags) {
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
    }
    configIntent(intent)
    startActivity(intent)
}

