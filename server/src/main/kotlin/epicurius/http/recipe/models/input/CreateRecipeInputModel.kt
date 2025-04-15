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
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size

data class CreateRecipeInputModel(
    @field:Size(min = RecipeDomain.MIN_RECIPE_NAME_LENGTH, max = RecipeDomain.MAX_RECIPE_NAME_LENGTH, message = RecipeDomain.RECIPE_NAME_LENGTH_MSG)
    val name: String,

    @field:Size(min = RecipeDomain.MIN_RECIPE_DESCRIPTION_LENGTH, max = RecipeDomain.MAX_RECIPE_DESCRIPTION_LENGTH, message = RecipeDomain.RECIPE_DESCRIPTION_LENGTH_MSG)
    val description: String,

    val servings: Int,
    val preparationTime: Int,
    val cuisine: Cuisine,
    val mealType: MealType,
    val intolerances: List<Intolerance>,
    val diets: List<Diet>,
    val ingredients: List<Ingredient>,

    @field:Positive(message = RecipeDomain.CALORIES_MSG)
    val calories: Int? = null,

    @field:Positive(message = RecipeDomain.PROTEIN_MSG)
    val protein: Int? = null,

    @field:Positive(message = RecipeDomain.FAT_MSG)
    val fat: Int? = null,

    @field:Positive(message = RecipeDomain.CARBS_MSG)
    val carbs: Int? = null,
    val instructions: Instructions
) {

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
