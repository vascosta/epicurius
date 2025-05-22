package android.epicurius.ui.screens.user.utils

import android.graphics.BitmapFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun UserProfilePicture(profilePicture: ByteArray?, iconSize: Int) {
    Box(
        modifier = Modifier
            .size(iconSize.dp)
            .clip(CircleShape)
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        if (profilePicture == null) {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Default Profile",
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
            }
        } else {
            val bitmap = BitmapFactory.decodeByteArray(profilePicture, 0, profilePicture.size)
            IconButton(onClick = {}) {
                Icon(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "User Profile Picture",
                )
            }
        }
    }
}

@Preview
@Composable
fun UserProfilePicturePreview() {
    UserProfilePicture(null, 120)
}
