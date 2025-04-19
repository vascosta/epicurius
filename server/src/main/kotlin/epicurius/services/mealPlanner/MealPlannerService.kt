package epicurius.services.mealPlanner

import epicurius.domain.PictureDomain.Companion.RECIPES_FOLDER
import epicurius.domain.exceptions.MealPlannerAlreadyExists
import epicurius.domain.exceptions.MealPlannerNotFound
import epicurius.domain.exceptions.MealTimeAlreadyExistsInPlanner
import epicurius.domain.exceptions.RecipeDoesNotContainCaloriesInfo
import epicurius.domain.exceptions.RecipeExceedsMaximumCalories
import epicurius.domain.exceptions.RecipeIsInvalidForMealTime
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.domain.mealPlanner.DailyMealPlanner
import epicurius.domain.mealPlanner.MealPlanner
import epicurius.domain.mealPlanner.MealTime
import epicurius.http.mealPlanner.models.input.AddMealPlannerInputModel
import epicurius.repository.cloudStorage.manager.CloudStorageManager
import epicurius.repository.jdbi.mealPlanner.models.JdbiDailyMealPlanner
import epicurius.repository.jdbi.recipe.models.JdbiRecipeModel
import epicurius.repository.transaction.TransactionManager
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class MealPlannerService(
    private val tm: TransactionManager,
    private val cs: CloudStorageManager
) {

    fun createMealPlanner(userId: Int, date: LocalDate) {
        if (checkIfMealPlannerExists(userId, date)) throw MealPlannerAlreadyExists(date)
        tm.run { it.mealPlannerRepository.createMealPlanner(userId, date) }
    }

    fun getMealPlanner(userId: Int): MealPlanner {
        val jdbiPlanner = tm.run { it.mealPlannerRepository.getMealPlanner(userId) }
        val planner = getRecipeInfoPicture(jdbiPlanner.planner)
        return MealPlanner(planner)
    }

    fun addMealPlanner(userId: Int, date: LocalDate, info: AddMealPlannerInputModel): MealPlanner {
        if (!checkIfMealPlannerExists(userId, date)) throw MealPlannerNotFound()
        checkIfMealTimeAlreadyExistsInPlanner(userId, date, info.mealTime)
        val recipe = checkIfRecipeExists(info.recipeId)
        if (!info.mealTime.isMealTypeAllowedForMealTime(recipe.mealType)) throw RecipeIsInvalidForMealTime()
        checkIfLimitOfCaloriesIsRespected(userId, date, recipe)

        val jdbiPlanner = tm.run {
            it.mealPlannerRepository.addMealPlanner(userId, date, info.recipeId, info.mealTime)
        }
        val planner = getRecipeInfoPicture(jdbiPlanner.planner)
        return MealPlanner(planner)
    }

    private fun getRecipeInfoPicture(planner: List<JdbiDailyMealPlanner>) =
        planner.map { daily ->
            val dailyMeals = daily.meals.mapValues { (_, recipe) ->
                val picture = cs.pictureCloudStorageRepository.getPicture(recipe.pictures.first(), RECIPES_FOLDER)
                recipe.toRecipeInfo(picture)
            }
            DailyMealPlanner(date = daily.date, meals = dailyMeals)
        }

    private fun checkIfMealPlannerExists(userId: Int, date: LocalDate): Boolean =
        tm.run { it.mealPlannerRepository.checkIfMealPlannerAlreadyExists(userId, date) }

    private fun checkIfRecipeExists(recipeId: Int): JdbiRecipeModel =
        tm.run { it.recipeRepository.getRecipe(recipeId) } ?: throw RecipeNotFound()

    private fun checkIfMealTimeAlreadyExistsInPlanner(userId: Int, date: LocalDate, mealTime: MealTime) {
        val check = tm.run {
            it.mealPlannerRepository.checkIfMealTimeAlreadyExistsInPlanner(userId, date, mealTime)
        }
        if (check) throw MealTimeAlreadyExistsInPlanner(mealTime)
    }

    private fun checkIfLimitOfCaloriesIsRespected(userId: Int, date: LocalDate, recipe: JdbiRecipeModel) {
        val calories = tm.run { it.mealPlannerRepository.getDailyCalories(userId, date) }
        if (calories.maxCalories != null) {
            if (recipe.calories == null) throw RecipeDoesNotContainCaloriesInfo()
            if (calories.reachedCalories + recipe.calories > calories.maxCalories) throw RecipeExceedsMaximumCalories()
        }
    }
}
