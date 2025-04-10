package epicurius.repository.jdbi.recipe.models

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.Ingredient
import epicurius.domain.recipe.MealType
import epicurius.domain.recipe.Recipe
import java.util.Date

data class JdbiRecipeModel(
    val id: Int,
    val name: String,
    val authorId: Int,
    val authorUsername: String,
    val date: Date,
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
)

fun JdbiRecipeModel.toRecipe(): Recipe = Recipe(
    id = this.id,
    name = this.name,
    authorId = this.authorId,
    authorUsername = this.authorUsername,
    date = this.date,
    description = null,
    servings = this.servings,
    preparationTime = this.preparationTime,
    cuisine = this.cuisine,
    mealType = this.mealType,
    intolerances = this.intolerances,
    diets = this.diets,
    ingredients = this.ingredients,
    instructions = null,
    calories = this.calories,
    protein = this.protein,
    fat = this.fat,
    carbs = this.carbs,
    picturesNames = this.picturesNames,
)
