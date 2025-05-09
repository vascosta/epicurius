package epicurius.services.mealPlanner

import epicurius.domain.exceptions.MealPlannerAlreadyExists
import epicurius.domain.exceptions.DailyMealPlannerNotFound
import epicurius.domain.exceptions.MealTimeAlreadyExistsInPlanner
import epicurius.domain.exceptions.MealTimeDoesNotExist
import epicurius.domain.exceptions.RecipeIsInvalidForMealTime
import epicurius.domain.exceptions.RecipeNotAccessible
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.domain.mealPlanner.DailyMealPlanner
import epicurius.domain.mealPlanner.MealPlanner
import epicurius.domain.mealPlanner.MealTime
import epicurius.domain.picture.PictureDomain.Companion.RECIPES_FOLDER
import epicurius.http.controllers.mealPlanner.models.input.AddMealPlannerInputModel
import epicurius.http.controllers.mealPlanner.models.input.UpdateMealPlannerInputModel
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

    fun createDailyMealPlanner(userId: Int, date: LocalDate, maxCalories: Int?) {
        if (checkIfDailyMealPlannerExists(userId, date) != null) throw MealPlannerAlreadyExists(date)
        tm.run { it.mealPlannerRepository.createDailyMealPlanner(userId, date, maxCalories) }
    }

    fun getWeeklyMealPlanner(userId: Int): MealPlanner {
        val jdbiPlanner = tm.run { it.mealPlannerRepository.getWeeklyMealPlanner(userId) }
        val planner = getRecipeInfoPicture(jdbiPlanner.planner)
        return MealPlanner(planner)
    }

    fun getDailyMealPlanner(userId: Int, date: LocalDate): DailyMealPlanner {
        val jdbiDailyPlanner = checkIfDailyMealPlannerExists(userId, date) ?: throw DailyMealPlannerNotFound()

        val dailyMeals = jdbiDailyPlanner.meals.mapValues { (_, recipe) ->
            val picture = cs.pictureRepository.getPicture(recipe.picturesNames.first(), RECIPES_FOLDER)
            recipe.toRecipeInfo(picture)
        }

        return DailyMealPlanner(
            date = jdbiDailyPlanner.date,
            maxCalories = jdbiDailyPlanner.maxCalories,
            meals = dailyMeals
        )
    }

    fun addDailyMealPlanner(userId: Int, username: String, date: LocalDate, info: AddMealPlannerInputModel): DailyMealPlanner {
        if (checkIfDailyMealPlannerExists(userId, date) == null) throw DailyMealPlannerNotFound()
        if (checkIfMealTimeAlreadyExistsInPlanner(userId, date, info.mealTime))
            throw MealTimeAlreadyExistsInPlanner(info.mealTime)
        val recipe = checkIfRecipeExists(info.recipeId)
        checkRecipeAccessibility(recipe.authorUsername, username)
        if (!info.mealTime.isMealTypeAllowedForMealTime(recipe.mealType)) throw RecipeIsInvalidForMealTime()

        val jdbiPlanner = tm.run {
            it.mealPlannerRepository.addDailyMealPlanner(userId, date, info.recipeId, info.mealTime)
        }
        val planner = getRecipeInfoPicture(listOf(jdbiPlanner))
        return DailyMealPlanner(date, jdbiPlanner.maxCalories, planner.first().meals)
    }

    fun updateDailyMealPlanner(
        userId: Int,
        username: String,
        date: LocalDate,
        info: UpdateMealPlannerInputModel
    ): DailyMealPlanner {
        if (checkIfDailyMealPlannerExists(userId, date) == null) throw DailyMealPlannerNotFound()
        if (!checkIfMealTimeAlreadyExistsInPlanner(userId, date, info.mealTime))
            throw MealTimeDoesNotExist()
        val recipe = checkIfRecipeExists(info.recipeId)
        checkRecipeAccessibility(recipe.authorUsername, username)
        if (!info.mealTime.isMealTypeAllowedForMealTime(recipe.mealType)) throw RecipeIsInvalidForMealTime()

        val jdbiDailyPlanner = tm.run {
            it.mealPlannerRepository.updateDailyMealPlanner(userId, date, info.recipeId, info.mealTime)
        }
        val meals = jdbiDailyPlanner.meals.mapValues { (_, recipe) ->
            val picture = cs.pictureRepository.getPicture(recipe.picturesNames.first(), RECIPES_FOLDER)
            recipe.toRecipeInfo(picture)
        }
        return DailyMealPlanner(date, jdbiDailyPlanner.maxCalories, meals)
    }

    fun updateDailyCalories(userId: Int, date: LocalDate, maxCalories: Int?): DailyMealPlanner {
        if (checkIfDailyMealPlannerExists(userId, date) == null) throw DailyMealPlannerNotFound()

        val jdbiDailyPlanner = tm.run {
            it.mealPlannerRepository.updateDailyCalories(userId, date, maxCalories)
        }

        val dailyMeals = jdbiDailyPlanner.meals.mapValues { (_, recipe) ->
            val picture = cs.pictureRepository.getPicture(recipe.picturesNames.first(), RECIPES_FOLDER)
            recipe.toRecipeInfo(picture)
        }

        return DailyMealPlanner(
            date = jdbiDailyPlanner.date,
            maxCalories = jdbiDailyPlanner.maxCalories,
            meals = dailyMeals
        )
    }

    fun removeMealTimeDailyMealPlanner(userId: Int, date: LocalDate, mealTime: MealTime): DailyMealPlanner {
        if (checkIfDailyMealPlannerExists(userId, date) == null) throw DailyMealPlannerNotFound()
        if (!checkIfMealTimeAlreadyExistsInPlanner(userId, date, mealTime))
            throw MealTimeDoesNotExist()

        val jdbiDailyPlanner = tm.run {
            it.mealPlannerRepository.removeMealTimeDailyMealPlanner(userId, date, mealTime)
        }

        val dailyMeals = jdbiDailyPlanner.meals.mapValues { (_, recipe) ->
            val picture = cs.pictureRepository.getPicture(recipe.picturesNames.first(), RECIPES_FOLDER)
            recipe.toRecipeInfo(picture)
        }
        return DailyMealPlanner(date, jdbiDailyPlanner.maxCalories, dailyMeals)
    }

    fun deleteDailyMealPlanner(userId: Int, date: LocalDate): MealPlanner {
        if (checkIfDailyMealPlannerExists(userId, date) == null) throw DailyMealPlannerNotFound()

        val jdbiPlanner = tm.run {
            it.mealPlannerRepository.deleteDailyMealPlanner(userId, date)
        }
        val planner = getRecipeInfoPicture(jdbiPlanner.planner)
        return MealPlanner(planner)
    }

    private fun getRecipeInfoPicture(planner: List<JdbiDailyMealPlanner>): List<DailyMealPlanner> =
        planner.map { daily ->
            val dailyMeals = daily.meals.mapValues { (_, recipe) ->
                val picture = cs.pictureRepository.getPicture(recipe.picturesNames.first(), RECIPES_FOLDER)
                recipe.toRecipeInfo(picture)
            }
            DailyMealPlanner(date = daily.date, maxCalories = daily.maxCalories, meals = dailyMeals)
        }

    private fun checkIfDailyMealPlannerExists(userId: Int, date: LocalDate): JdbiDailyMealPlanner? =
        tm.run { it.mealPlannerRepository.checkIfDailyMealPlannerExists(userId, date) }

    private fun checkIfRecipeExists(recipeId: Int): JdbiRecipeModel =
        tm.run { it.recipeRepository.getRecipeById(recipeId) } ?: throw RecipeNotFound()

    private fun checkRecipeAccessibility(authorUsername: String, username: String) {
        if (!tm.run { it.userRepository.checkUserVisibility(authorUsername, username) })
            throw RecipeNotAccessible()
    }

    private fun checkIfMealTimeAlreadyExistsInPlanner(userId: Int, date: LocalDate, mealTime: MealTime): Boolean =
        tm.run { it.mealPlannerRepository.checkIfMealTimeAlreadyExistsInPlanner(userId, date, mealTime) }
}
