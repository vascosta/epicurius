package epicurius.unit.services.recipe

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.Ingredient
import epicurius.domain.recipe.IngredientUnit
import epicurius.domain.recipe.Instructions
import epicurius.domain.recipe.MealType
import epicurius.domain.user.User
import epicurius.http.recipe.models.input.CreateRecipeInputModel
import epicurius.repository.jdbi.recipe.models.JdbiRecipeModel
import epicurius.unit.services.ServiceTest
import epicurius.utils.generateEmail
import epicurius.utils.generateRandomUsername

open class RecipeServiceTest : ServiceTest() {

    companion object {
        const val RECIPE_ID = 1
        const val AUTHOR_ID = 1
        const val USER_ID = 6798

        val authorName = generateRandomUsername()
        val author = User(
            AUTHOR_ID,
            authorName,
            generateEmail(authorName),
            "",
            "",
            "PT",
            true,
            emptyList(),
            emptyList(),
            ""
        )

        val recipePictures = setOf(testPicture)
        val recipePicturesNames = recipePictures.map { it.name }

        val createRecipeInputInfo = CreateRecipeInputModel(
            "Pastel de nata",
            "A delicious Portuguese dessert",
            4,
            30,
            Cuisine.MEDITERRANEAN,
            MealType.DESSERT,
            setOf(Intolerance.EGG, Intolerance.GLUTEN, Intolerance.DAIRY),
            setOf(Diet.OVO_VEGETARIAN, Diet.LACTO_VEGETARIAN),
            listOf(
                Ingredient("Eggs", 4.0, IngredientUnit.X),
                Ingredient("Sugar", 200.0, IngredientUnit.G),
                Ingredient("Flour", 100.0, IngredientUnit.G),
                Ingredient("Milk", 500.0, IngredientUnit.ML),
                Ingredient("Butter", 50.0, IngredientUnit.G)
            ),
            calories = 300,
            protein = 8,
            fat = 10,
            carbs = 40,
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
        val jdbiCreateRecipeInfo = createRecipeInputInfo.toJdbiCreateRecipeModel(AUTHOR_ID, recipePicturesNames)
        val firestoreRecipeInfo = createRecipeInputInfo.toFirestoreRecipeModel(RECIPE_ID)

        val jdbiRecipeModel = JdbiRecipeModel(
            RECIPE_ID,
            createRecipeInputInfo.name,
            AUTHOR_ID,
            authorName,
            jdbiCreateRecipeInfo.date,
            createRecipeInputInfo.servings,
            createRecipeInputInfo.preparationTime,
            createRecipeInputInfo.cuisine,
            createRecipeInputInfo.mealType,
            createRecipeInputInfo.intolerances.toList(),
            createRecipeInputInfo.diets.toList(),
            createRecipeInputInfo.ingredients,
            createRecipeInputInfo.calories,
            createRecipeInputInfo.protein,
            createRecipeInputInfo.fat,
            createRecipeInputInfo.carbs,
            recipePicturesNames
        )
    }
}
