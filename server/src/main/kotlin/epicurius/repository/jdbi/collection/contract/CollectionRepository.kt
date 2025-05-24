package epicurius.repository.jdbi.collection.contract

import epicurius.domain.PagingParams
import epicurius.domain.collection.CollectionType
import epicurius.repository.jdbi.collection.models.JdbiCollectionModel
import epicurius.repository.jdbi.collection.models.JdbiCollectionProfileModel

interface CollectionRepository {

    fun createCollection(ownerId: Int, collectionName: String, collectionType: CollectionType): Int

    fun getCollection(ownerId: Int, collectionName: String, collectionType: CollectionType): JdbiCollectionModel?
    fun getCollectionById(collectionId: Int): JdbiCollectionModel?
    fun getCollections(userId: Int, collectionType: CollectionType, pagingParams: PagingParams): List<JdbiCollectionProfileModel>

    fun updateCollection(collectionId: Int, newName: String?): JdbiCollectionModel

    fun addRecipeToCollection(collectionId: Int, recipeId: Int): JdbiCollectionModel
    fun removeRecipeFromCollection(collectionId: Int, recipeId: Int): JdbiCollectionModel

    fun deleteCollection(collectionId: Int)

    fun checkIfUserIsCollectionOwner(collectionId: Int, userId: Int): Boolean
    fun checkIfRecipeInCollection(collectionId: Int, recipeId: Int): Boolean
}
