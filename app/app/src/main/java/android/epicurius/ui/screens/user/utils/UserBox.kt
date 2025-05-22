package android.epicurius.ui.screens.user.utils

import android.epicurius.domain.user.SearchUser
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun UserBox(user: SearchUser) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White)
            .clip(RoundedCornerShape(20.dp))
            .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(20.dp))
    ) {
        Row(
            modifier = Modifier.height(90.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.size(10.dp))
            UserProfilePicture(user.profilePicture, 60)
            Spacer(modifier = Modifier.width(70.dp))
            Text(user.name)
        }
    }
}

@Preview
@Composable
fun UserBoxPreview() {
    val userProfile = SearchUser(
        name = "John Doe",
        profilePicture = null
    )

    UserBox(userProfile)
}