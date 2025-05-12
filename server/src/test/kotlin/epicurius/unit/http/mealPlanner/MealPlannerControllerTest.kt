package epicurius.unit.http.mealPlanner

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.mealPlanner.DailyMealPlanner
import epicurius.domain.mealPlanner.MealPlanner
import epicurius.domain.mealPlanner.MealTime
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.MealType
import epicurius.domain.recipe.RecipeInfo
import epicurius.domain.user.AuthenticatedUser
import epicurius.domain.user.User
import epicurius.http.controllers.mealPlanner.models.input.AddMealPlannerInputModel
import epicurius.http.controllers.mealPlanner.models.input.UpdateDailyCaloriesInputModel
import epicurius.http.controllers.mealPlanner.models.input.UpdateMealPlannerInputModel
import epicurius.http.controllers.mealPlanner.models.output.MealPlannerOutputModel
import epicurius.unit.http.HttpTest
import epicurius.utils.generateEmail
import epicurius.utils.generateRandomUsername
import java.time.LocalDate
import java.util.UUID.randomUUID

open class MealPlannerControllerTest : HttpTest() {

    companion object {
        private val authenticatedUsername = generateRandomUsername()
        private val token = randomUUID().toString()

        val testAuthenticatedUser = AuthenticatedUser(
            User(
                1,
                authenticatedUsername,
                generateEmail(authenticatedUsername),
                userDomain.encodePassword(randomUUID().toString()),
                userDomain.hashToken(token),
                "PT",
                false,
                listOf(Intolerance.GLUTEN),
                listOf(Diet.GLUTEN_FREE),
                randomUUID().toString()
            ),
            token,
        )

        val testAuthorUser = AuthenticatedUser(
            User(
                2,
                "author",
                generateEmail("author"),
                userDomain.encodePassword(randomUUID().toString()),
                userDomain.hashToken(token),
                "PT",
                false,
                listOf(Intolerance.GLUTEN),
                listOf(Diet.GLUTEN_FREE),
                randomUUID().toString()
            ),
            token,
        )

        const val CALORIES = 2000
        val today: LocalDate = LocalDate.now()
        val tomorrow: LocalDate = today.plusDays(1)
        val mealTime = MealTime.LUNCH

        val recipePictures = listOf(testPicture, testPicture2, testTomatoPicture)

        val recipeInfo1 = RecipeInfo(
            id = 1,
            name = "Recipe 1",
            cuisine = Cuisine.ITALIAN,
            mealType = MealType.MAIN_COURSE,
            preparationTime = 40,
            servings = 4,
            picture = recipePictures.first().bytes
        )

        val recipeInfo2 = RecipeInfo(
            id = 2,
            name = "Recipe 2",
            cuisine = Cuisine.THAI,
            mealType = MealType.SIDE_DISH,
            preparationTime = 40,
            servings = 4,
            picture = recipePictures.first().bytes
        )

        val dailyMealPlannerToday = DailyMealPlanner(
            date = today,
            maxCalories = 2000,
            meals = mapOf(
                mealTime to recipeInfo1
            )
        )

        val dailyMealPlannerTomorrow = DailyMealPlanner(
            date = tomorrow,
            maxCalories = 2000,
            meals = mapOf(
                mealTime to recipeInfo2
            )
        )

        val mealPlanner = MealPlanner(
            planner = listOf(
                dailyMealPlannerToday,
                dailyMealPlannerTomorrow
            )
        )

        val mealPlannerOutputModel = MealPlannerOutputModel(mealPlanner.planner)

        val addRecipeToDailyMealPlannerInputModel = AddMealPlannerInputModel(
            recipeId = 1,
            mealTime = MealTime.LUNCH
        )

        val updateDailyMealPlannerInputModel = UpdateMealPlannerInputModel(
            recipeId = 1,
            mealTime = MealTime.DINNER
        )

        val updateDailyCaloriesInputModel = UpdateDailyCaloriesInputModel(
            maxCalories = 3000
        )
    }
}