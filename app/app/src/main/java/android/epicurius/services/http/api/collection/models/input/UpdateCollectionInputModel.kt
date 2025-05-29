package epicurius.http.controllers.collection.models.input

import android.epicurius.domain.collection.COLLECTION_NAME_LENGTH_MSG
import android.epicurius.domain.collection.MAX_COLLECTION_NAME_LENGTH
import android.epicurius.domain.collection.MIN_COLLECTION_NAME_LENGTH
import androidx.annotation.Size

data class UpdateCollectionInputModel(
    @field:Size(
        min = MIN_COLLECTION_NAME_LENGTH.toLong(),
        max = MAX_COLLECTION_NAME_LENGTH.toLong()
    )
    val name: String?,
) {
    init {
        require(name == null || (name.length in MIN_COLLECTION_NAME_LENGTH..MAX_COLLECTION_NAME_LENGTH)) {
            COLLECTION_NAME_LENGTH_MSG
        }
    }
}
