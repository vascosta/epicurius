package android.epicurius.ui.screens.recipe.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PunchClock
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun RecipeDetails(preparationTime: Int, servings: Int, rating: Float) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        InfoItem(icon = Icons.Filled.PunchClock, text = "$preparationTime min")
        InfoItem(icon = Icons.Filled.People, text = "$servings px")
        InfoItem(icon = Icons.Filled.Star, text = "$rating/5")
    }
}
