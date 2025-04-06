package epicurius.services

import epicurius.domain.PictureDomain
import epicurius.domain.recipe.RecipeProfile
import epicurius.domain.recipe.SearchRecipesModel
import epicurius.http.recipe.models.input.CreateRecipeInputModel
import epicurius.http.recipe.models.input.SearchRecipesInputModel
import epicurius.repository.cloudStorage.CloudStorageManager
import epicurius.repository.spoonacular.SpoonacularManager
import epicurius.repository.transaction.TransactionManager
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@Component
class RecipeService(
    private val tm: TransactionManager,
    private val cs: CloudStorageManager,
    private val sm: SpoonacularManager,
    private val pictureDomain: PictureDomain,
) {

    fun searchRecipes(userId: Int, name: String?, form: SearchRecipesInputModel): List<RecipeProfile> {
        val fillForm = form.toSearchRecipe(name)
        return tm.run {
            it.recipeRepository.searchRecipes(userId, fillForm)
        }
    }

    fun createRecipe(authorId: Int, recipeInfo: CreateRecipeInputModel, pictures: List<MultipartFile>): RecipeProfile {

        if (pictures.isEmpty()) {
            throw IllegalArgumentException("At least one image is required")
        }

        else {
            pictures.forEach { pictureDomain.validatePicture(it) }
            val picturesNames = pictures.map { UUID.randomUUID().toString() }

            val recipeId = tm.run {
                it.recipeRepository.createRecipe(recipeInfo.toCreateRecipeInputModel(authorId, picturesNames))
            }

            picturesNames.forEachIndexed { index, pictureName ->
                cs.pictureCloudStorageRepository.updatePicture(
                    pictureName,
                    pictures[index]
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
}