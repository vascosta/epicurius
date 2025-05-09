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
        val breakfast = getRecipe(intolerances, diets, MealType.BREAKFAST)
        val soup = getRecipe(intolerances, diets, MealType.SOUP)
        val dessert = getRecipe(intolerances, diets, MealType.DESSERT)
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

    private fun getRecipe(intolerances: List<Intolerance>, diets: List<Diet>, mealType: MealType): RecipeInfo? {
        val recipeFromPublicUsers = tm.run {
            it.recipeRepository.getRandomRecipesFromPublicUsers(mealType, intolerances, diets, 1)
        }

        return if (recipeFromPublicUsers.isNotEmpty()) {
            val jdbiRecipeModel = recipeFromPublicUsers.first()
            val recipePicture = cs.pictureRepository.getPicture(jdbiRecipeModel.picturesNames.first(), RECIPES_FOLDER)
            jdbiRecipeModel.toRecipeInfo(recipePicture)
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
                val lunchRecipeModel = mainCourseFromPublicUsers.first() // lunch has priority over dinner
                val lunchPicture = cs.pictureRepository.getPicture(lunchRecipeModel.picturesNames.first(), RECIPES_FOLDER)
                listOf(lunchRecipeModel.toRecipeInfo(lunchPicture), null)
            }
            else -> { listOf(null, null) }
        }
    }
}
