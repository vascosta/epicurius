package android.epicurius.ui.screens.user.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun UserProfilePicture(
    profilePicture: ByteArray?,
    iconSize: Int,
    isUserProfile: Boolean,
    onClick: () -> Unit,
    onRemoveImage: (ByteArray) -> Unit,
    enabled: Boolean
) {
    var showRemoveIcon by remember { mutableStateOf(false) }
    var canShowRemoveIcon by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .size(iconSize.dp)
            .clip(CircleShape)
            .background(Color.LightGray)
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { if (enabled) onClick() },
                    onTap = { if (canShowRemoveIcon) showRemoveIcon = !showRemoveIcon }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        if (profilePicture == null) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Default Profile",
                tint = Color.White,
                modifier = Modifier.size(64.dp)
            )
        } else {
            val bitmap = BitmapFactory.decodeByteArray(profilePicture, 0, profilePicture.size)
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "User Profile Picture"
            )
            canShowRemoveIcon = true

            if (showRemoveIcon && isUserProfile && enabled) {
                IconButton(
                    onClick = {
                        onRemoveImage(profilePicture)
                        showRemoveIcon = false
                        canShowRemoveIcon = false
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.6f), shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove Image",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun UserProfilePicturePreview() {
    UserProfilePicture(null, 120, true, {}, {}, true)
}
