package epicurius.http.controllers.collection.models.input

import epicurius.domain.collection.COLLECTION_NAME_LENGTH_MSG
import epicurius.domain.collection.CollectionType
import epicurius.domain.collection.MAX_COLLECTION_NAME_LENGTH
import epicurius.domain.collection.MIN_COLLECTION_NAME_LENGTH
import jakarta.validation.constraints.Size

data class CreateCollectionInputModel(
    @field:Size(min = MIN_COLLECTION_NAME_LENGTH, max = MAX_COLLECTION_NAME_LENGTH, message = COLLECTION_NAME_LENGTH_MSG)
    val name: String,
    val type: CollectionType
)
