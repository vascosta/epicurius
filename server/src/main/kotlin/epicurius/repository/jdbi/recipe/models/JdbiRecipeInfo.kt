package epicurius.repository.jdbi.recipe.models

import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.MealType
import epicurius.domain.recipe.RecipeInfo

data class JdbiRecipeInfo(
    val id: Int,
    val name: String,
    val cuisine: Cuisine,
    val mealType: MealType,
    val preparationTime: Int,
    val servings: Int,
    val picturesNames: List<String>
) {
    fun toRecipeInfo(picture: ByteArray) =
        RecipeInfo(
            id = id,
            name = name,
            cuisine = cuisine,
            mealType = mealType,
            preparationTime = preparationTime,
            servings = servings,
            picture = picture
        )
}
