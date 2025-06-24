package epicurius.repository.jdbi.recipe.models

import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.MealType
import epicurius.domain.recipe.RecipeInfo

data class JdbiRecipeInfo(
    val id: Int,
    val name: String,
    val authorUsername: String,
    val rating: Double,
    val cuisine: Cuisine,
    val mealType: MealType,
    val preparationTime: Int,
    val servings: Int,
    val picturesNames: List<String>
) {
    fun toRecipeInfo(picture: ByteArray): RecipeInfo =
        RecipeInfo(
            id, name, authorUsername, rating, cuisine, mealType, preparationTime, servings, picture
        )
}
