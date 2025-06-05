package epicurius.unit.http.feed

import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.MealType
import epicurius.domain.recipe.RecipeInfo
import epicurius.unit.http.HttpTest
import epicurius.utils.generateRandomUsername
import java.util.UUID.randomUUID

open class FeedControllerTest : HttpTest() {

    companion object {
        val token = randomUUID().toString()

        val recipeInfo = RecipeInfo(
            id = 1,
            name = "Carbonara",
            generateRandomUsername(),
            0.0,
            cuisine = Cuisine.ITALIAN,
            mealType = MealType.MAIN_COURSE,
            preparationTime = 30,
            servings = 4,
            picture = ByteArray(0)
        )

        val recipeInfo2 = RecipeInfo(
            id = 2,
            name = "Spring Rolls",
            generateRandomUsername(),
            0.0,
            cuisine = Cuisine.CHINESE,
            mealType = MealType.APPETIZER,
            preparationTime = 20,
            servings = 2,
            picture = ByteArray(0)
        )
    }
}
