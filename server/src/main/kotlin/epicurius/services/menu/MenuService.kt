package epicurius.services.menu

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.picture.PictureDomain.Companion.RECIPES_FOLDER
import epicurius.domain.recipe.MealType
import epicurius.domain.recipe.RecipeInfo
import epicurius.repository.cloudStorage.manager.CloudStorageManager
import epicurius.repository.transaction.TransactionManager
import org.springframework.stereotype.Component

@Component
class MenuService(private val tm: TransactionManager, private val cs: CloudStorageManager) {

        fun getDailyMenu(intolerances: List<Intolerance>, diets: List<Diet>): Map<String, RecipeInfo?> {
            val breakfast = getBreakfastRecipe(intolerances, diets)
            val soup = getSoupRecipe(intolerances, diets)
            val dessert = getDessertRecipe(intolerances, diets)
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

    private fun getBreakfastRecipe(intolerances: List<Intolerance>, diets: List<Diet>): RecipeInfo? {
        val breakfastFromPublicUsers = tm.run {
            it.recipeRepository.getRandomRecipesFromPublicUsers(MealType.BREAKFAST, intolerances, diets, 1)
        }

        return if (breakfastFromPublicUsers.isNotEmpty()) {
            val jdbiBreakfastRecipeModel = breakfastFromPublicUsers.first()
            val breakfastPicture = cs.pictureRepository.getPicture(jdbiBreakfastRecipeModel.picturesNames.first(), RECIPES_FOLDER)
            jdbiBreakfastRecipeModel.toRecipeInfo(breakfastPicture)
        } else {
            null
        }
    }

    private fun getSoupRecipe(intolerances: List<Intolerance>, diets: List<Diet>): RecipeInfo? {
        val soupFromPublicUsers = tm.run {
            it.recipeRepository.getRandomRecipesFromPublicUsers(MealType.SOUP, intolerances, diets, 1)
        }

        return if (soupFromPublicUsers.isNotEmpty()) {
            val jdbiSoupRecipeModel = soupFromPublicUsers.first()
            val soupPicture = cs.pictureRepository.getPicture(jdbiSoupRecipeModel.picturesNames.first(), RECIPES_FOLDER)
            jdbiSoupRecipeModel.toRecipeInfo(soupPicture)
        } else {
            null
        }
    }

    private fun getDessertRecipe(intolerances: List<Intolerance>, diets: List<Diet>): RecipeInfo? {
        val dessertFromPublicUsers = tm.run {
            it.recipeRepository.getRandomRecipesFromPublicUsers(MealType.DESSERT, intolerances, diets, 1)
        }

        return if (dessertFromPublicUsers.isNotEmpty()) {
            val jdbiDessertRecipeModel = dessertFromPublicUsers.first()
            val dessertPicture = cs.pictureRepository.getPicture(jdbiDessertRecipeModel.picturesNames.first(), RECIPES_FOLDER)
            jdbiDessertRecipeModel.toRecipeInfo(dessertPicture)
        } else {
            null
        }
    }

    private fun getMainCourseRecipes(intolerances: List<Intolerance>, diets: List<Diet>): List<RecipeInfo?> {
        val mainCourseFromPublicUsers = tm.run {
            it.recipeRepository.getRandomRecipesFromPublicUsers(MealType.MAIN_COURSE, intolerances, diets, 2)
        }

        return when (mainCourseFromPublicUsers.size) {
            2 -> {
                val jdbiLunchRecipeModel = mainCourseFromPublicUsers[0]
                val lunchPicture = cs.pictureRepository.getPicture(jdbiLunchRecipeModel.picturesNames.first(), RECIPES_FOLDER)
                val jdbiDinnerRecipeModel = mainCourseFromPublicUsers[1]
                val dinnerPicture = cs.pictureRepository.getPicture(jdbiDinnerRecipeModel.picturesNames.first(), RECIPES_FOLDER)
                listOf(jdbiLunchRecipeModel.toRecipeInfo(lunchPicture), jdbiDinnerRecipeModel.toRecipeInfo(dinnerPicture))
            }
            1 -> {
                val lunchRecipeModel = mainCourseFromPublicUsers.first()  // lunch has priority over dinner
                val lunchPicture = cs.pictureRepository.getPicture(lunchRecipeModel.picturesNames.first(), RECIPES_FOLDER)
                listOf(lunchRecipeModel.toRecipeInfo(lunchPicture), null)
            }
            else -> { listOf(null, null) }
        }
    }
}
