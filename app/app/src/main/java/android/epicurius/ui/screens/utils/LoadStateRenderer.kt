package android.epicurius.ui.screens.utils

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun <T> LoadStateRenderer(
    loadState: LoadState<T>,
    content: @Composable (T) -> Unit = {},
) {
    when (loadState) {
        is Idle -> {}
        is Loading -> {
            if (loadState.cachedValue != null) {
                when {
                    loadState.cachedValue.isSuccess -> RenderSuccess(
                        loadState.getOrThrow(),
                        { value ->
                            content(value)
                            LoadingSpinner(Modifier.size(30.dp))
                        }
                    )
                }
            }
            else {
                LoadingSpinner()
            }

        }
        is Loaded -> {
            when {
                loadState.value.isSuccess -> RenderSuccess(loadState.getOrThrow(), content)
            }
        }
    }
}

@Composable
fun <T> RenderSuccess(
    value: T,
    content: @Composable (T) -> Unit = {}
) {
    content(value)
}