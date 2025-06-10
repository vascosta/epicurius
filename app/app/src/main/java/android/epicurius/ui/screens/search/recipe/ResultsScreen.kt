package android.epicurius.ui.screens.search.recipe

import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.ui.navigation.BottomBar
import android.epicurius.ui.navigation.TopBar
import android.epicurius.ui.screens.recipe.components.RecipeInfoBox
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ResultsScreen(
    recipeList: List<RecipeInfo>,
    onBackButton: () -> Unit = {},
    onRecipeRequest: (Int) -> Unit = {}
) {
    Scaffold(
        topBar = { TopBar("Search Recipes", backButton = true, onBackButton = onBackButton, enableButtons = true) },
        bottomBar = { BottomBar(buttonsEnable = true) },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                recipeList.forEach { recipe ->
                    RecipeInfoBox(
                        collectionId = null,
                        recipeInfo = recipe,
                        collectionsStateBundle = TODO(),
                        onAddRecipeToCollections = {_, _, _, _ ->},
                        onRemoveRecipeFromCollections = {_, _, _, _ ->},
                        onRemoveRecipeFromCollection = {_, _ ->},
                        onRecipeRequest = { onRecipeRequest(recipe.id) },
                        onCollectionsRequest = {},
                        onCollectionsClear = {},
                        enableButtons = TODO(),
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                }
            }
        },
        containerColor = Color.White
    )
}

@Preview
@Composable
fun ResultsScreenPreview() {
    val dummyRecipes = List(10) { index ->
        RecipeInfo(
            id = index,
            name = "Recipe $index",
            authorUsername = "Author $index",
            rating = 3.5,
            cuisine = Cuisine.MEDITERRANEAN,
            mealType = MealType.MAIN_COURSE,
            preparationTime = 30,
            servings = 4,
            picture = "",
            isInCollection = true
        )
    }
    ResultsScreen(dummyRecipes)
}
