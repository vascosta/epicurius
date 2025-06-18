package android.epicurius.ui.screens.feed.components

import android.epicurius.R
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FollowRequestButtons(
    onAccept: () -> Unit,
    onReject: () -> Unit,
    enableButtons: Boolean
) {
    Row {
        FollowRequestIconButton(
            iconResId = R.drawable.accept,
            contentDescription = "Accept Follow Request",
            onClick = onAccept,
            enabled = enableButtons
        )
        Spacer(modifier = Modifier.size(10.dp))
        FollowRequestIconButton(
            iconResId = R.drawable.reject,
            contentDescription = "Reject Follow Request",
            onClick = onReject,
            enabled = enableButtons
        )
    }
}
