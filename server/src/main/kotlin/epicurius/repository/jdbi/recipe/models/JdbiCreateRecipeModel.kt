package epicurius.repository.jdbi.recipe.models

import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.Ingredient
import epicurius.domain.recipe.MealType
import java.time.Instant
import java.util.Date

data class JdbiCreateRecipeModel(
    val name: String,
    val authorId: Int,
    val date: Date = Date.from(Instant.now()),
    val servings: Int,
    val preparationTime: Int,
    val cuisine: Int,
    val mealType: Int,
    val intolerances: List<Int>,
    val diets: List<Int>,
    val ingredients: List<Ingredient>,
    val calories: Int? = null,
    val protein: Int? = null,
    val fat: Int? = null,
    val carbs: Int? = null,
    val picturesNames: List<String>,
)
