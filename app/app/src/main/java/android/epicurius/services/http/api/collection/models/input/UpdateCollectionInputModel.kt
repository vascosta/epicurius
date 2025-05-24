package epicurius.http.controllers.collection.models.input

import android.epicurius.domain.collection.CollectionDomain.Companion.COLLECTION_NAME_LENGTH_MSG
import android.epicurius.domain.collection.CollectionDomain.Companion.MAX_COLLECTION_NAME_LENGTH
import android.epicurius.domain.collection.CollectionDomain.Companion.MIN_COLLECTION_NAME_LENGTH
import jakarta.validation.constraints.Size

data class UpdateCollectionInputModel(
    @field:Size(min = MIN_COLLECTION_NAME_LENGTH, max = MAX_COLLECTION_NAME_LENGTH, message = COLLECTION_NAME_LENGTH_MSG)
    val name: String?,
)
