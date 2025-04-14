package epicurius.unit.services.recipe

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.Ingredient
import epicurius.domain.recipe.IngredientUnit
import epicurius.domain.recipe.Instructions
import epicurius.domain.recipe.MealType
import epicurius.http.recipe.models.input.CreateRecipeInputModel
import epicurius.repository.jdbi.recipe.models.JdbiRecipeModel
import epicurius.unit.services.ServiceTest
import epicurius.utils.generateRandomUsername

open class RecipeServiceTest: ServiceTest() {

    companion object {
        const val RECIPE_ID = 0
        const val AUTHOR_ID = 1
        val authorName = generateRandomUsername()

        val recipePictures = listOf(testPicture)
        val recipePicturesNames = recipePictures.map { it.name }

        val recipeInfo = getCreatRecipeInputModel()
        val jdbiCreateRecipeModel = recipeInfo.toJdbiRecipeModel(AUTHOR_ID, recipePicturesNames)
        val firestoreRecipeModel = recipeInfo.toFirestoreRecipeModel(RECIPE_ID)

        fun getCreatRecipeInputModel(): CreateRecipeInputModel {
            return CreateRecipeInputModel(
                name = "Pastel de nata",
                description = "A delicious Portuguese dessert",
                servings = 4,
                preparationTime = 30,
                cuisine = Cuisine.MEDITERRANEAN,
                mealType = MealType.DESSERT,
                intolerances = listOf(Intolerance.EGG, Intolerance.GLUTEN, Intolerance.DAIRY),
                diets = listOf(Diet.OVO_VEGETARIAN, Diet.LACTO_VEGETARIAN),
                ingredients = listOf(
                    Ingredient("Eggs", 4, IngredientUnit.X),
                    Ingredient("Sugar", 200, IngredientUnit.G),
                    Ingredient("Flour", 100, IngredientUnit.G),
                    Ingredient("Milk", 500, IngredientUnit.ML),
                    Ingredient("Butter", 50, IngredientUnit.G)
                ),
                instructions = Instructions(
                    mapOf(
                        "1" to "Preheat the oven to 200°C.",
                        "2" to "In a bowl, mix the eggs, sugar, flour, and milk.",
                        "3" to "Pour the mixture into pastry shells.",
                        "4" to "Bake for 20 minutes or until golden brown.",
                        "5" to "Let cool before serving."
                    )
                )
            )
        }

        fun getJdbiRecipeModel(): JdbiRecipeModel {
            return JdbiRecipeModel(
                RECIPE_ID,
                recipeInfo.name,
                AUTHOR_ID,
                authorName,
                jdbiCreateRecipeModel.date,
                recipeInfo.servings,
                recipeInfo.preparationTime,
                recipeInfo.cuisine,
                recipeInfo.mealType,
                recipeInfo.intolerances,
                recipeInfo.diets,
                recipeInfo.ingredients,
                recipeInfo.calories,
                recipeInfo.protein,
                recipeInfo.fat,
                recipeInfo.carbs,
                recipePicturesNames
            )
        }
    }
}