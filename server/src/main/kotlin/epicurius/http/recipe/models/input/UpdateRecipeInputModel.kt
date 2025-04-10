package epicurius.http.recipe.models.input

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.Ingredient
import epicurius.domain.recipe.Instructions
import epicurius.domain.recipe.MealType
import epicurius.domain.recipe.RecipeDomain
import epicurius.repository.firestore.recipe.models.FirestoreUpdateRecipeModel
import epicurius.repository.jdbi.recipe.models.JdbiUpdateRecipeModel
import jakarta.validation.constraints.Size

data class UpdateRecipeInputModel(
    @field:Size(max = RecipeDomain.MAX_RECIPE_DESCRIPTION_LENGTH, message = RecipeDomain.RECIPE_DESCRIPTION_LENGTH_MSG)
    val name: String? = null,

    @field:Size(min = RecipeDomain.MIN_RECIPE_NAME_LENGTH, max = RecipeDomain.MAX_RECIPE_NAME_LENGTH, message = RecipeDomain.RECIPE_NAME_LENGTH_MSG)
    val description: String? = null,

    val servings: Int? = null,
    val preparationTime: Int? = null,
    val cuisine: Cuisine? = null,
    val mealType: MealType? = null,
    val intolerances: List<Intolerance>? = null,
    val diets: List<Diet>? = null,
    val ingredients: List<Ingredient>? = null,
    val calories: Int? = null,
    val protein: Int? = null,
    val fat: Int? = null,
    val carbs: Int? = null,
    val instructions: Instructions? = null,
) {
    fun toJdbiUpdateRecipeModel(recipeId: Int) =
        JdbiUpdateRecipeModel(
            recipeId,
            name,
            servings,
            preparationTime,
            cuisine,
            mealType,
            intolerances,
            diets,
            ingredients,
            calories,
            protein,
            fat,
            carbs,
            emptyList()
        )

    fun toFirestoreUpdateRecipeModel(recipeId: Int) = FirestoreUpdateRecipeModel(recipeId, description, instructions)
}
