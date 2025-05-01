package epicurius.repository.jdbi.recipe.models

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.Ingredient
import epicurius.domain.recipe.Instructions
import epicurius.domain.recipe.MealType
import epicurius.domain.recipe.Recipe
import epicurius.domain.recipe.RecipeInfo
import java.time.LocalDate

data class JdbiRecipeModel(
    val id: Int,
    val name: String,
    val authorId: Int,
    val authorUsername: String,
    val date: LocalDate,
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
    val picturesNames: List<String>,
) {
    fun toRecipe(description: String, instructions: Instructions, pictures: List<ByteArray>): Recipe = Recipe(
        id = id,
        name = name,
        authorUsername = authorUsername,
        date = date,
        description = description,
        servings = servings,
        preparationTime = preparationTime,
        cuisine = cuisine,
        mealType = mealType,
        intolerances = intolerances,
        diets = diets,
        ingredients = ingredients,
        instructions = instructions,
        calories = calories,
        protein = protein,
        fat = fat,
        carbs = carbs,
        pictures = pictures,
    )

    fun toRecipeInfo(picture: ByteArray): RecipeInfo =
        RecipeInfo(id, name, cuisine, mealType, preparationTime, servings, picture)
}
