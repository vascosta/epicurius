package epicurius.unit.repository.mealPlanner

import epicurius.domain.mealPlanner.MealTime
import epicurius.repository.jdbi.recipe.models.JdbiRecipeInfo
import epicurius.unit.repository.RepositoryTest
import epicurius.utils.createTestRecipe
import epicurius.utils.createTestUser
import java.time.LocalDate

open class MealPlannerRepositoryTest : RepositoryTest() {

    companion object {

        private val testAuthor = createTestUser(tm)

        val testRecipe = createTestRecipe(tm, fs, testAuthor.user)
        val testRecipe2 = createTestRecipe(tm, fs, testAuthor.user)

        val today: LocalDate = LocalDate.of(2025, 5, 12)
        val tomorrow: LocalDate = today.plusDays(1)

        val jdbiRecipeInfo = JdbiRecipeInfo(
            id = testRecipe.id,
            name = testRecipe.name,
            cuisine = testRecipe.cuisine,
            mealType = testRecipe.mealType,
            preparationTime = testRecipe.preparationTime,
            servings = testRecipe.servings,
            picturesNames = emptyList()
        )

        fun getWeeklyMealPlanner(userId: Int) =
            tm.run { it.mealPlannerRepository.getWeeklyMealPlanner(userId) }

        fun getDailyMealPlanner(userId: Int, date: LocalDate) =
            tm.run { it.mealPlannerRepository.getDailyMealPlanner(userId, date) }

        fun createDailyMealPlanner(userId: Int, date: LocalDate, maxCalories: Int? = null) =
            tm.run { it.mealPlannerRepository.createDailyMealPlanner(userId, date, maxCalories) }

        fun addRecipeToMealPlanner(userId: Int, date: LocalDate, recipeId: Int, mealTime: MealTime) =
            tm.run { it.mealPlannerRepository.addRecipeToDailyMealPlanner(userId, date, recipeId, mealTime) }

        fun updateDailyMealPlanner(userId: Int, date: LocalDate, recipeId: Int, mealTime: MealTime) =
            tm.run { it.mealPlannerRepository.updateDailyMealPlanner(userId, date, recipeId, mealTime) }

        fun deleteDailyMealPlanner(userId: Int, date: LocalDate) =
            tm.run { it.mealPlannerRepository.deleteDailyMealPlanner(userId, date) }

        fun removeMealTimeFromDailyMealPlanner(userId: Int, date: LocalDate, mealTime: MealTime) =
            tm.run { it.mealPlannerRepository.removeMealTimeFromDailyMealPlanner(userId, date, mealTime) }

        fun updateDailyCalories(userId: Int, date: LocalDate, calories: Int) =
            tm.run { it.mealPlannerRepository.updateDailyCalories(userId, date, calories) }

        fun checkIfMealTimeAlreadyExistsInPlanner(userId: Int, date: LocalDate, mealTime: MealTime) =
            tm.run { it.mealPlannerRepository.checkIfMealTimeAlreadyExistsInPlanner(userId, date, mealTime) }

        fun checkIfDailyMealPlannerExists(userId: Int, date: LocalDate) =
            tm.run { it.mealPlannerRepository.checkIfDailyMealPlannerExists(userId, date) }
    }
}
