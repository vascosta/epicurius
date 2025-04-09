package epicurius.services

import epicurius.domain.PictureDomain
import epicurius.domain.recipe.Recipe
import epicurius.domain.recipe.RecipeDomain.Companion.IMAGES_MSG
import epicurius.domain.recipe.RecipeDomain.Companion.MAX_IMAGES
import epicurius.domain.recipe.RecipeDomain.Companion.MIN_IMAGES
import epicurius.domain.recipe.RecipeProfile
import epicurius.http.recipe.models.input.CreateRecipeInputModel
import epicurius.http.recipe.models.input.SearchRecipesInputModel
import epicurius.repository.cloudStorage.CloudStorageManager
import epicurius.repository.firestore.FirestoreManager
import epicurius.repository.firestore.recipe.models.FirestoreRecipeModel
import epicurius.repository.jdbi.recipe.models.JdbiRecipeModel
import epicurius.repository.spoonacular.SpoonacularManager
import epicurius.repository.transaction.TransactionManager
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@Component
class RecipeService(
    private val tm: TransactionManager,
    private val fs: FirestoreManager,
    private val cs: CloudStorageManager,
    private val sm: SpoonacularManager,
    private val pictureDomain: PictureDomain,
) {

    fun createRecipe(authorId: Int, recipeInfo: CreateRecipeInputModel, pictures: List<MultipartFile>): RecipeProfile {

        if (pictures.size !in MIN_IMAGES..MAX_IMAGES) {
            throw IllegalArgumentException(IMAGES_MSG)
        } else {
            pictures.forEach { pictureDomain.validatePicture(it) }
            val picturesNames = pictures.map { UUID.randomUUID().toString() }

            val recipeId = tm.run {
                it.recipeRepository.createRecipe(recipeInfo.toJdbiRecipeModel(authorId, picturesNames))
            }

            fs.recipeRepository.createRecipe(FirestoreRecipeModel(recipeId, recipeInfo.description, recipeInfo.instructions))

            picturesNames.forEachIndexed { index, pictureName ->
                cs.pictureCloudStorageRepository.updatePicture(
                    pictureName,
                    pictures[index],
                    PictureDomain.RECIPES_FOLDER
                )
            }

            return RecipeProfile(
                recipeId,
                recipeInfo.name,
                recipeInfo.cuisine,
                recipeInfo.mealType,
                recipeInfo.preparationTime,
                recipeInfo.servings
            )
        }
    }

    fun getRecipe(recipeId: Int): RecipeProfile {
        TODO()
    }

    fun searchRecipes(userId: Int, name: String?, form: SearchRecipesInputModel): List<RecipeProfile> {
        val fillForm = form.toSearchRecipe(name)
        return tm.run {
            it.recipeRepository.searchRecipes(userId, fillForm)
        }
    }

    fun deleteRecipe(userId: Int, recipeId: Int) {
        val recipe = checkIfRecipeExists(recipeId) ?: throw IllegalArgumentException("Recipe not found")
        checkIfUserIsAuthor(userId, recipe.authorId)
        tm.run { it.recipeRepository.deleteRecipe(recipeId) }
        fs.recipeRepository.deleteRecipe(recipeId)
    }

    private fun checkIfRecipeExists(recipeId: Int): JdbiRecipeModel? =
        tm.run { it.recipeRepository.getRecipe(recipeId) }

    private fun checkIfUserIsAuthor(userId: Int, authorId: Int) {
        if (userId != authorId) throw IllegalArgumentException("You are not the author of this recipe")
    }
}
