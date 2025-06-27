package android.epicurius.ui.screens.utils

import androidx.compose.runtime.Composable

@Composable
fun getNameFromLoadStateValue(nameState: LoadState<String>): String {
    return when (nameState) {
        is Loaded -> nameState.getOrThrow()
        is Loading -> nameState.getOrNull() ?: "Loading..."
        is Idle -> "Loading..."
    }
}