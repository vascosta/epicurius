package epicurius.unit.services.mealPlanner

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.mealPlanner.MealTime
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.Ingredient
import epicurius.domain.recipe.IngredientUnit
import epicurius.domain.recipe.Instructions
import epicurius.domain.recipe.MealType
import epicurius.http.controllers.recipe.models.input.CreateRecipeInputModel
import epicurius.repository.jdbi.mealPlanner.models.JdbiDailyMealPlanner
import epicurius.repository.jdbi.mealPlanner.models.JdbiMealPlanner
import epicurius.repository.jdbi.recipe.models.JdbiRecipeInfo
import epicurius.repository.jdbi.recipe.models.JdbiRecipeModel
import epicurius.unit.services.ServiceTest
import java.time.LocalDate

open class MealPlannerServiceTest : ServiceTest() {

    companion object {
        const val USER_ID = 1
        const val USERNAME = "TestUser"
        const val AUTHOR_ID = 2
        const val AUTHOR_USERNAME = "TestAuthor"

        const val CALORIES = 2000

        val mealTime = MealTime.LUNCH
        val today: LocalDate = LocalDate.of(2025, 5, 12)
        val tomorrow: LocalDate = today.plusDays(1)

        const val RECIPE_ID = 1

        private val createRecipeInputInfo = CreateRecipeInputModel(
            "Another Recipe",
            "A different recipe",
            2,
            15,
            Cuisine.ITALIAN,
            MealType.MAIN_COURSE,
            setOf(Intolerance.TREE_NUT),
            setOf(Diet.VEGAN),
            listOf(
                Ingredient("Pasta", 200.0, IngredientUnit.G),
                Ingredient("Tomato Sauce", 100.0, IngredientUnit.G)
            ),
            calories = 500,
            protein = 12,
            fat = 5,
            carbs = 80,
            instructions = Instructions(
                mapOf(
                    "1" to "Boil water.",
                    "2" to "Cook pasta for 10 minutes.",
                    "3" to "Add sauce and serve."
                )
            )
        )

        private val recipePictures = setOf(testPicture)
        private val recipePicturesNames = recipePictures.map { it.name }

        val jdbiRecipeModel = JdbiRecipeModel(
            RECIPE_ID,
            createRecipeInputInfo.name,
            AUTHOR_ID,
            AUTHOR_USERNAME,
            today.minusDays(1),
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

        val jdbiRecipeInfo = JdbiRecipeInfo(
            id = RECIPE_ID,
            name = "Test Recipe",
            servings = 4,
            preparationTime = 30,
            cuisine = Cuisine.FRENCH,
            mealType = MealType.APPETIZER,
            picturesNames = listOf(testPicture.name)
        )

        val jdbiDailyMealPlannerToday = JdbiDailyMealPlanner(
            date = today,
            maxCalories = CALORIES,
            meals = mapOf(
                mealTime to jdbiRecipeInfo
            )
        )

        val jdbiDailyMealPlannerTomorrow = JdbiDailyMealPlanner(
            date = tomorrow,
            maxCalories = CALORIES,
            meals = mapOf(
                mealTime to jdbiRecipeInfo
            )
        )

        val jdbiMealPlanner = JdbiMealPlanner(
            planner = listOf(jdbiDailyMealPlannerTomorrow)
        )
    }
}
