package android.epicurius.ui.screens.dailyMenu.components

import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.ui.screens.collections.list.components.CollectionsStateBundle
import android.epicurius.ui.screens.recipe.components.RecipeInfoBox
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
    recipe: RecipeInfo?,
    collectionsStateBundle: CollectionsStateBundle,
    onAddRecipeToCollections: (
        collectionsAvailableToAdd: List<CollectionProfile>,
        collectionsAvailableToRemove: List<CollectionProfile>,
        collectionsToAdd: List<CollectionProfile>,
        recipeId: Int
    ) -> Unit,
    onRemoveRecipeFromCollections: (
        collectionsAvailableToAdd: List<CollectionProfile>,
        collectionsAvailableToRemove: List<CollectionProfile>,
        collectionsToRemove: List<CollectionProfile>,
        recipeId: Int
    ) -> Unit,
    onRecipeRequest: (recipeId: Int) -> Unit,
    onCollectionsRequest: (recipeId: Int) -> Unit,
    onCollectionsClear: () -> Unit,
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
                color = Color.Black
            )
            if (recipe != null) {
                RecipeInfoBox(
                    collectionId = null,
                    recipeInfo = recipe,
                    collectionsStateBundle = collectionsStateBundle,
                    onAddRecipeToCollections = onAddRecipeToCollections,
                    onRemoveRecipeFromCollections = onRemoveRecipeFromCollections,
                    onRecipeRequest = onRecipeRequest,
                    onCollectionsRequest = onCollectionsRequest,
                    onRemoveRecipeFromCollection = {_, _ ->},
                    onCollectionsClear = onCollectionsClear,
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
            picture = "",
            isInCollection = true
        ),
        CollectionsStateBundle(apiSuccess(emptyList()), apiSuccess(emptyList())),
        {_, _, _, _ ->},
        {_, _, _, _ ->},
        {},
        {},
        {},
        true
    )
}