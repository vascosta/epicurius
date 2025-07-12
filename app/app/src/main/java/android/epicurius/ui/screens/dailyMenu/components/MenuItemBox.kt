package android.epicurius.ui.screens.dailyMenu.components

import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.ui.screens.collections.recipeCollections.components.RecipeCollectionsStateBundle
import android.epicurius.ui.screens.recipe.components.RecipeInfoBox
import android.epicurius.ui.screens.theme.DarkPurple
import android.epicurius.ui.screens.utils.apiSuccess
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MenuItemBox(
    title: String,
    recipe: RecipeInfo? = null,
    recipeCollectionsStateBundle: RecipeCollectionsStateBundle,
    onAddRecipeToCollections: (
        recipeId: Int,
        collectionsToAdd: List<CollectionProfile>
    ) -> Unit = { _, _ -> },
    onRemoveRecipeFromCollections: (
        recipeId: Int,
        collectionsToRemove: List<CollectionProfile>
    ) -> Unit = { _, _ -> },
    onClearRecipeCollections: () -> Unit = {},
    onRecipeCollectionsRequest: (recipeId: Int) -> Unit = {},
    onRecipeRequest: (recipeId: Int) -> Unit = {},
    enableButtons: Boolean
) {
    Box(modifier = Modifier.padding(10.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                modifier = Modifier.padding(bottom = 10.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = DarkPurple
            )
            if (recipe != null) {
                RecipeInfoBox(
                    recipeInfo = recipe,
                    recipeCollectionsStateBundle = recipeCollectionsStateBundle,
                    onAddRecipeToCollections = onAddRecipeToCollections,
                    onRemoveRecipeFromCollections = onRemoveRecipeFromCollections,
                    onRecipeCollectionsClear = onClearRecipeCollections,
                    onRecipeRequest = onRecipeRequest,
                    onRecipeCollectionsRequest = onRecipeCollectionsRequest,
                    enableButtons = enableButtons
                )
            } else {
                Text(
                    text = "$title recipe is not available today",
                    modifier = Modifier.padding(10.dp),
                    color = Color.Gray
                )
            }
        }
    }
}

@Preview
@Composable
fun MenuItemBoxPreview() {
    MenuItemBox(
        "Breakfast",
        RecipeInfo(
            id = 1,
            name = "Pancakes",
            authorUsername = "ChefBear",
            rating = 4.3,
            cuisine = Cuisine.AMERICAN,
            mealType = MealType.BREAKFAST,
            preparationTime = 20,
            servings = 2,
            picture = ""
        ),
        RecipeCollectionsStateBundle(apiSuccess(emptyList()), apiSuccess(emptyList())),
        enableButtons = true
    )
}