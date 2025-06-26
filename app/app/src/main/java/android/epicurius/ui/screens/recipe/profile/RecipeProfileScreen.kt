package android.epicurius.ui.screens.recipe.profile

import android.epicurius.R
import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.Ingredient
import android.epicurius.domain.recipe.IngredientUnit
import android.epicurius.domain.recipe.Instructions
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.Recipe
import android.epicurius.ui.navigation.BottomBar
import android.epicurius.ui.navigation.TopBar
import android.epicurius.ui.screens.recipe.profile.components.ConfirmIngredientsContent
import android.epicurius.ui.screens.recipe.profile.components.PreparationContent
import android.epicurius.ui.screens.recipe.profile.utils.generateTestImageByteArray
import android.epicurius.ui.screens.recipe.profile.components.RecipeProfileContent
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.apiSuccess
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.util.Base64

enum class ScreenState {
    Profile, Ingredients, Preparation
}

@Composable
fun RecipeProfileScreen(
    recipe: Recipe,
    rating: Double,
    images: List<Int>,
    isAuthor: Boolean,
    userRating: Int = 0,
    collectionId: Int?,
    collectionsState: LoadState<List<CollectionProfile>>?,
    onBackButton: () -> Unit,
    onEditRating: (Int) -> Unit,
    onEditRecipe: () -> Unit,
    onEditRecipeImages: (List<ByteArray>) -> Unit,
    onDeleteRecipe: (Int) -> Unit,
    onAddRecipeToCollection: (Int, Int) -> Unit,
    onRemoveRecipeFromCollection: (Int, Int) -> Unit,
    onCollectionsRequest: (Int, Boolean) -> Unit,
    enableButtons: Boolean,
) {
    var currentScreen by remember { mutableStateOf(ScreenState.Profile) }

    Scaffold(
        topBar = {
            TopBar(
                titleText = recipe.name,
                backButton = true,
                onBackButton = when (currentScreen) {
                    ScreenState.Profile -> onBackButton
                    ScreenState.Ingredients -> {
                        { currentScreen = ScreenState.Profile }
                    }
                    ScreenState.Preparation -> {
                        { currentScreen = ScreenState.Ingredients }
                    }
                },
                enableButtons = true
            )
        },
        bottomBar = { BottomBar(buttonsEnable = true) },
        content = { paddingValues ->
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                        slideOutHorizontally { width -> -width } + fadeOut())
                }
            ) { targetState ->
                    when (targetState) {
                        ScreenState.Profile ->
                            RecipeProfileContent(
                                recipe = recipe,
                                rating = rating,
                                images = images,
                                isAuthor = isAuthor,
                                userRating = userRating,
                                collectionId = collectionId,
                                collectionsState = collectionsState,
                                onEditRating = onEditRating,
                                onEditRecipe = onEditRecipe,
                                onEditRecipeImages = onEditRecipeImages,
                                onMakeIt = { currentScreen = ScreenState.Ingredients },
                                onDeleteRecipe = onDeleteRecipe,
                                onAddRecipeToCollection = onAddRecipeToCollection,
                                onRemoveRecipeFromCollection = onRemoveRecipeFromCollection,
                                onCollectionsRequest = onCollectionsRequest,
                                enableButtons = enableButtons,
                                paddingValues = paddingValues
                            )
                        ScreenState.Ingredients ->
                            ConfirmIngredientsContent(
                                ingredientsList = recipe.ingredients,
                                onSubstituteIngredients = { ingredientName ->
                                    listOf("Substitute for $ingredientName")
                                },
                                onConfirmIngredients = {
                                    currentScreen = ScreenState.Preparation
                                },
                                paddingValues = paddingValues
                            )
                        ScreenState.Preparation ->
                            PreparationContent(
                                instructions = recipe.instructions,
                                onRateRecipe = onEditRating,
                                onSkipRating = { currentScreen = ScreenState.Profile },
                                onCancelPreparation = {
                                    currentScreen = ScreenState.Profile
                                },
                                paddingValues = paddingValues,
                            )
                    }

            }
        },
        containerColor = Color.White
    )
}

@Preview
@Composable
fun RecipeProfilePreview(){
    val testImages = listOf(
        Base64.getEncoder().encodeToString(generateTestImageByteArray(R.drawable.test_tomato)),
        Base64.getEncoder().encodeToString(generateTestImageByteArray(R.drawable.test_tomato)),
        Base64.getEncoder().encodeToString(generateTestImageByteArray(R.drawable.test_tomato))
    )

    val recipe = Recipe(
        id = 1,
        name = "Panquecas Americanas",
        authorUsername = "MestreAndre",
        rating = 4.3,
        date = LocalDate.of(2025, 5, 19),
        description = "Deliciosas panquecas fofinhas perfeitas para o pequeno-almoço.",
        servings = 4,
        preparationTime = 20,
        cuisine = Cuisine.AMERICAN,
        mealType = MealType.BREAKFAST,
        intolerances = listOf(Intolerance.GLUTEN),
        diets = listOf(Diet.VEGETARIAN),
        ingredients = listOf(
            Ingredient("Farinha de trigo", 200.0, IngredientUnit.G),
            Ingredient("Leite", 300.0, IngredientUnit.ML),
            Ingredient("Ovo", 2.0, IngredientUnit.X),
            Ingredient("Açúcar", 50.0, IngredientUnit.G),
            Ingredient("Fermento em pó", 10.0, IngredientUnit.G),
            Ingredient("Sal", 1.0, IngredientUnit.TSP),
            Ingredient("Manteiga", 30.0, IngredientUnit.G)
        ),
        calories = 350,
        protein = 8,
        fat = 10,
        carbs = 55,
        instructions = Instructions(
            steps = mapOf(
                "1" to "Numa taça, mistura a farinha, o açúcar, o fermento e o sal.",
                "2" to "Adiciona o leite, os ovos e a manteiga derretida. Mistura até ficar homogéneo.",
                "3" to "Aquece uma frigideira antiaderente e coloca uma concha da massa.",
                "4" to "Cozinha até formar bolhas na superfície e vira a panqueca. Cozinha o outro lado.",
                "5" to "Serve quente com xarope de ácer ou frutas."
            )
        ),
        pictures = testImages,
        isInCollection = true
    )
    val rating = 4.0
    val collections = listOf(
        CollectionProfile(id = 1, name = "Favorites"),
        CollectionProfile(id = 2, name = "Breakfast Recipes")
    )
    RecipeProfileScreen(
        recipe = recipe,
        rating = rating,
        images = listOf(R.drawable.home, R.drawable.star, R.drawable.pencil),
        isAuthor = true,
        userRating = 4,
        collectionId = null,
        collectionsState = apiSuccess(collections),
        {},
        {},
        {},
        {},
        {},
        { _, _ -> },
        { _, _ -> },
        { _, _ -> },
        enableButtons = true,
    )
}