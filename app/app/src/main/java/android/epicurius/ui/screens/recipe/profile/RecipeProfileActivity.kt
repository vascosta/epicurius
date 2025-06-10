package android.epicurius.ui.screens.recipe.profile

import android.epicurius.R
import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.Ingredient
import android.epicurius.domain.recipe.IngredientUnit
import android.epicurius.domain.recipe.Instructions
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.Recipe
import android.epicurius.ui.EpicuriusActivity
import android.epicurius.ui.EpicuriusViewModel
import android.epicurius.ui.navigation.navigateTo
import android.epicurius.ui.screens.recipe.ingredients.ConfirmIngredientsActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import java.time.LocalDate

class RecipeProfileActivity : EpicuriusActivity() {
    override val viewModel: EpicuriusViewModel by getViewModel<EpicuriusViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecipeProfileScreen(
                recipe = Recipe(
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
                    pictures = listOf(),
                    isInCollection = false
                ),
                rating = 4.0,
                images = listOf(R.drawable.home, R.drawable.star, R.drawable.pencil),
                isAuthor = true,
                collectionId = null,
                collectionsState = null,
                onBackButton = {},
                onEditRecipe = {},
                onEditRecipeImages = {},
                onEditRating = { },
                onMakeIt = { navigateTo<ConfirmIngredientsActivity>() },
                onDeleteRecipe = {},
                onCollectionsRequest = { _, _ -> },
                onAddRecipeToCollection = { _, _ -> },
                onRemoveRecipeFromCollection = { _, _ -> },
                enableButtons = true,
            )
        }
    }
}
