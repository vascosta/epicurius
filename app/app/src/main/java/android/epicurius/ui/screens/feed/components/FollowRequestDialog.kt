package android.epicurius.ui.screens.feed.components

import android.epicurius.domain.user.SearchUser
import android.epicurius.ui.screens.user.components.UserBox
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

@Composable
fun FollowRequestDialog(
    onDismiss: () -> Unit,
    onFollowRequests: () -> List<SearchUser>,
    onAccept: (userId: Int) -> Unit,
    onReject: (userId: Int) -> Unit
) {
    val users = onFollowRequests()

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Follow Requests", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                users.forEach { user ->
                    FollowRequestBox(
                        user = user,
                        onAccept = { onAccept(it) },
                        onReject = { onReject(it) }
                    )
                }
            }
        },
        confirmButton = { TextButton(onClick = { onDismiss() }) { Text("Close") } },
        containerColor = Color.White
    )
}
