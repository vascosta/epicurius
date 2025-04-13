package epicurius.services

import epicurius.domain.PictureDomain.Companion.RECIPES_FOLDER
import epicurius.domain.mealPlanner.DailyMealPlanner
import epicurius.domain.mealPlanner.MealPlanner
import epicurius.repository.cloudStorage.CloudStorageManager
import epicurius.repository.transaction.TransactionManager
import org.springframework.stereotype.Component
import java.util.Date

@Component
class MealPlannerService(
    private val tm: TransactionManager,
    private val cs: CloudStorageManager
) {

    fun createMealPlanner(userId: Int, date: Date) {
        // checkIfMealPlannerAlreadyExists(userId, date)
        tm.run { it.mealPlannerRepository.createMealPlanner(userId, date) }
    }

    fun getMealPlanner(userId: Int): MealPlanner {
        val jdbiPlanner = tm.run { it.mealPlannerRepository.getMealPlanner(userId) }

        val planner = jdbiPlanner.planner.map { daily ->
            val dailyMeals = daily.meals.mapValues { (_, recipe) ->
                val picture = cs.pictureCloudStorageRepository.getPicture(recipe.pictures.first(), RECIPES_FOLDER)
                recipe.toRecipeInfo(picture)
            }
            DailyMealPlanner(date = daily.date, meals = dailyMeals)
        }

        return MealPlanner(planner)
    }
}
