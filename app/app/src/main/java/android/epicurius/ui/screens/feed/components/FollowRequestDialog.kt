package android.epicurius.ui.screens.feed.components

import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.domain.user.FollowUser
import android.epicurius.domain.user.SearchUser
import android.epicurius.ui.screens.user.components.UserBox
import android.epicurius.ui.screens.utils.Idle
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.LoadStateRenderer
import android.epicurius.ui.screens.utils.Loaded
import androidx.compose.foundation.layout.Arrangement
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
    onDismiss: () -> Unit,
    followRequestsState: LoadState<List<SearchUser>>,
    onAcceptFollowRequest: (name: String) -> Unit,
    onRejectFollowRequest: (name: String) -> Unit,
    onUserProfileRequest: (name: String) -> Unit,
    onFollowRequests: () -> Unit,
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
            { Text("Close") }
        },
        title = { Text(text = "Follow Requests", fontWeight = FontWeight.Bold) },
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
                                    onAcceptFollowRequest = { onAcceptFollowRequest(user.name) },
                                    onRejectFollowRequest = { onRejectFollowRequest(user.name) },
                                    onUserProfileRequest = { onUserProfileRequest(user.name) },
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
        containerColor = Color.White
    )
}
