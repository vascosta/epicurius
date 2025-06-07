package epicurius.repository.jdbi.mealPlanner.models

import epicurius.domain.mealPlanner.DailyMealPlanner
import epicurius.domain.mealPlanner.MealTime
import epicurius.domain.picture.PictureDomain.Companion.RECIPES_FOLDER
import epicurius.repository.cloudStorage.manager.CloudStorageManager
import epicurius.repository.jdbi.recipe.models.JdbiRecipeInfo
import epicurius.repository.transaction.TransactionManager
import java.time.LocalDate

data class JdbiDailyMealPlanner(val date: LocalDate, val maxCalories: Int?, val meals: Map<MealTime, JdbiRecipeInfo>) {

    fun toDailyMealPlanner(
        tm: TransactionManager,
        cs: CloudStorageManager,
        userId: Int
    ): DailyMealPlanner {
        val dailyMeals = meals.mapValues { (_, recipe) ->
            val picture = cs.pictureRepository.getPicture(recipe.picturesNames.first(), RECIPES_FOLDER)
            val isInCollection = tm.run {
                it.collectionRepository.checkIfRecipeInAnyUserCollection(userId, recipe.id)
            }
            recipe.toRecipeInfo(picture, isInCollection)
        }
        return DailyMealPlanner(date = date, maxCalories = maxCalories, meals = dailyMeals)
    }

    companion object {

        fun List<JdbiDailyMealPlanner>.toDailyMealPlannerList(
            tm: TransactionManager,
            cs: CloudStorageManager,
            userId: Int
        ): List<DailyMealPlanner> =
            map { daily -> daily.toDailyMealPlanner(tm, cs, userId) }
    }
}
