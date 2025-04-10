package epicurius.services

import epicurius.domain.PictureDomain
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.domain.recipe.Instructions
import epicurius.domain.recipe.Recipe
import epicurius.domain.recipe.RecipeDomain.Companion.IMAGES_MSG
import epicurius.domain.recipe.RecipeDomain.Companion.MAX_IMAGES
import epicurius.domain.recipe.RecipeDomain.Companion.MIN_IMAGES
import epicurius.domain.recipe.RecipeInfo
import epicurius.http.recipe.models.input.CreateRecipeInputModel
import epicurius.http.recipe.models.input.SearchRecipesInputModel
import epicurius.http.recipe.models.input.UpdateRecipeInputModel
import epicurius.repository.cloudStorage.CloudStorageManager
import epicurius.repository.firestore.FirestoreManager
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

    fun createRecipe(authorId: Int, authorName: String, recipeInfo: CreateRecipeInputModel, pictures: List<MultipartFile>): Recipe {

        if (pictures.size !in MIN_IMAGES..MAX_IMAGES) {
            throw IllegalArgumentException(IMAGES_MSG)
        } else {
            pictures.forEach { pictureDomain.validatePicture(it) }
            val picturesNames = pictures.map { UUID.randomUUID().toString() }

            val jdbiCreateRecipeModel = recipeInfo.toJdbiRecipeModel(authorId, picturesNames)

            val recipeId = tm.run {
                it.recipeRepository.createRecipe(jdbiCreateRecipeModel)
            }

            fs.recipeRepository.createRecipe(recipeInfo.toFirestoreRecipeModel(recipeId))

            picturesNames.forEachIndexed { index, pictureName ->
                cs.pictureCloudStorageRepository.updatePicture(
                    pictureName,
                    pictures[index],
                    PictureDomain.RECIPES_FOLDER
                )
            }

            return Recipe(
                recipeId,
                recipeInfo.name,
                authorName,
                jdbiCreateRecipeModel.date,
                recipeInfo.description,
                recipeInfo.servings,
                recipeInfo.preparationTime,
                recipeInfo.cuisine,
                recipeInfo.mealType,
                recipeInfo.intolerances,
                recipeInfo.diets,
                recipeInfo.ingredients,
                recipeInfo.calories,
                recipeInfo.protein,
                recipeInfo.fat,
                recipeInfo.carbs,
                recipeInfo.instructions,
                picturesNames
            )
        }
    }

    fun getRecipe(recipeId: Int): Recipe {
        val jdbiRecipe = tm.run { it.recipeRepository.getRecipe(recipeId) } ?: throw RecipeNotFound()
        // missing description and instructions
        return jdbiRecipe.toRecipe(null, Instructions(emptyMap()))
    }

    fun searchRecipes(userId: Int, form: SearchRecipesInputModel): List<RecipeInfo> {
        val fillForm = form.toSearchRecipe(form.name)
        val recipesList = tm.run { it.recipeRepository.searchRecipes(userId, fillForm) }

        return if (form.ingredients != null) {
            val recipesByIngredients = tm.run {
                it.recipeRepository.searchRecipesByIngredients(userId, form.ingredients)
            }
            recipesList.intersect(recipesByIngredients.toSet()).toList()
        } else {
            recipesList
        }
    }

    fun updateRecipe(userId: Int, recipeId: Int, recipeInfo: UpdateRecipeInputModel): Recipe {
        checkIfRecipeExists(recipeId) ?: throw RecipeNotFound()
        checkIfUserIsAuthor(userId, recipeId)

        val jdbiRecipe = tm.run { it.recipeRepository.updateRecipe(recipeInfo.toJdbiUpdateRecipeModel(recipeId)) }
        val firestoreRecipe = fs.recipeRepository.updateRecipe(recipeInfo.toFirestoreUpdateRecipeModel(recipeId))

        return Recipe(
            recipeId,
            jdbiRecipe.name,
            jdbiRecipe.authorUsername,
            jdbiRecipe.date,
            recipeInfo.description ?: firestoreRecipe.description,
            jdbiRecipe.servings,
            jdbiRecipe.preparationTime,
            jdbiRecipe.cuisine,
            jdbiRecipe.mealType,
            jdbiRecipe.intolerances,
            jdbiRecipe.diets,
            jdbiRecipe.ingredients,
            jdbiRecipe.calories,
            jdbiRecipe.protein,
            jdbiRecipe.fat,
            jdbiRecipe.carbs,
            recipeInfo.instructions ?: firestoreRecipe.instructions,
            jdbiRecipe.picturesNames
        )
    }

    fun deleteRecipe(userId: Int, recipeId: Int) {
        val recipe = checkIfRecipeExists(recipeId) ?: throw RecipeNotFound()
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
