package android.epicurius.ui.navigation

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity

inline fun <reified T : ComponentActivity> Context.navigateTo(configIntent: (Intent) -> Unit = {}) {
    val intent = Intent(this, T::class.java)
    configIntent(intent)
    startActivity(intent)
}
