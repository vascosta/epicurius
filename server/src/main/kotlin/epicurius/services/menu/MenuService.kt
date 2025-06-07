package epicurius.services.menu

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.menu.Menu
import epicurius.domain.picture.PictureDomain.Companion.RECIPES_FOLDER
import epicurius.domain.recipe.MealType
import epicurius.domain.recipe.RecipeInfo
import epicurius.repository.cloudStorage.manager.CloudStorageManager
import epicurius.repository.transaction.TransactionManager
import org.springframework.stereotype.Service

@Service
class MenuService(private val tm: TransactionManager, private val cs: CloudStorageManager) {

    fun getDailyMenu(userId: Int, intolerances: List<Intolerance>, diets: List<Diet>): Menu {
        val breakfast = getRecipe(userId, intolerances, diets, MealType.BREAKFAST)
        val soup = getRecipe(userId, intolerances, diets, MealType.SOUP)
        val dessert = getRecipe(userId, intolerances, diets, MealType.DESSERT)
        val mainCourses = getMainCourseRecipes(userId, intolerances, diets)
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

    private fun getRecipe(userId: Int, intolerances: List<Intolerance>, diets: List<Diet>, mealType: MealType): RecipeInfo? {
        val recipeFromPublicUsers = tm.run {
            it.recipeRepository.getRandomRecipesFromPublicUsers(userId, mealType, intolerances, diets, 1)
        }

        return if (recipeFromPublicUsers.isNotEmpty()) {
            val jdbiRecipeModel = recipeFromPublicUsers.first()
            val recipePicture = cs.pictureRepository.getPicture(jdbiRecipeModel.picturesNames.first(), RECIPES_FOLDER)
            val isInCollection = tm.run {
                it.collectionRepository.checkIfRecipeInAnyUserCollection(userId, jdbiRecipeModel.id)
            }
            jdbiRecipeModel.toRecipeInfo(recipePicture, isInCollection)
        } else {
            null
        }
    }

    private fun getMainCourseRecipes(userId: Int, intolerances: List<Intolerance>, diets: List<Diet>): List<RecipeInfo?> {
        val mainCourseFromPublicUsers = tm.run {
            it.recipeRepository.getRandomRecipesFromPublicUsers(userId, MealType.MAIN_COURSE, intolerances, diets, 2)
        }

        return when (mainCourseFromPublicUsers.size) {
            2 -> {
                val jdbiLunchRecipeModel = mainCourseFromPublicUsers[0]
                val lunchPicture = cs.pictureRepository.getPicture(jdbiLunchRecipeModel.picturesNames.first(), RECIPES_FOLDER)
                val isLunchInCollection = tm.run {
                    it.collectionRepository.checkIfRecipeInAnyUserCollection(userId, jdbiLunchRecipeModel.id)
                }
                val jdbiDinnerRecipeModel = mainCourseFromPublicUsers[1]
                val dinnerPicture = cs.pictureRepository.getPicture(jdbiDinnerRecipeModel.picturesNames.first(), RECIPES_FOLDER)
                val isDinnerInCollection = tm.run {
                    it.collectionRepository.checkIfRecipeInAnyUserCollection(userId, jdbiDinnerRecipeModel.id)
                }
                listOf(
                    jdbiLunchRecipeModel.toRecipeInfo(lunchPicture, isLunchInCollection),
                    jdbiDinnerRecipeModel.toRecipeInfo(dinnerPicture, isDinnerInCollection)
                )
            }
            1 -> {
                val lunchRecipeModel = mainCourseFromPublicUsers.first() // lunch has priority over dinner
                val lunchPicture = cs.pictureRepository.getPicture(lunchRecipeModel.picturesNames.first(), RECIPES_FOLDER)
                val isInCollection = tm.run {
                    it.collectionRepository.checkIfRecipeInAnyUserCollection(userId, lunchRecipeModel.id)
                }
                listOf(lunchRecipeModel.toRecipeInfo(lunchPicture, isInCollection), null)
            }
            else -> { listOf(null, null) }
        }
    }
}
