package android.epicurius.ui.navigation

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity

inline fun <reified T : ComponentActivity> Context.navigateTo(
    useStack: Boolean = false,
    finishCurrent: Boolean = false,
    configIntent: (Intent) -> Unit = {}
) {
    val intent = Intent(this, T::class.java)
    if (useStack) {
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
    }
    configIntent(intent)
    startActivity(intent)

    if (this is Activity && finishCurrent) {
        this.finish()
    }
}

