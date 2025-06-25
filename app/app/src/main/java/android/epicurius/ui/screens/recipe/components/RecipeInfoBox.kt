package android.epicurius.ui.screens.recipe.components

import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.domain.mealPlanner.MealTime
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.ui.screens.collections.recipeCollections.components.RecipeCollectionsStateBundle
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@Composable
fun RecipeInfoBox(
    collectionId: Int? = null,
    recipeInfo: RecipeInfo,
    date: LocalDate? = null,
    mealTime: MealTime? = null,
    recipeCollectionsStateBundle: RecipeCollectionsStateBundle? = null,
    onAddRecipeToCollections: (
        recipeId: Int,
        collectionsToAdd: List<CollectionProfile>
    ) -> Unit = { _, _ -> },
    onRemoveRecipeFromCollections: (
        recipeId: Int,
        collectionsToRemove: List<CollectionProfile>,
    ) -> Unit = { _, _ -> },
    onRemoveRecipeFromCollection: (
        collectionId: Int,
        recipeId: Int
    ) -> Unit = { _, _ -> },
    onAddRecipeToMealPlanner: (
        date: LocalDate,
        recipeId: Int,
        mealTime: MealTime
    ) -> Unit = { _, _, _ -> },
    onRecipeCollectionsClear: () -> Unit = {},
    onRecipeCollectionsRequest: (recipeId: Int) -> Unit = {},
    onRecipeRequest: (recipeId: Int) -> Unit = {},
    enableButtons: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .clip(RoundedCornerShape(20.dp))
            .border(0.5.dp, Color.Black, RoundedCornerShape(20.dp))
            .padding(5.dp)
            .clickable(
                enabled = enableButtons,
                onClick = { onRecipeRequest(recipeInfo.id) }
            )
    ) {
        Column {
            RecipeHeader(
                collectionId = collectionId,
                recipeId = recipeInfo.id,
                name = recipeInfo.name,
                author = recipeInfo.authorUsername,
                date = date,
                mealTime = mealTime,
                recipeCollectionsStateBundle = recipeCollectionsStateBundle,
                onAddRecipeToCollections = onAddRecipeToCollections,
                onRemoveRecipeFromCollections = onRemoveRecipeFromCollections,
                onRemoveRecipeFromCollection = onRemoveRecipeFromCollection,
                onAddRecipeToMealPlanner = onAddRecipeToMealPlanner,
                onRecipeCollectionsRequest = onRecipeCollectionsRequest,
                onRecipeCollectionsClear = onRecipeCollectionsClear,
                enableButtons = enableButtons
            )
            RecipeImage(recipeInfo.pictureBytes)
            RecipeDetails(
                preparationTime = recipeInfo.preparationTime,
                servings = recipeInfo.servings,
                rating = recipeInfo.rating
            )
        }
    }
}

@Preview
@Composable
fun RecipeInfoPreview() {
    RecipeInfoBox(
        collectionId = null,
        recipeInfo = RecipeInfo(
            id = 1,
            name = "Recipe Name",
            authorUsername = "ChefBear",
            rating = 4.3,
            cuisine = Cuisine.MEDITERRANEAN,
            mealType = MealType.SIDE_DISH,
            preparationTime = 30,
            servings = 4,
            picture = ""
        ),
        date = LocalDate.now(),
        mealTime = MealTime.BREAKFAST,
        null,
        enableButtons = true
    )
}
