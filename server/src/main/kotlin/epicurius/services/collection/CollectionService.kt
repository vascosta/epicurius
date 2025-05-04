package epicurius.services.collection

import epicurius.domain.collection.Collection
import epicurius.domain.collection.CollectionType
import epicurius.domain.exceptions.CollectionAlreadyExists
import epicurius.http.collection.models.input.CreateCollectionInputModel
import epicurius.repository.transaction.TransactionManager
import org.springframework.stereotype.Component

@Component
class CollectionService(private val tm: TransactionManager) {

    fun createCollection(ownerId: Int, createCollectionInfo: CreateCollectionInputModel): Collection {
        if (checkIfCollectionAlreadyExists(ownerId, createCollectionInfo.name, createCollectionInfo.type) != null) {
            throw CollectionAlreadyExists()
        }
        val collectionId = tm.run { it.collectionRepository.createCollection(ownerId, createCollectionInfo) }

        return Collection(
            collectionId,
            createCollectionInfo.name,
            createCollectionInfo.type,
            emptyList()
        )
    }


    private fun checkIfCollectionAlreadyExistsById(collectionId: Int) =
        tm.run { it.collectionRepository.getCollectionById(collectionId) }

    private fun checkIfCollectionAlreadyExists(ownerId: Int, collectionName: String, collectionType: CollectionType) =
        tm.run { it.collectionRepository.getCollection(ownerId, collectionName, collectionType) }

}
