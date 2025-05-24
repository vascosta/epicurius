package epicurius.http.controllers.collection.models.input

import epicurius.domain.collection.utils.Companion.COLLECTION_NAME_LENGTH_MSG
import epicurius.domain.collection.utils.Companion.MAX_COLLECTION_NAME_LENGTH
import epicurius.domain.collection.utils.Companion.MIN_COLLECTION_NAME_LENGTH
import jakarta.validation.constraints.Size

data class UpdateCollectionInputModel(
    @field:Size(min = MIN_COLLECTION_NAME_LENGTH, max = MAX_COLLECTION_NAME_LENGTH, message = COLLECTION_NAME_LENGTH_MSG)
    val name: String?,
)
