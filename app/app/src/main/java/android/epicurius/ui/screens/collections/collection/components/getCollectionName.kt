package android.epicurius.ui.screens.collections.collection.components

import android.epicurius.ui.screens.utils.Idle
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.Loaded
import android.epicurius.ui.screens.utils.Loading
import android.epicurius.ui.screens.utils.getOrNull
import android.epicurius.ui.screens.utils.getOrThrow
import androidx.compose.runtime.Composable

@Composable
fun getCollectionName(nameState: LoadState<String>): String {
    return when (nameState) {
        is Loaded -> nameState.getOrThrow()
        is Loading -> nameState.getOrNull() ?: "Loading..."
        is Idle -> "Loading..."
    }
}