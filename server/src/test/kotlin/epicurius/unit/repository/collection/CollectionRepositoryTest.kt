package epicurius.unit.repository.collection

import epicurius.domain.PagingParams
import epicurius.domain.collection.CollectionType
import epicurius.unit.repository.RepositoryTest
import epicurius.utils.createTestCollection
import epicurius.utils.createTestRecipe
import epicurius.utils.createTestUser

open class CollectionRepositoryTest : RepositoryTest() {

    companion object {
        val testOwner = createTestUser(tm)
        val testCollectionId = createTestCollection(tm, testOwner.user.id, CollectionType.FAVOURITE)
        val testRecipe = createTestRecipe(tm, fs, testOwner.user)

        fun createCollection(ownerId: Int, collectionName: String, collectionType: CollectionType) =
            tm.run { it.collectionRepository.createCollection(ownerId, collectionName, collectionType) }

        fun getCollection(ownerId: Int, collectionName: String, collectionType: CollectionType) =
            tm.run { it.collectionRepository.getCollection(ownerId, collectionName, collectionType) }

        fun getCollectionById(collectionId: Int) =
            tm.run { it.collectionRepository.getCollectionById(collectionId) }

        fun getCollections(ownerId: Int, collectionType: CollectionType, pagingParams: PagingParams) =
            tm.run { it.collectionRepository.getCollections(ownerId, collectionType, pagingParams) }

        fun updateCollection(collectionId: Int, newName: String?) =
            tm.run { it.collectionRepository.updateCollection(collectionId, newName) }

        fun addRecipeToCollection(collectionId: Int, recipeId: Int) =
            tm.run { it.collectionRepository.addRecipeToCollection(collectionId, recipeId) }

        fun removeRecipeFromCollection(collectionId: Int, recipeId: Int) =
            tm.run { it.collectionRepository.removeRecipeFromCollection(collectionId, recipeId) }

        fun deleteCollection(collectionId: Int) = tm.run { it.collectionRepository.deleteCollection(collectionId) }

        fun checkIfUserIsCollectionOwner(collectionId: Int, userId: Int) =
            tm.run { it.collectionRepository.checkIfUserIsCollectionOwner(collectionId, userId) }

        fun checkIfRecipeInCollection(collectionId: Int, recipeId: Int) =
            tm.run { it.collectionRepository.checkIfRecipeInCollection(collectionId, recipeId) }
    }
}
