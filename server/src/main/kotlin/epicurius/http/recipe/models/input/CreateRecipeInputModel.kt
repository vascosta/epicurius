package epicurius.http.recipe.models.input

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.Ingredient
import epicurius.domain.recipe.Instructions
import epicurius.domain.recipe.MealType
import epicurius.domain.recipe.RecipeDomain
import epicurius.repository.firestore.recipe.models.FirestoreRecipeModel
import epicurius.repository.jdbi.recipe.models.JdbiCreateRecipeModel
import jakarta.validation.constraints.Size

data class CreateRecipeInputModel(
    @field:Size(min = RecipeDomain.MIN_RECIPE_NAME_LENGTH, max = RecipeDomain.MAX_RECIPE_NAME_LENGTH, message = RecipeDomain.RECIPE_NAME_LENGTH_MSG)
    val name: String,

    @field:Size(max = RecipeDomain.MAX_RECIPE_DESCRIPTION_LENGTH, message = RecipeDomain.RECIPE_DESCRIPTION_LENGTH_MSG)
    val description: String? = null,

    val servings: Int,
    val preparationTime: Int,
    val cuisine: Cuisine,
    val mealType: MealType,
    val intolerances: List<Intolerance>,
    val diets: List<Diet>,
    val ingredients: List<Ingredient>,
    val calories: Int? = null,
    val protein: Int? = null,
    val fat: Int? = null,
    val carbs: Int? = null,
    val instructions: Instructions
) {
    fun toJdbiRecipeModel(authorId: Int, picturesNames: List<String>): JdbiCreateRecipeModel {
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
