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
        val testUser1 = createTestUser(tm)
        val testUser2 = createTestUser(tm, true)
        val testUser3 = createTestUser(tm, true)
        val testUser4 = createTestUser(tm, true)
        val testUser5 = createTestUser(tm)
        val testUser6 = createTestUser(tm, true)
        val testUser7 = createTestUser(tm)
        val testUser8 = createTestUser(tm)
        val testUser9 = createTestUser(tm)
        val testUser10 = createTestUser(tm)
        val testUser11 = createTestUser(tm)
        val testUser12 = createTestUser(tm)

        val testRecipe = createTestRecipe(tm, fs, testAuthor)
        val testRecipe2 = createTestRecipe(tm, fs, testAuthor)

        val today: LocalDate = LocalDate.now()
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
