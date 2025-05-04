package epicurius.unit.services.feed

import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.MealType
import epicurius.domain.recipe.RecipeInfo
import epicurius.repository.jdbi.recipe.models.JdbiRecipeInfo
import epicurius.unit.services.ServiceTest

open class FeedServiceTest : ServiceTest() {

    companion object {
        const val USER_ID = 1

        val jdbiRecipeInfo = JdbiRecipeInfo(
            id = 1,
            name = "Carbonara",
            cuisine = Cuisine.ITALIAN,
            mealType = MealType.MAIN_COURSE,
            preparationTime = 30,
            servings = 4,
            picturesNames = listOf("")
        )

        val recipeInfo = RecipeInfo(
            id = 1,
            name = "Carbonara",
            cuisine = Cuisine.ITALIAN,
            mealType = MealType.MAIN_COURSE,
            preparationTime = 30,
            servings = 4,
            picture = ByteArray(0)
        )

        val jdbiRecipeInfo2 = JdbiRecipeInfo(
            id = 2,
            name = "Spring Rolls",
            cuisine = Cuisine.CHINESE,
            mealType = MealType.APPETIZER,
            preparationTime = 20,
            servings = 2,
            picturesNames = listOf("")
        )

        val recipeInfo2 = RecipeInfo(
            id = 2,
            name = "Spring Rolls",
            cuisine = Cuisine.CHINESE,
            mealType = MealType.APPETIZER,
            preparationTime = 20,
            servings = 2,
            picture = ByteArray(0)
        )
    }
}
