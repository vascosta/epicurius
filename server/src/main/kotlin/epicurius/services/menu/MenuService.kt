package epicurius.services.menu

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.recipe.MealType
import epicurius.repository.jdbi.recipe.models.JdbiRecipeModel
import epicurius.repository.transaction.TransactionManager
import org.springframework.stereotype.Component

@Component
class MenuService(private val tm: TransactionManager) {

        fun getDailyMenu(intolerances: List<Intolerance>, diets: List<Diet>): Map<String, JdbiRecipeModel?> {
            val breakfast = getBreakfastRecipes(intolerances, diets)
            val soup = getSoupRecipes(intolerances, diets)
            val dessert = getDessertRecipes(intolerances, diets)
            val mainCourses = getMainCourseRecipes(intolerances, diets)
            val lunch = mainCourses[0]
            val dinner = mainCourses[1]
            return mapOf(
                "breakfast" to breakfast,
                "soup" to soup,
                "dessert" to dessert,
                "lunch" to lunch,
                "dinner" to dinner
            )
        }

    private fun getBreakfastRecipes(intolerances: List<Intolerance>, diets: List<Diet>): JdbiRecipeModel? {
        val breakfastFromPublicUsers = tm.run {
            it.recipeRepository.getRandomRecipesFromPublicUsers(MealType.BREAKFAST, intolerances, diets, 1)
        }

        return if (breakfastFromPublicUsers.size > 1) {
            breakfastFromPublicUsers.first()
        } else {
            null
        }
    }

    private fun getSoupRecipes(intolerances: List<Intolerance>, diets: List<Diet>): JdbiRecipeModel? {
        val soupFromPublicUsers = tm.run {
            it.recipeRepository.getRandomRecipesFromPublicUsers(MealType.SOUP, intolerances, diets, 1)
        }

        return if (soupFromPublicUsers.size > 1) {
            soupFromPublicUsers.first()
        } else {
            null
        }
    }

    private fun getDessertRecipes(intolerances: List<Intolerance>, diets: List<Diet>): JdbiRecipeModel? {
        val dessertFromPublicUsers = tm.run {
            it.recipeRepository.getRandomRecipesFromPublicUsers(MealType.DESSERT, intolerances, diets, 1)
        }

        return if (dessertFromPublicUsers.size > 1) {
            dessertFromPublicUsers.first()
        } else {
            null
        }
    }

    private fun getMainCourseRecipes(intolerances: List<Intolerance>, diets: List<Diet>): List<JdbiRecipeModel?> {
        val mainCourseFromPublicUsers = tm.run {
            it.recipeRepository.getRandomRecipesFromPublicUsers(MealType.MAIN_COURSE, intolerances, diets, 2)
        }

        return when (mainCourseFromPublicUsers.size) {
            2 -> {
                listOf(mainCourseFromPublicUsers[0], mainCourseFromPublicUsers[1])
            }
            1 -> {
                listOf(mainCourseFromPublicUsers.first(), null) // lunch has priority over dinner
            }
            else -> { listOf(null, null) }
        }
    }
}
