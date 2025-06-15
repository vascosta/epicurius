package android.epicurius.ui.screens.user.preferences.lists

import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import android.epicurius.ui.EpicuriusActivity
import android.epicurius.ui.navigation.navigateTo
import android.epicurius.ui.screens.feed.FeedActivity
import android.os.Bundle
import androidx.activity.compose.setContent

class PreferencesActivity : EpicuriusActivity() {
    override val viewModel: PreferencesViewModel by getViewModel<PreferencesViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PreferencesScreen(
                onSetIntolerances = { intolerances: Set<Intolerance>? ->
                    viewModel.updatePreferences(intolerances, null)
                },
                onSetDiets = { diets: Set<Diet>? ->
                    viewModel.updatePreferences(null, diets)
                },
                onDone = { navigateTo<FeedActivity>(finishCurrent = true) },
                enableButtons = viewModel.enableButtons
            )
        }
    }
}
