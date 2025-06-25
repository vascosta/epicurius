package android.epicurius.ui.screens.feed.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun FollowRequestIconButton(
    iconResId: Int,
    contentDescription: String,
    onClick: () -> Unit = {},
    enabled: Boolean
) {
    IconButton(
        onClick = { onClick() },
        modifier = Modifier
            .size(24.dp)
            .border(
                width = 1.dp,
                color = Color.Transparent,
                shape = RoundedCornerShape(25.dp)
            ),
        enabled = enabled
    ) {
        Image(
            painter = painterResource(iconResId),
            contentDescription = contentDescription,
            modifier = Modifier.size(24.dp),
        )
    }
}
