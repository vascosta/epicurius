package android.epicurius.ui.screens.user.preferences.lists

import android.epicurius.ui.navigation.navigateTo
import android.epicurius.ui.screens.feed.FeedActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class PreferencesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PreferencesScreen(
                onSetIntolerances = { intolerances -> },
                onSetDiets = { diets -> },
                onDone = { navigateTo<FeedActivity>() },
                enableButtons = true
            )
        }
    }
}
