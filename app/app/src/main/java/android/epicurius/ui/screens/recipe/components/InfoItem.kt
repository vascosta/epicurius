package android.epicurius.ui.screens.recipe.components

import android.epicurius.ui.screens.theme.DarkPurple
import android.epicurius.ui.screens.theme.LightGreen
import android.epicurius.ui.screens.theme.Lilac
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InfoItem(
    icon: ImageVector,
    iconColor: Color = LightGreen,
    text: String,
    textColor: Color = Lilac
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(12.dp),
            tint = iconColor
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(text = text, color = textColor, fontSize = 10.sp)
    }
}