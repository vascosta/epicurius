package android.epicurius.ui.screens.favourites.folder

import android.epicurius.MainActivity
import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.ui.screens.utils.navigateTo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class FavouritesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FavouritesScreen(
                onBackButton = { navigateTo<MainActivity>() },
                collections = listOf(
                    CollectionProfile(1, "Italian Delights"),
                    CollectionProfile(2, "Quick Snacks"),
                    CollectionProfile(3, "Healthy Meals")
                )
            )
        }
    }
}