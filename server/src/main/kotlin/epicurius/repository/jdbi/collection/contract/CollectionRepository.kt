package epicurius.repository.jdbi.collection.contract

import epicurius.domain.collection.CollectionType
import epicurius.http.collection.models.input.CreateCollectionInputModel
import epicurius.http.collection.models.input.UpdateCollectionInputModel
import epicurius.repository.jdbi.collection.models.JdbiCollectionModel
import epicurius.repository.jdbi.collection.models.JdbiCreateCollectionModel
import epicurius.repository.jdbi.collection.models.JdbiUpdateCollectionModel

interface CollectionRepository {

    fun createCollection(ownerId: Int, createCollectionInfo: CreateCollectionInputModel): Int

    fun getCollection(ownerId: Int, collectionName: String, collectionType: CollectionType): JdbiCollectionModel?
    fun getCollectionById(collectionId: Int): JdbiCollectionModel?

    fun updateCollection(collectionId: Int, updateCollectionInfo: UpdateCollectionInputModel): JdbiCollectionModel

    fun addRecipeToCollection(collectionId: Int, recipeId: Int): JdbiCollectionModel
    fun removeRecipeFromCollection(collectionId: Int, recipeId: Int): JdbiCollectionModel

    fun deleteCollection(collectionId: Int)
}