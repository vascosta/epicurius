package android.epicurius.ui.screens.feed.components

import android.epicurius.domain.user.SearchUser
import android.epicurius.ui.screens.theme.Beige
import android.epicurius.ui.screens.theme.DarkGreen
import android.epicurius.ui.screens.theme.Lilac
import android.epicurius.ui.screens.utils.Idle
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.LoadStateRenderer
import android.epicurius.ui.screens.utils.Loaded
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun FollowRequestDialog(
    followRequestsState: LoadState<List<SearchUser>>,
    onAcceptFollowRequest: (name: String) -> Unit = {},
    onRejectFollowRequest: (name: String) -> Unit = {},
    onUserProfileRequest: (name: String) -> Unit = {},
    onFollowRequests: () -> Unit = {},
    onDismiss: () -> Unit = {},
    enableButtons: Boolean
) {
    LaunchedEffect(followRequestsState) {
        if (followRequestsState is Idle) onFollowRequests()
    }
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(
                onClick = { onDismiss() },
                enabled = enableButtons
            )
            { Text(text = "Close", color = Lilac) }
        },
        title = {
            Text(
                text = "Follow Requests",
                fontWeight = FontWeight.Bold,
                color = Beige
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LoadStateRenderer(
                    loadState = followRequestsState,
                    content = { followRequest ->
                        if (followRequest.isNotEmpty()) {
                            followRequest.forEach { user ->
                                FollowRequestBox(
                                    user = user,
                                    onAcceptFollowRequest = onAcceptFollowRequest,
                                    onRejectFollowRequest = onRejectFollowRequest,
                                    onUserProfileRequest = onUserProfileRequest,
                                    enableButtons = enableButtons
                                )
                            }
                        }
                        else if (followRequestsState is Loaded)
                            Text(
                                text = "No follow requests found",
                                modifier = Modifier.padding(10.dp),
                                color = Color.Gray
                            )
                    }
                )
            }
        },
        containerColor = DarkGreen
    )
}
