package android.epicurius.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun BottomBarButton(
    onClick: () -> Unit,
    enabled: Boolean,
    imageId: Int,
    description: String,
    imageSize: Int = 36
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(70.dp),
        enabled = enabled
    ) {
        Image(
            painter = painterResource(id = imageId),
            contentDescription = description,
            modifier = Modifier.size(imageSize.dp),
            contentScale = ContentScale.Fit
        )
    }
}