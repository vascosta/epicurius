package android.epicurius.ui.screens.favourites.folder.components

import android.epicurius.ui.screens.utils.Cached
import android.epicurius.ui.screens.utils.Idle
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.Loaded
import android.epicurius.ui.screens.utils.Loading
import android.epicurius.ui.screens.utils.getOrThrow
import androidx.compose.runtime.Composable

@Composable
fun getFavouritesListName(nameState: LoadState<String>): String {
    return when (nameState) {
        is Loaded -> nameState.getOrThrow()
        is Cached -> nameState.getOrThrow()
        is Loading -> "Loading..."
        is Idle -> "Favourites"
    }
}