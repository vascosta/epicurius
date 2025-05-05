package epicurius.unit.services.rateRecipe

import epicurius.domain.Intolerance
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.Ingredient
import epicurius.domain.recipe.IngredientUnit
import epicurius.domain.recipe.MealType
import epicurius.repository.jdbi.recipe.models.JdbiRecipeModel
import epicurius.unit.services.ServiceTest
import java.time.LocalDate

open class RateRecipeServiceTest : ServiceTest() {

    companion object {
        const val USER_ID = 1
        const val USERNAME = "testUser"

        const val AUTHOR_ID = 2
        const val AUTHOR_USERNAME = "authorUser"

        const val RECIPE_ID = 1
        val jdbiRecipeModel = JdbiRecipeModel(
            id = RECIPE_ID,
            name = "Test Recipe",
            authorId = AUTHOR_ID,
            authorUsername = AUTHOR_USERNAME,
            date = LocalDate.now(),
            servings = 4,
            preparationTime = 30,
            cuisine = Cuisine.FRENCH,
            mealType = MealType.APPETIZER,
            intolerances = listOf(Intolerance.SESAME),
            diets = emptyList(),
            ingredients = listOf(
                Ingredient(
                    name = "Test Ingredient",
                    quantity = 1.0,
                    unit = IngredientUnit.G
                )
            ),
            picturesNames = emptyList()
        )

        const val RATING_5 = 5
        const val RATING_3 = 3
        const val RATING_1 = 1
    }
}
