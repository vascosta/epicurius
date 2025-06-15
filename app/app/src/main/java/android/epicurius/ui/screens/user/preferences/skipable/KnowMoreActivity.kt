package android.epicurius.ui.screens.user.preferences.skipable

import android.epicurius.ui.navigation.navigateTo
import android.epicurius.ui.screens.feed.FeedActivity
import android.epicurius.ui.screens.user.preferences.lists.PreferencesActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class KnowMoreActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KnowMoreScreen(
                onSkip = { navigateTo<FeedActivity>(finishCurrent = true) },
                onNext = { navigateTo<PreferencesActivity>(finishCurrent = true) },
                enableButtons = true
            )
        }
    }
}
