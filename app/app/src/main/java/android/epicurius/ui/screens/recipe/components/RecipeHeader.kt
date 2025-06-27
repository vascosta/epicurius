package android.epicurius.ui.screens.recipe.components

import android.epicurius.R
import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.domain.mealPlanner.MealTime
import android.epicurius.ui.screens.collections.recipeCollections.components.RecipeCollectionsDialog
import android.epicurius.ui.screens.collections.recipeCollections.components.RecipeCollectionsStateBundle
import android.epicurius.ui.screens.utils.MixedText
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate

@Composable
fun RecipeHeader(
    collectionId: Int? = null,
    recipeId: Int,
    name: String,
    author: String,
    date: LocalDate? = null,
    mealTime: MealTime? = null,
    recipeCollectionsStateBundle: RecipeCollectionsStateBundle? = null,
    onAddRecipeToCollections: (
        recipeId: Int,
        collectionsToAdd: List<CollectionProfile>
    ) -> Unit = { _, _ -> },
    onRemoveRecipeFromCollections: (
        recipeId: Int,
        collectionsToRemove: List<CollectionProfile>
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
    enableButtons: Boolean
) {
    var showCollectionsDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = name,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            overflow = TextOverflow.Ellipsis,
            maxLines = 3
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 3.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MixedText(boldString = "by ", normalString = author)
            IconButton(
                onClick = {
                    if (date != null && mealTime != null)
                        onAddRecipeToMealPlanner(date, recipeId, mealTime)
                    else
                        if (collectionId != null) onRemoveRecipeFromCollection(collectionId, recipeId)
                        else showCollectionsDialog = true
                },
                modifier = Modifier.size(24.dp),
                enabled = enableButtons
            ) {
                val painter =
                    if (date != null && mealTime != null) {
                        painterResource(R.drawable.calendar)
                    } else {
                        if (collectionId != null) painterResource(R.drawable.star)
                        else painterResource(R.drawable.white_star)
                    }
                Image(
                    painter = painter,
                    contentDescription = "Favorites",
                    modifier = Modifier.size(20.dp),
                    contentScale = ContentScale.Fit
                )
                if (showCollectionsDialog) {
                    RecipeCollectionsDialog(
                        recipeId = recipeId,
                        recipeCollectionsStateBundle = recipeCollectionsStateBundle,
                        onDismissRequest = {
                            showCollectionsDialog = false
                            onRecipeCollectionsClear()
                        },
                        onAddRecipeToCollections = onAddRecipeToCollections,
                        onRemoveRecipeFromCollections = onRemoveRecipeFromCollections,
                        onRecipeCollectionsRequest = onRecipeCollectionsRequest,
                        enableButtons = enableButtons
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun RecipeHeaderPreview() {
    RecipeHeader(
        recipeId = 1,
        name = "Delicious Recipe Name",
        author = "ChefBear",
        date = LocalDate.now(),
        mealTime = MealTime.LUNCH,
        enableButtons = true
    )
    RecipeHeader(
        recipeId = 1,
        name = "Delicious Recipe Name",
        author = "ChefBear",
        enableButtons = true
    )
}
