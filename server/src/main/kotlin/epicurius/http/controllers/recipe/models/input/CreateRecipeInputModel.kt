package epicurius.http.controllers.recipe.models.input

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.Ingredient
import epicurius.domain.recipe.Instructions
import epicurius.domain.recipe.MealType
import epicurius.domain.recipe.utils
import epicurius.domain.user.UserDomain
import epicurius.repository.firestore.recipe.models.FirestoreRecipeModel
import epicurius.repository.jdbi.recipe.models.JdbiCreateRecipeModel
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size

data class CreateRecipeInputModel(
    @field:Size(min = utils.MIN_RECIPE_NAME_LENGTH, max = utils.MAX_RECIPE_NAME_LENGTH, message = utils.RECIPE_NAME_LENGTH_MSG)
    val name: String,

    @field:Size(min = utils.MIN_RECIPE_DESCRIPTION_LENGTH, max = utils.MAX_RECIPE_DESCRIPTION_LENGTH, message = utils.RECIPE_DESCRIPTION_LENGTH_MSG)
    val description: String,

    @field:Positive(message = UserDomain.POSITIVE_NUMBER_MSG)
    val servings: Int,

    @field:Positive(message = UserDomain.POSITIVE_NUMBER_MSG)
    val preparationTime: Int,
    val cuisine: Cuisine,
    val mealType: MealType,
    val intolerances: Set<Intolerance>,
    val diets: Set<Diet>,
    val ingredients: List<Ingredient>,

    @field:Positive(message = UserDomain.POSITIVE_NUMBER_MSG)
    val calories: Int? = null,

    @field:Positive(message = UserDomain.POSITIVE_NUMBER_MSG)
    val protein: Int? = null,

    @field:Positive(message = UserDomain.POSITIVE_NUMBER_MSG)
    val fat: Int? = null,

    @field:Positive(message = UserDomain.POSITIVE_NUMBER_MSG)
    val carbs: Int? = null,
    val instructions: Instructions
) {

    init {
        if (intolerances.size > UserDomain.MAX_INTOLERANCE_SIZE) {
            throw IllegalArgumentException(UserDomain.MAX_INTOLERANCE_SIZE_MSG)
        }

        if (diets.size > UserDomain.MAX_DIET_SIZE) {
            throw IllegalArgumentException(UserDomain.MAX_DIET_SIZE_MSG)
        }

        if (ingredients.size > utils.MAX_NUMBER_OF_INGREDIENTS) {
            throw IllegalArgumentException(utils.INGREDIENTS_SIZE_MSG)
        }

        if (instructions.steps.size > utils.MAX_NUMBER_OF_INSTRUCTIONS_STEPS) {
            throw IllegalArgumentException(utils.INSTRUCTIONS_STEPS_SIZE_MSG)
        }
    }

    fun toJdbiCreateRecipeModel(authorId: Int, picturesNames: List<String>): JdbiCreateRecipeModel {
        return JdbiCreateRecipeModel(
            name = name,
            authorId = authorId,
            servings = servings,
            preparationTime = preparationTime,
            cuisine = cuisine.ordinal,
            mealType = mealType.ordinal,
            intolerances = intolerances.map { it.ordinal },
            diets = diets.map { it.ordinal },
            ingredients = ingredients,
            calories = calories,
            protein = protein,
            fat = fat,
            carbs = carbs,
            picturesNames = picturesNames
        )
    }

    fun toFirestoreRecipeModel(recipeId: Int) = FirestoreRecipeModel(recipeId, description, instructions)
}
