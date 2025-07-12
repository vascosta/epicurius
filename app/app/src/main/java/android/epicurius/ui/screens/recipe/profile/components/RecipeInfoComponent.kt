package android.epicurius.ui.screens.recipe.profile.components

import android.epicurius.domain.recipe.Recipe
import android.epicurius.ui.screens.recipe.utils.formattedQuantity
import android.epicurius.ui.screens.utils.MixedText
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun RecipeInfoComponent(
    isAuthor: Boolean,
    recipe: Recipe,
    onEditRecipe: () -> Unit = {},
    enableButtons: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 10.dp, end = 10.dp)
            .border(1.dp, Color.Black, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.CenterStart
    ) {
        if (isAuthor) {
            Button(
                onClick = { onEditRecipe() },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 5.dp, end = 10.dp),
                enabled = enableButtons
            ) { Text("Edit") }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            val ingredients = recipe.ingredients.joinToString("\n") {
                val formattedQuantity = formattedQuantity(it.quantity)
                val formattedUnit = it.unit.displayName
                "$formattedQuantity$formattedUnit ${it.name}"
            }
            val instructions = recipe.instructions.steps.entries.joinToString("\n") { "${it.key}: ${it.value}" }

            MixedText("Servings: ", "${recipe.servings} px")
            MixedText("Preparation Time: ", "${recipe.preparationTime} min")
            MixedText("Meal Type: ", recipe.mealType.displayName)
            MixedText("Cuisine: ", recipe.cuisine.displayName)
            MixedText("Intolerances: ", recipe.intolerances.joinToString(", ") { it.displayName })
            MixedText("Diets: ", recipe.diets.joinToString(", ") { it.displayName })
            MixedText("Calories: ", recipe.calories?.toString() ?: "N/A")
            MixedText("Protein: ", recipe.protein?.toString() ?: "N/A")
            MixedText("Fat: ", recipe.fat?.toString() ?: "N/A")
            MixedText("Carbs: ", recipe.carbs?.toString() ?: "N/A")
            Text("Ingredients:", fontWeight = FontWeight.Bold)
            Text(text = ingredients, modifier = Modifier.padding(start = 10.dp))
            Text("Instructions:", fontWeight = FontWeight.Bold)
            Text(text = instructions, modifier = Modifier.padding(start = 10.dp))
        }
    }
}