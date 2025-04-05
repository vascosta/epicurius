package epicurius.domain.recipe

import epicurius.domain.Diet
import epicurius.domain.Intolerance

data class RecipePostrgresModel(
    val id: Int,
    val name: String,
    val authorId: Int,
    val servings: Int,
    val preparationTime: Int,
    val cuisine: Cuisine,
    val mealType: MealType,
    val intolerances: List<Intolerance>,
    val diets: List<Diet>,
    val ingredients: List<Ingredient>,
    val calories: Int?,
    val protein: Int?,
    val fat: Int?,
    val carbs: Int?,
    val imagesNames : List<String>,
) {
    fun toRecipe(description: String, instructions: Instructions): Recipe {
        return Recipe(
            id = id,
            name = name,
            authorId = authorId,
            description = description,
            servings = servings,
            preparationTime = preparationTime,
            cuisine = cuisine,
            mealType = mealType,
            intolerances = intolerances,
            diets = diets,
            ingredients = ingredients,
            calories = calories,
            protein = protein,
            fat = fat,
            carbs = carbs,
            instructions = instructions,
            imagesNames = imagesNames
        )
    }
}
