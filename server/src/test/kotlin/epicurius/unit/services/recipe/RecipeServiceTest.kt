package epicurius.unit.services.recipe

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.Ingredient
import epicurius.domain.recipe.IngredientUnit
import epicurius.domain.recipe.Instructions
import epicurius.domain.recipe.MealType
import epicurius.http.recipe.models.input.CreateRecipeInputModel
import epicurius.http.recipe.models.input.UpdateRecipeInputModel
import epicurius.repository.jdbi.recipe.models.JdbiRecipeModel
import epicurius.unit.services.ServiceTest
import epicurius.utils.generateRandomUsername
import java.util.Date

open class RecipeServiceTest: ServiceTest() {

    companion object {
        const val RECIPE_ID = 1
        const val AUTHOR_ID = 1
        val authorName = generateRandomUsername()

        val recipePictures = listOf(testPicture)
        val recipePicturesNames = recipePictures.map { it.name }

        val createRecipeInfo = getCreateRecipeInputModel()
        val jdbiCreateRecipeInfo = createRecipeInfo.toJdbiCreateRecipeModel(AUTHOR_ID, recipePicturesNames)
        val firestoreRecipeInfo = createRecipeInfo.toFirestoreRecipeModel(RECIPE_ID)

        val updateRecipeInfo = getUpdateRecipeInputModel()
        val jdbiUpdateRecipeInfo = updateRecipeInfo.toJdbiUpdateRecipeModel(RECIPE_ID, null)
        val firestoreUpdateRecipeInfo = updateRecipeInfo.toFirestoreUpdateRecipeModel(RECIPE_ID)

        fun getCreateRecipeInputModel() =
            CreateRecipeInputModel(
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


        fun getUpdateRecipeInputModel() =
            UpdateRecipeInputModel(
                "name",
                "description",
                1,
                1,
                Cuisine.ASIAN,
                MealType.SOUP,
                listOf(Intolerance.PEANUT),
                listOf(Diet.KETOGENIC),
                listOf(
                    Ingredient("Ingredient1", 1, IngredientUnit.TSP),
                    Ingredient("Ingredient2", 1, IngredientUnit.TSP)
                ),
                1,
                1,
                1,
                1,
                Instructions(mapOf("1" to "Step1", "2" to "Step2"))
            )

        fun getJdbiRecipeModel(date: Date): JdbiRecipeModel {
            return JdbiRecipeModel(
                RECIPE_ID,
                createRecipeInfo.name,
                AUTHOR_ID,
                authorName,
                date,
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
        }
    }
}