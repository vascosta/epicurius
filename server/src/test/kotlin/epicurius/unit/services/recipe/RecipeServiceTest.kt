package epicurius.unit.services.recipe

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.Ingredient
import epicurius.domain.recipe.IngredientUnit
import epicurius.domain.recipe.Instructions
import epicurius.domain.recipe.MealType
import epicurius.http.recipe.models.input.CreateRecipeInputModel
import epicurius.http.recipe.models.input.SearchRecipesInputModel
import epicurius.repository.jdbi.recipe.models.JdbiRecipeInfo
import epicurius.repository.jdbi.recipe.models.JdbiRecipeModel
import epicurius.unit.services.ServiceTest
import epicurius.utils.generateRandomUsername

open class RecipeServiceTest : ServiceTest() {

    companion object {
        const val RECIPE_ID = 1
        const val AUTHOR_ID = 1
        const val USER_ID = 6798
        val authorName = generateRandomUsername()

        val recipePictures = listOf(testPicture)
        val recipePicturesNames = recipePictures.map { it.name }

        val createRecipeInfo = CreateRecipeInputModel(
            "Pastel de nata",
            "A delicious Portuguese dessert",
            4,
            30,
            Cuisine.MEDITERRANEAN,
            MealType.DESSERT,
            listOf(Intolerance.EGG, Intolerance.GLUTEN, Intolerance.DAIRY),
            listOf(Diet.OVO_VEGETARIAN, Diet.LACTO_VEGETARIAN),
            listOf(
                Ingredient("Eggs", 4, IngredientUnit.X),
                Ingredient("Sugar", 200, IngredientUnit.G),
                Ingredient("Flour", 100, IngredientUnit.G),
                Ingredient("Milk", 500, IngredientUnit.ML),
                Ingredient("Butter", 50, IngredientUnit.G)
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
        val jdbiCreateRecipeInfo = createRecipeInfo.toJdbiCreateRecipeModel(AUTHOR_ID, recipePicturesNames)
        val firestoreRecipeInfo = createRecipeInfo.toFirestoreRecipeModel(RECIPE_ID)

        val jdbiRecipeModel = JdbiRecipeModel(
            RECIPE_ID,
            createRecipeInfo.name,
            AUTHOR_ID,
            authorName,
            jdbiCreateRecipeInfo.date,
            createRecipeInfo.servings,
            createRecipeInfo.preparationTime,
            createRecipeInfo.cuisine,
            createRecipeInfo.mealType,
            createRecipeInfo.intolerances,
            createRecipeInfo.diets,
            createRecipeInfo.ingredients,
            createRecipeInfo.calories,
            createRecipeInfo.protein,
            createRecipeInfo.fat,
            createRecipeInfo.carbs,
            recipePicturesNames
        )

        fun getSearchRecipesWithoutIngredientsInputModel(): SearchRecipesInputModel {
            return SearchRecipesInputModel(
                name = "Pastel de nata",
                cuisine = Cuisine.MEDITERRANEAN,
                mealType = MealType.DESSERT,
                intolerances = listOf(Intolerance.EGG, Intolerance.GLUTEN),
                diets = listOf(Diet.OVO_VEGETARIAN, Diet.LACTO_VEGETARIAN),
                minCalories = 200,
                maxCalories = 500,
                minCarbs = 20,
                maxCarbs = 50,
                minFat = 10,
                maxFat = 30,
                minProtein = 5,
                maxProtein = 15,
                minTime = 20,
                maxTime = 60
            )
        }

        fun getSearchRecipesWithIngredientsInputModel(): SearchRecipesInputModel {
            return SearchRecipesInputModel(
                name = "Pastel de nata",
                cuisine = Cuisine.MEDITERRANEAN,
                mealType = MealType.DESSERT,
                ingredients = listOf("Eggs", "Sugar"),
                intolerances = listOf(Intolerance.EGG, Intolerance.GLUTEN),
                diets = listOf(Diet.OVO_VEGETARIAN, Diet.LACTO_VEGETARIAN),
                minCalories = 200,
                maxCalories = 500,
                minCarbs = 20,
                maxCarbs = 50,
                minFat = 10,
                maxFat = 30,
                minProtein = 5,
                maxProtein = 15,
                minTime = 20,
                maxTime = 60
            )
        }

        fun getRecipeInfo(): JdbiRecipeInfo = JdbiRecipeInfo(
            id = RECIPE_ID,
            name = "Pastel de nata",
            cuisine = Cuisine.MEDITERRANEAN,
            mealType = MealType.DESSERT,
            preparationTime = 30,
            servings = 4,
            pictures = recipePicturesNames
        )

        // val jdbiSearchRecipesModel = getSearchRecipesModel()
    }
}
