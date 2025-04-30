package epicurius.services.menu

import epicurius.domain.recipe.MealType
import epicurius.repository.jdbi.recipe.models.JdbiRecipeModel
import epicurius.repository.transaction.TransactionManager
import org.springframework.stereotype.Component

@Component
class MenuService(private val tm: TransactionManager) {

        fun getDailyMenu(userId: Int): Map<String, JdbiRecipeModel?> {
            val breakfast = getBreakfastRecipes(userId)
            val soup = getSoupRecipes(userId)
            val dessert = getDessertRecipes(userId)
            val mainCourses = getMainCourseRecipes(userId)
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

    private fun getBreakfastRecipes(userId: Int): JdbiRecipeModel? {
        val breakfastFromPublicUsers = tm.run { it.recipeRepository.getRandomRecipesFromPublicUsers(MealType.BREAKFAST, 1) }
        val breakfastFromFollowing = tm.run { it.recipeRepository.getRandomRecipesFromFollowing(userId, MealType.BREAKFAST, 1) }

        return if (breakfastFromPublicUsers.isEmpty() && breakfastFromFollowing.isEmpty()) {
            null
        } else {
            (breakfastFromPublicUsers + breakfastFromFollowing).random()
        }
    }

    private fun getSoupRecipes(userId: Int): JdbiRecipeModel? {
        val soupFromPublicUsers = tm.run { it.recipeRepository.getRandomRecipesFromPublicUsers(MealType.SOUP, 1) }
        val soupFromFollowing = tm.run { it.recipeRepository.getRandomRecipesFromFollowing(userId, MealType.SOUP, 1) }

        return if (soupFromPublicUsers.isEmpty() && soupFromFollowing.isEmpty()) {
            null
        } else {
            (soupFromPublicUsers + soupFromFollowing).random()
        }
    }

    private fun getDessertRecipes(userId: Int): JdbiRecipeModel? {
        val dessertFromPublicUsers = tm.run { it.recipeRepository.getRandomRecipesFromPublicUsers(MealType.DESSERT, 1) }
        val dessertFromFollowing = tm.run { it.recipeRepository.getRandomRecipesFromFollowing(userId, MealType.DESSERT, 1) }

        return if (dessertFromPublicUsers.isEmpty() && dessertFromFollowing.isEmpty()) {
            null
        } else {
            (dessertFromPublicUsers + dessertFromFollowing).random()
        }
    }

    private fun getMainCourseRecipes(userId: Int): List<JdbiRecipeModel?> {
        val mainCourseFromPublicUsers = tm.run { it.recipeRepository.getRandomRecipesFromPublicUsers(MealType.MAIN_COURSE, 2) }
        val mainCourseFromFollowing = tm.run { it.recipeRepository.getRandomRecipesFromFollowing(userId, MealType.MAIN_COURSE, 2) }

        return when {
            mainCourseFromPublicUsers.isEmpty() && mainCourseFromFollowing.isEmpty() -> {
                listOf(null, null)
            }
            mainCourseFromPublicUsers.size == 1 && mainCourseFromFollowing.isEmpty() -> {
                listOf(mainCourseFromPublicUsers.first(), null)
            }
            mainCourseFromPublicUsers.isEmpty() && mainCourseFromFollowing.size == 1 -> {
                listOf(null, mainCourseFromFollowing.first())
            }
            mainCourseFromPublicUsers.size == 2 && mainCourseFromFollowing.isEmpty() -> {
                listOf(mainCourseFromPublicUsers[0], mainCourseFromPublicUsers[1])
            }
            mainCourseFromPublicUsers.isEmpty() && mainCourseFromFollowing.size == 2 -> {
                listOf(mainCourseFromFollowing[0], mainCourseFromFollowing[1])
            }
            mainCourseFromPublicUsers.size == 1 && mainCourseFromFollowing.size == 1 -> {
                listOf(mainCourseFromPublicUsers.first(), mainCourseFromFollowing.first())
            }
            mainCourseFromPublicUsers.size == 2 && mainCourseFromFollowing.size == 1 -> {
                listOf(mainCourseFromPublicUsers.random(), mainCourseFromFollowing.first())
            }
            mainCourseFromPublicUsers.size == 1 && mainCourseFromFollowing.size == 2 -> {
                listOf(mainCourseFromPublicUsers.first(), mainCourseFromFollowing.random())
            }
            mainCourseFromPublicUsers.size == 2 && mainCourseFromFollowing.size == 2 -> {
                listOf(mainCourseFromPublicUsers.random(), mainCourseFromFollowing.random())
            }
            else -> { listOf(null, null) }
        }
    }
}
