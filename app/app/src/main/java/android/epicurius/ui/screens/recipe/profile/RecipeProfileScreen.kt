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
import android.epicurius.ui.screens.collections.recipeCollections.components.RecipeCollectionsStateBundle
import android.epicurius.ui.screens.recipe.confirmIngredients.ConfirmIngredientsContent
import android.epicurius.ui.screens.recipe.preparation.PreparationContent
import android.epicurius.ui.screens.utils.generateTestImageByteArray
import android.epicurius.ui.screens.recipe.profile.components.RecipeProfileContent
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.LoadStateRenderer
import android.epicurius.ui.screens.utils.Loaded
import android.epicurius.ui.screens.utils.apiSuccess
import android.epicurius.ui.screens.utils.getNameFromLoadStateValue
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import epicurius.domain.collection.CollectionType
import java.time.LocalDate
import java.util.Base64

enum class ScreenState {
    Profile, Ingredients, Preparation
}

@Composable
fun RecipeProfileScreen(
    recipeState: LoadState<Recipe>,
    recipeNameState: LoadState<String>,
    usernameState: LoadState<String>,
    userRecipeRatingState: LoadState<Int?>,
    recipeCollectionsStateBundle: RecipeCollectionsStateBundle,
    substituteIngredientsState: LoadState<List<String>>,
    onBackButton: () -> Unit = { },
    onEditRecipe: (
        name: String?,
        description: String?,
        servings: Int?,
        preparationTime: Int?,
        cuisine: Cuisine?,
        mealType: MealType?,
        intolerances: Set<Intolerance>?,
        diets: Set<Diet>?,
        ingredients: List<Ingredient>?,
        calories: Int?,
        protein: Int?,
        fat: Int?,
        carbs: Int?,
        instructions: Instructions?
    ) -> Unit = { _, _, _, _, _, _, _, _, _, _, _, _, _, _ -> },
    onEditRecipePictures: (picturesBytes: List<ByteArray>) -> Unit = {},
    onEditUserRating: (rating: Int) -> Unit = {},
    onDeleteUserRecipeRating: () -> Unit = {},
    onDeleteRecipe: () -> Unit = { },
    onAddRecipeToCollections: (
        recipeId: Int,
        collectionsToAdd: List<CollectionProfile>
    ) -> Unit = { _, _ -> },
    onRemoveRecipeFromCollections: (
        recipeId: Int,
        collectionsToRemove: List<CollectionProfile>
    ) -> Unit = { _, _ -> },
    onSubstituteIngredients: (ingredientName: String) -> Unit = {},
    onRateRecipe: (rating: Int) -> Unit = {},
    onRecipeCollectionsClear: () -> Unit = {},
    onUserProfileRequest: (name: String) -> Unit = {},
    onRecipeCollectionsRequest: (recipeId: Int, collectionType: CollectionType) -> Unit = { _, _ -> },
    enableButtons: Boolean,
) {
    var currentScreen by remember { mutableStateOf(ScreenState.Profile) }
    var recipeName = getNameFromLoadStateValue(recipeNameState)

    Scaffold(
        topBar = {
            TopBar(
                titleText = recipeName,
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
                enableButtons = enableButtons && recipeState is Loaded
            )
        },
        bottomBar = { BottomBar(buttonsEnable = enableButtons && recipeState is Loaded) },
        content = { paddingValues ->
            LoadStateRenderer(
                loadState = recipeState,
                content = { recipe ->
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
                                    usernameState = usernameState,
                                    userRecipeRatingState = userRecipeRatingState,
                                    recipeCollectionsStateBundle = recipeCollectionsStateBundle,
                                    onEditRecipe = onEditRecipe,
                                    onEditRecipePictures = onEditRecipePictures,
                                    onEditUserRating = onEditUserRating,
                                    onDeleteRecipe = onDeleteRecipe,
                                    onMakeRecipe = { currentScreen = ScreenState.Ingredients },
                                    onAddRecipeToCollections = onAddRecipeToCollections,
                                    onRemoveRecipeFromCollections = onRemoveRecipeFromCollections,
                                    onRecipeCollectionsClear = onRecipeCollectionsClear,
                                    onUserProfileRequest = onUserProfileRequest,
                                    onRecipeCollectionsRequest = onRecipeCollectionsRequest,
                                    enableButtons = enableButtons,
                                    paddingValues = paddingValues
                                )
                            ScreenState.Ingredients ->
                                ConfirmIngredientsContent(
                                    recipe = recipe,
                                    substituteIngredientsState = substituteIngredientsState,
                                    onSubstituteIngredients = onSubstituteIngredients,
                                    onConfirmIngredients = { currentScreen = ScreenState.Preparation },
                                    enableButtons = enableButtons,
                                    paddingValues = paddingValues
                                )
                            ScreenState.Preparation ->
                                PreparationContent(
                                    recipe = recipe,
                                    usernameState = usernameState,
                                    userRecipeRatingState = userRecipeRatingState,
                                    onRateRecipe = onRateRecipe,
                                    onFinishPreparation = { currentScreen = ScreenState.Profile },
                                    enableButtons = enableButtons,
                                    paddingValues = paddingValues,
                                )
                        }
                    }
                }
            )
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
        pictures = testImages
    )
    val rating = 4
    RecipeProfileScreen(
        recipeState = apiSuccess(recipe),
        recipeNameState = apiSuccess(recipe.name),
        usernameState = apiSuccess(recipe.authorUsername),
        userRecipeRatingState = apiSuccess(rating),
        recipeCollectionsStateBundle = RecipeCollectionsStateBundle(
            collectionsToAddRecipeState = apiSuccess(emptyList()),
            collectionsToRemoveRecipeState = apiSuccess(emptyList())
        ),
        substituteIngredientsState = apiSuccess(emptyList()),
        enableButtons = true,
    )
}