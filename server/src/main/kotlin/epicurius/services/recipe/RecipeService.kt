package epicurius.services.recipe

import epicurius.domain.picture.PictureDomain
import epicurius.domain.picture.PictureDomain.Companion.RECIPES_FOLDER
import epicurius.domain.exceptions.InvalidNumberOfRecipePictures
import epicurius.domain.exceptions.NotTheAuthor
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.domain.recipe.Recipe
import epicurius.domain.recipe.RecipeDomain.Companion.MAX_PICTURES
import epicurius.domain.recipe.RecipeDomain.Companion.MIN_PICTURES
import epicurius.domain.recipe.RecipeInfo
import epicurius.http.recipe.models.input.CreateRecipeInputModel
import epicurius.http.recipe.models.input.SearchRecipesInputModel
import epicurius.http.recipe.models.input.UpdateRecipeInputModel
import epicurius.repository.cloudFunction.manager.CloudFunctionManager
import epicurius.repository.cloudStorage.manager.CloudStorageManager
import epicurius.repository.firestore.FirestoreManager
import epicurius.repository.jdbi.recipe.models.JdbiRecipeModel
import epicurius.repository.jdbi.recipe.models.JdbiUpdateRecipeModel
import epicurius.repository.spoonacular.manager.SpoonacularManager
import epicurius.repository.transaction.TransactionManager
import epicurius.services.recipe.models.UpdateRecipeModel
import epicurius.services.recipe.models.UpdateRecipePicturesModel
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile

@Component
class RecipeService(
    private val tm: TransactionManager,
    private val fs: FirestoreManager,
    private val cs: CloudStorageManager,
    private val sm: SpoonacularManager,
    private val cf: CloudFunctionManager,
    private val pictureDomain: PictureDomain
) {

    fun createRecipe(authorId: Int, authorName: String, recipeInfo: CreateRecipeInputModel, pictures: List<MultipartFile>): Recipe {

        if (pictures.size !in MIN_PICTURES..MAX_PICTURES) {
            throw InvalidNumberOfRecipePictures()
        } else {
            pictures.forEach { pictureDomain.validatePicture(it) }
            val picturesNames = pictures.map { pictureDomain.generatePictureName() }

            val jdbiCreateRecipeModel = recipeInfo.toJdbiCreateRecipeModel(authorId, picturesNames)

            val recipeId = tm.run { it.recipeRepository.createRecipe(jdbiCreateRecipeModel) }

            fs.recipeRepository.createRecipe(recipeInfo.toFirestoreRecipeModel(recipeId))

            picturesNames.forEachIndexed { index, pictureName ->
                cs.pictureRepository.updatePicture(
                    pictureName,
                    pictures[index],
                    RECIPES_FOLDER
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
                pictures.map { it.bytes }
            )
        }
    }

    suspend fun getRecipe(recipeId: Int): Recipe {
        val jdbiRecipe = tm.run { it.recipeRepository.getRecipe(recipeId) } ?: throw RecipeNotFound()
        val firestoreRecipe = fs.recipeRepository.getRecipe(recipeId) ?: throw RecipeNotFound()
        val recipePictures = jdbiRecipe.picturesNames.map {
            cs.pictureRepository.getPicture(it, RECIPES_FOLDER)
        }

        return jdbiRecipe.toRecipe(firestoreRecipe.description, firestoreRecipe.instructions, recipePictures)
    }

    fun searchRecipes(userId: Int, form: SearchRecipesInputModel): List<RecipeInfo> {
        val fillForm = form.toSearchRecipe(form.name)
        val recipesList = tm.run { it.recipeRepository.searchRecipes(userId, fillForm) }

        val recipes = if (form.ingredients != null) {
            val recipesByIngredients = tm.run {
                it.recipeRepository.searchRecipesByIngredients(userId, form.ingredients)
            }
            recipesList.intersect(recipesByIngredients.toSet()).toList()
        } else {
            recipesList
        }

        return recipes.map {
            it.toRecipeInfo(cs.pictureRepository.getPicture(it.pictures.first(), RECIPES_FOLDER))
        }
    }

    suspend fun updateRecipe(userId: Int, recipeId: Int, recipeInfo: UpdateRecipeInputModel): UpdateRecipeModel {
        val jdbiRecipeModel = checkIfRecipeExists(recipeId) ?: throw RecipeNotFound()
        checkIfUserIsAuthor(userId, jdbiRecipeModel.authorId)

        val jdbiRecipe = tm.run { it.recipeRepository.updateRecipe(recipeInfo.toJdbiUpdateRecipeModel(recipeId, null)) }
        val firestoreRecipe = fs.recipeRepository.updateRecipe(recipeInfo.toFirestoreUpdateRecipeModel(recipeId))

        return UpdateRecipeModel(
            recipeId,
            jdbiRecipe.name,
            jdbiRecipe.authorUsername,
            jdbiRecipe.date,
            firestoreRecipe.description,
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
            firestoreRecipe.instructions
        )
    }

    fun updateRecipePictures(userId: Int, recipeId: Int, newPictures: List<MultipartFile>): UpdateRecipePicturesModel {
        if (newPictures.size !in MIN_PICTURES..MAX_PICTURES) {
            throw InvalidNumberOfRecipePictures()
        }

        val recipe = checkIfRecipeExists(recipeId) ?: throw RecipeNotFound()
        checkIfUserIsAuthor(userId, recipe.authorId)

        val picturesBytes = recipe.picturesNames.map { cs.pictureRepository.getPicture(it, RECIPES_FOLDER) }
        val newPicturesBytes = newPictures.map { it.bytes }

        if (picturesBytes == newPictures) { // if the pictures are equal and in the same order
            return UpdateRecipePicturesModel(picturesBytes)
        } else {
            val pictureNames = recipe.picturesNames

            val reorderedPicturesNames = newPicturesBytes.map { newPicture ->
                val oldIdx = picturesBytes.indexOfFirst { it.contentEquals(newPicture) }
                if (oldIdx != -1) {
                    pictureNames[oldIdx] // if the picture is already saved, keep the same name
                } else {
                    pictureDomain.generatePictureName() // new picture
                }
            }

            val picturesToDelete = pictureNames.filter { !reorderedPicturesNames.contains(it) }
            picturesToDelete.forEach { cs.pictureRepository.deletePicture(it, RECIPES_FOLDER) }

            tm.run { it.recipeRepository.updateRecipe(JdbiUpdateRecipeModel(recipeId, picturesNames = reorderedPicturesNames)) }
            val picturesToAdd = reorderedPicturesNames.filter { !pictureNames.contains(it) }

            picturesToAdd.forEachIndexed { _, pictureName ->
                val pictureIdx = reorderedPicturesNames.indexOf(pictureName)
                if (pictureIdx != -1) {
                    cs.pictureRepository.updatePicture(
                        pictureName,
                        newPictures[pictureIdx],
                        RECIPES_FOLDER
                    )
                }
            }

            return UpdateRecipePicturesModel(picturesBytes)
        }
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
        if (userId != authorId) throw NotTheAuthor()
    }
}
