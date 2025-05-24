package epicurius.http.controllers.recipe.models.input

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.INGREDIENTS_SIZE_MSG
import epicurius.domain.recipe.INSTRUCTIONS_STEPS_SIZE_MSG
import epicurius.domain.recipe.Ingredient
import epicurius.domain.recipe.Instructions
import epicurius.domain.recipe.MAX_NUMBER_OF_INGREDIENTS
import epicurius.domain.recipe.MAX_NUMBER_OF_INSTRUCTIONS_STEPS
import epicurius.domain.recipe.MAX_RECIPE_DESCRIPTION_LENGTH
import epicurius.domain.recipe.MAX_RECIPE_NAME_LENGTH
import epicurius.domain.recipe.MIN_RECIPE_NAME_LENGTH
import epicurius.domain.recipe.MealType
import epicurius.domain.recipe.RECIPE_DESCRIPTION_LENGTH_MSG
import epicurius.domain.recipe.RECIPE_NAME_LENGTH_MSG
import epicurius.domain.user.UserDomain
import epicurius.repository.firestore.recipe.models.FirestoreUpdateRecipeModel
import epicurius.repository.jdbi.recipe.models.JdbiUpdateRecipeModel
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size

data class UpdateRecipeInputModel(
    @field:Size(max = MAX_RECIPE_DESCRIPTION_LENGTH, message = RECIPE_DESCRIPTION_LENGTH_MSG)
    val name: String? = null,

    @field:Size(min = MIN_RECIPE_NAME_LENGTH, max = MAX_RECIPE_NAME_LENGTH, message = RECIPE_NAME_LENGTH_MSG)
    val description: String? = null,

    @field:Positive(message = UserDomain.POSITIVE_NUMBER_MSG)
    val servings: Int? = null,

    @field:Positive(message = UserDomain.POSITIVE_NUMBER_MSG)
    val preparationTime: Int? = null,
    val cuisine: Cuisine? = null,
    val mealType: MealType? = null,
    val intolerances: Set<Intolerance>? = null,
    val diets: Set<Diet>? = null,
    val ingredients: List<Ingredient>? = null,

    @field:Positive(message = UserDomain.POSITIVE_NUMBER_MSG)
    val calories: Int? = null,

    @field:Positive(message = UserDomain.POSITIVE_NUMBER_MSG)
    val protein: Int? = null,

    @field:Positive(message = UserDomain.POSITIVE_NUMBER_MSG)
    val fat: Int? = null,

    @field:Positive(message = UserDomain.POSITIVE_NUMBER_MSG)
    val carbs: Int? = null,
    val instructions: Instructions? = null,
) {
    init {
        if (ingredients != null && ingredients.size > MAX_NUMBER_OF_INGREDIENTS) {
            throw IllegalArgumentException(INGREDIENTS_SIZE_MSG)
        }

        if (intolerances != null && intolerances.size > UserDomain.MAX_INTOLERANCE_SIZE) {
            throw IllegalArgumentException(UserDomain.MAX_INTOLERANCE_SIZE_MSG)
        }

        if (diets != null && diets.size > UserDomain.MAX_DIET_SIZE) {
            throw IllegalArgumentException(UserDomain.MAX_DIET_SIZE_MSG)
        }

        if (instructions != null && instructions.steps.size > MAX_NUMBER_OF_INSTRUCTIONS_STEPS) {
            throw IllegalArgumentException(INSTRUCTIONS_STEPS_SIZE_MSG)
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
