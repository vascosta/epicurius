package epicurius.http.recipe.models.input

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.Ingredient
import epicurius.domain.recipe.Instructions
import epicurius.domain.recipe.MealType
import epicurius.domain.recipe.RecipeDomain
import epicurius.domain.user.UserDomain
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
    val intolerances: Set<Intolerance>? = null,
    val diets: Set<Diet>? = null,
    val ingredients: List<Ingredient>? = null,
    val calories: Int? = null,
    val protein: Int? = null,
    val fat: Int? = null,
    val carbs: Int? = null,
    val instructions: Instructions? = null,
) {
    init {
        if (intolerances != null && intolerances.size > UserDomain.MAX_INTOLERANCE_SIZE) {
            throw IllegalArgumentException(UserDomain.MAX_INTOLERANCE_SIZE_MSG)
        }

        if (diets != null && diets.size > UserDomain.MAX_DIET_SIZE) {
            throw IllegalArgumentException(UserDomain.MAX_DIET_SIZE_MSG)
        }
    }

    fun toJdbiUpdateRecipeModel(recipeId: Int, pictureNames: List<String>?) =
        JdbiUpdateRecipeModel(
            recipeId,
            name,
            servings,
            preparationTime,
            cuisine?.ordinal,
            mealType?.ordinal,
            intolerances?.map { it.ordinal }?.toSet(),
            diets?.map { it.ordinal }?.toSet(),
            ingredients,
            calories,
            protein,
            fat,
            carbs,
            pictureNames
        )

    fun toFirestoreUpdateRecipeModel(recipeId: Int) = FirestoreUpdateRecipeModel(recipeId, description, instructions)
}
