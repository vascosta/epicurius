package android.epicurius.ui.navigation

import android.epicurius.ui.screens.theme.DarkPurple
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun BottomBarButton(
    onClick: () -> Unit,
    enabled: Boolean,
    imageId: Int,
    description: String,
    imageSize: Int = 36,
    isSelected: Boolean
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(70.dp),
        enabled = enabled
    ) {
        Column(
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = imageId),
                contentDescription = description,
                modifier = Modifier.size(imageSize.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(4.dp))
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(DarkPurple)
                )
            } else {
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }
}