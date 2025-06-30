package epicurius.services.recipe

import epicurius.domain.exceptions.InvalidIngredient
import epicurius.domain.exceptions.InvalidNumberOfRecipePictures
import epicurius.domain.exceptions.NotTheRecipeAuthor
import epicurius.domain.exceptions.RecipeNotAccessible
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.domain.exceptions.UserNotFound
import epicurius.domain.picture.PictureDomain
import epicurius.domain.picture.PictureDomain.Companion.RECIPES_FOLDER
import epicurius.domain.recipe.Ingredient
import epicurius.domain.recipe.MAX_PICTURES
import epicurius.domain.recipe.MIN_PICTURES
import epicurius.domain.recipe.Recipe
import epicurius.domain.recipe.RecipeInfo
import epicurius.http.controllers.recipe.models.input.CreateRecipeInputModel
import epicurius.http.controllers.recipe.models.input.SearchRecipesInputModel
import epicurius.http.controllers.recipe.models.input.UpdateRecipeInputModel
import epicurius.repository.cloudStorage.manager.CloudStorageManager
import epicurius.repository.jdbi.recipe.models.JdbiRecipeModel
import epicurius.repository.jdbi.recipe.models.JdbiUpdateRecipeModel
import epicurius.repository.spoonacular.manager.SpoonacularManager
import epicurius.repository.transaction.TransactionManager
import epicurius.services.recipe.models.UpdateRecipeModel
import epicurius.services.recipe.models.UpdateRecipePicturesModel
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class RecipeService(
    private val tm: TransactionManager,
    private val cs: CloudStorageManager,
    private val sm: SpoonacularManager,
    private val pictureDomain: PictureDomain
) {

    suspend fun createRecipe(authorId: Int, authorName: String, recipeInfo: CreateRecipeInputModel, pictures: Set<MultipartFile>): Recipe {
        if (pictures.size !in MIN_PICTURES..MAX_PICTURES) {
            throw InvalidNumberOfRecipePictures()
        } else {
            validateIngredients(recipeInfo.ingredients)
            pictures.forEach { pictureDomain.validatePicture(it) }
            val picturesNames = pictures.map { pictureDomain.generatePictureName() }

            val jdbiCreateRecipeModel = recipeInfo.toJdbiCreateRecipeModel(authorId, picturesNames)

            val recipeId = tm.run { it.recipeRepository.createRecipe(jdbiCreateRecipeModel) }

            picturesNames.forEachIndexed { index, pictureName ->
                cs.pictureRepository.updatePicture(
                    pictureName,
                    pictures.elementAt(index),
                    RECIPES_FOLDER
                )
            }

            return Recipe(
                recipeId,
                recipeInfo.name,
                authorName,
                0.0, // Initial rating is 0
                jdbiCreateRecipeModel.date,
                recipeInfo.description,
                recipeInfo.servings,
                recipeInfo.preparationTime,
                recipeInfo.cuisine,
                recipeInfo.mealType,
                recipeInfo.intolerances.toList(),
                recipeInfo.diets.toList(),
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

    suspend fun getRecipe(recipeId: Int, userId: Int): Recipe {
        val jdbiRecipe = tm.run { it.recipeRepository.getRecipeById(recipeId) } ?: throw RecipeNotFound()

        checkOwnerVisibility(jdbiRecipe.authorUsername, userId)

        val recipePictures = jdbiRecipe.picturesNames.map { cs.pictureRepository.getPicture(it, RECIPES_FOLDER) }

        return jdbiRecipe.toRecipe(recipePictures)
    }

    suspend fun getUserRecipes(userId: Int, username: String?, lastRecipeId: Int?, limit: Int): List<RecipeInfo> {
        if (username != null) {
            val user = tm.run { it.userRepository.getUser(username) ?: throw UserNotFound(username) }
            checkOwnerVisibility(username, userId)
            val recipes = tm.run { it.recipeRepository.getUserRecipes(user.id, lastRecipeId, limit) }
            return recipes.map { jdbiRecipeInfo ->
                jdbiRecipeInfo.toRecipeInfo(
                    cs.pictureRepository.getPicture(jdbiRecipeInfo.picturesNames.first(), RECIPES_FOLDER),
                )
            }
        }
        val recipes = tm.run { it.recipeRepository.getUserRecipes(userId, lastRecipeId, limit) }
        return recipes.map { jdbiRecipeInfo ->
            jdbiRecipeInfo.toRecipeInfo(
                cs.pictureRepository.getPicture(jdbiRecipeInfo.picturesNames.first(), RECIPES_FOLDER),
            )
        }
    }

    fun searchRecipes(userId: Int, form: SearchRecipesInputModel, lastRecipeId: Int?, limit: Int): List<RecipeInfo> {
        val fillForm = form.toSearchRecipeModel(form.name)
        val recipes = tm.run { it.recipeRepository.searchRecipes(userId, fillForm, lastRecipeId, limit) }

        return recipes.map { jdbiRecipeInfo ->
            jdbiRecipeInfo.toRecipeInfo(
                cs.pictureRepository.getPicture(jdbiRecipeInfo.picturesNames.first(), RECIPES_FOLDER),
            )
        }
    }

    suspend fun updateRecipe(userId: Int, recipeId: Int, recipeInfo: UpdateRecipeInputModel): UpdateRecipeModel {
        if (recipeInfo.ingredients != null) {
            validateIngredients(recipeInfo.ingredients)
        }

        val jdbiRecipeModel = checkIfRecipeExists(recipeId) ?: throw RecipeNotFound()
        checkIfUserIsAuthor(userId, jdbiRecipeModel.authorId)

        val jdbiRecipe = tm.run { it.recipeRepository.updateRecipe(recipeInfo.toJdbiUpdateRecipeModel(recipeId, null)) }

        return UpdateRecipeModel(
            recipeId,
            jdbiRecipe.name,
            jdbiRecipe.authorUsername,
            jdbiRecipe.date,
            jdbiRecipe.description,
            jdbiRecipe.servings,
            jdbiRecipe.preparationTime,
            jdbiRecipe.cuisine,
            jdbiRecipe.mealType,
            jdbiRecipe.intolerances.toList(),
            jdbiRecipe.diets.toList(),
            jdbiRecipe.ingredients,
            jdbiRecipe.calories,
            jdbiRecipe.protein,
            jdbiRecipe.fat,
            jdbiRecipe.carbs,
            jdbiRecipe.instructions,
        )
    }

    fun updateRecipePictures(userId: Int, recipeId: Int, newPictures: Set<MultipartFile>): UpdateRecipePicturesModel {
        if (newPictures.size !in MIN_PICTURES..MAX_PICTURES) {
            throw InvalidNumberOfRecipePictures()
        }

        val recipe = checkIfRecipeExists(recipeId) ?: throw RecipeNotFound()
        checkIfUserIsAuthor(userId, recipe.authorId)

        val picturesBytes = recipe.picturesNames.map { cs.pictureRepository.getPicture(it, RECIPES_FOLDER) }
        val newPicturesBytes = newPictures.map { it.bytes }

        if (newPicturesBytes == picturesBytes) { // if the pictures are equal and in the same order
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
                        newPictures.elementAt(pictureIdx),
                        RECIPES_FOLDER
                    )
                }
            }

            return UpdateRecipePicturesModel(newPicturesBytes)
        }
    }

    fun deleteRecipe(userId: Int, recipeId: Int) {
        val recipe = checkIfRecipeExists(recipeId) ?: throw RecipeNotFound()
        checkIfUserIsAuthor(userId, recipe.authorId)
        tm.run { it.recipeRepository.deleteRecipe(recipeId) }
    }

    private suspend fun validateIngredients(ingredients: List<Ingredient>) {
        ingredients.forEach { ingredient ->
            val lowerCaseIngredient = ingredient.name.lowercase()
            val ingredientsList = sm.spoonacularRepository.getIngredients(lowerCaseIngredient)
            if (!ingredientsList.contains(lowerCaseIngredient)) throw InvalidIngredient(ingredient.name)
        }
    }

    private fun checkIfRecipeExists(recipeId: Int): JdbiRecipeModel? =
        tm.run { it.recipeRepository.getRecipeById(recipeId) }

    private fun checkOwnerVisibility(authorUsername: String, userId: Int) {
        if (!tm.run { it.userRepository.checkUserVisibility(authorUsername, userId) })
            throw RecipeNotAccessible()
    }

    private fun checkIfUserIsAuthor(userId: Int, authorId: Int) {
        if (userId != authorId) throw NotTheRecipeAuthor()
    }
}
