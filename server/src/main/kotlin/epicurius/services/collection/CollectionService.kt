package epicurius.services.collection

import epicurius.domain.collection.Collection
import epicurius.domain.collection.CollectionType
import epicurius.domain.exceptions.CollectionAlreadyExists
import epicurius.domain.exceptions.CollectionNotAccessible
import epicurius.domain.exceptions.CollectionNotFound
import epicurius.domain.exceptions.NotTheCollectionOwner
import epicurius.domain.exceptions.NotTheRecipeAuthor
import epicurius.domain.exceptions.RecipeAlreadyInCollection
import epicurius.domain.exceptions.RecipeNotAccessible
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.domain.exceptions.RecipeNotInCollection
import epicurius.domain.exceptions.UserNotFound
import epicurius.domain.picture.PictureDomain.Companion.RECIPES_FOLDER
import epicurius.domain.recipe.RecipeInfo
import epicurius.http.controllers.collection.models.input.CreateCollectionInputModel
import epicurius.http.controllers.collection.models.input.UpdateCollectionInputModel
import epicurius.repository.cloudStorage.manager.CloudStorageManager
import epicurius.repository.jdbi.collection.models.JdbiCollectionModel
import epicurius.repository.jdbi.recipe.models.JdbiRecipeInfo
import epicurius.repository.jdbi.recipe.models.JdbiRecipeModel
import epicurius.repository.transaction.TransactionManager
import org.springframework.stereotype.Component

@Component
class CollectionService(private val tm: TransactionManager, private val cs: CloudStorageManager) {

    fun createCollection(ownerId: Int, createCollectionInfo: CreateCollectionInputModel): Collection {
        if (checkIfOwnedCollectionExists(ownerId, createCollectionInfo.name, createCollectionInfo.type)) {
            throw CollectionAlreadyExists()
        }
        val collectionId = tm.run {
            it.collectionRepository.createCollection(ownerId, createCollectionInfo.name, createCollectionInfo.type)
        }

        return Collection(
            collectionId,
            createCollectionInfo.name,
            createCollectionInfo.type,
            emptyList()
        )
    }

    fun getCollection(userId: Int, username: String, collectionId: Int): Collection {
        val jdbiCollectionModel = checkIfCollectionExists(collectionId)

        if (!checkCollectionVisibility(userId, username, jdbiCollectionModel)) throw CollectionNotAccessible()
        return Collection(
            jdbiCollectionModel.id,
            jdbiCollectionModel.name,
            jdbiCollectionModel.type,
            getRecipesInfo(jdbiCollectionModel.recipes)
        )
    }

    fun updateCollection(userId: Int, collectionId: Int, updateCollectionInfo: UpdateCollectionInputModel): Collection {
        checkIfCollectionExists(collectionId)
        if (!checkIfUserIsCollectionOwner(collectionId, userId)) throw NotTheCollectionOwner()
        val updatedCollection = tm.run {
            it.collectionRepository.updateCollection(collectionId, updateCollectionInfo.name)
        }
        return Collection(
            updatedCollection.id,
            updatedCollection.name,
            updatedCollection.type,
            getRecipesInfo(updatedCollection.recipes)
        )
    }

    fun addRecipeToCollection(userId: Int, username: String, collectionId: Int, recipeId: Int): Collection {
        val jdbiCollectionModel = checkIfCollectionExists(collectionId)
        if (!checkIfUserIsCollectionOwner(collectionId, userId)) throw NotTheCollectionOwner()

        val jdbiRecipeModel = getJdbiRecipeModel(recipeId)
        if (jdbiCollectionModel.type == CollectionType.FAVOURITE) {
            checkRecipeAccessibility(jdbiRecipeModel.authorUsername, username)
        } else if (jdbiRecipeModel.authorId != userId) throw NotTheRecipeAuthor()

        if (checkIfRecipeInCollection(collectionId, recipeId)) throw RecipeAlreadyInCollection()

        val updatedCollection = tm.run { it.collectionRepository.addRecipeToCollection(collectionId, recipeId) }
        return Collection(
            updatedCollection.id,
            updatedCollection.name,
            updatedCollection.type,
            getRecipesInfo(updatedCollection.recipes)
        )
    }

    fun removeRecipeFromCollection(userId: Int, collectionId: Int, recipeId: Int): Collection {
        checkIfCollectionExists(collectionId)
        if (!checkIfUserIsCollectionOwner(collectionId, userId)) throw NotTheCollectionOwner()

        getJdbiRecipeModel(recipeId)

        if (!checkIfRecipeInCollection(collectionId, recipeId)) throw RecipeNotInCollection()

        val updatedCollection = tm.run { it.collectionRepository.removeRecipeFromCollection(collectionId, recipeId) }
        return Collection(
            updatedCollection.id,
            updatedCollection.name,
            updatedCollection.type,
            getRecipesInfo(updatedCollection.recipes)
        )
    }

    fun deleteCollection(userId: Int, collectionId: Int) {
        checkIfCollectionExists(collectionId)
        if (!checkIfUserIsCollectionOwner(collectionId, userId)) throw NotTheCollectionOwner()
        tm.run { it.collectionRepository.deleteCollection(collectionId) }
    }

    private fun checkIfUserIsCollectionOwner(collectionId: Int, userId: Int) =
        tm.run { it.collectionRepository.checkIfUserIsCollectionOwner(collectionId, userId) }

    private fun checkIfCollectionExists(collectionId: Int) =
        tm.run { it.collectionRepository.getCollectionById(collectionId) } ?: throw CollectionNotFound()

    private fun checkIfOwnedCollectionExists(ownerId: Int, collectionName: String, collectionType: CollectionType) =
        tm.run { it.collectionRepository.getCollection(ownerId, collectionName, collectionType) } != null

    private fun checkCollectionVisibility(userId: Int, username: String, jdbiCollectionModel: JdbiCollectionModel): Boolean {
        if (checkIfUserIsCollectionOwner(jdbiCollectionModel.id, userId)) return true
        if (jdbiCollectionModel.type == CollectionType.FAVOURITE) return false
        val owner = tm.run { it.userRepository.getUserById(jdbiCollectionModel.ownerId) } ?: throw UserNotFound(null)
        return tm.run { it.userRepository.checkUserVisibility(owner.name, username) }
    }

    private fun getJdbiRecipeModel(recipeId: Int): JdbiRecipeModel =
        tm.run { it.recipeRepository.getRecipeById(recipeId) } ?: throw RecipeNotFound()

    private fun getRecipesInfo(recipes: List<JdbiRecipeInfo>): List<RecipeInfo> {
        return recipes.map {
            val recipePicture = cs.pictureRepository.getPicture(it.picturesNames.first(), RECIPES_FOLDER)
            it.toRecipeInfo(recipePicture)
        }
    }

    private fun checkIfRecipeInCollection(collectionId: Int, recipeId: Int) =
        tm.run { it.collectionRepository.checkIfRecipeInCollection(collectionId, recipeId) }

    private fun checkRecipeAccessibility(authorUsername: String, username: String) {
        if (!tm.run { it.userRepository.checkUserVisibility(authorUsername, username) })
            throw RecipeNotAccessible()
    }
}
