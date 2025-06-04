package android.epicurius.ui.screens.recipe.createRecipe.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DividerComponent() {
    HorizontalDivider(
        modifier = Modifier.padding(
            top = 15.dp,
            bottom = 10.dp,
            start = 15.dp,
            end = 15.dp
        ),
        color = Color.Black,
        thickness = 1.dp
    )
}
