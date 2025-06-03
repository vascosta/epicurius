package android.epicurius.ui.screens.utils

import androidx.compose.runtime.Composable

@Composable
fun <T> LoadStateRenderer(
    loadState: LoadState<T>,
    swipeToRefresh: (() -> Unit)? = null,
    content: @Composable (T) -> Unit = {}
) {
    when (loadState) {
        is Idle -> {}
        is Loading -> {} //LoadingSpinner maybe?
        is Loaded -> {
            when {
                loadState.value.isSuccess -> RenderSuccess(loadState.getOrThrow(), swipeToRefresh, content)
                loadState.value.isFailure -> RenderFailure(swipeToRefresh)
            }
        }
        is Cached -> RenderSuccess(loadState.getOrThrow(), swipeToRefresh, content)
    }
}

@Composable
fun <T> RenderSuccess(
    value: T,
    swipeToRefresh: (() -> Unit)?,
    content: @Composable (T) -> Unit = {}
) {
    if (swipeToRefresh != null) {
        content(value)
/*        PullToRefresh(onRefresh = pullToRefresh) {
            content(value)
        }*/
    } else {
        content(value)
    }
}

@Composable
fun RenderFailure(
    swipeToRefresh: (() -> Unit)?
) {
    /*if (pullToRefresh != null) {
        PullToRefresh(onRefresh = pullToRefresh) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = stringResource(R.string.pull_to_refresh_icon),
                    modifier = Modifier.size(35.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Spacer(10.dp)
                Text(
                    text = stringResource(R.string.pull_to_refresh),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }*/
}