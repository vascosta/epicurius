package android.epicurius.services.http.api.collection.models.output

import android.epicurius.domain.collection.Collection

data class CreateCollectionOutputModel(val collection: Collection)

typealias GetCollectionOutputModel = CreateCollectionOutputModel

typealias UpdateCollectionOutputModel = CreateCollectionOutputModel

typealias AddRecipeToCollectionOutputModel = CreateCollectionOutputModel

typealias RemoveRecipeFromCollectionOutputModel = CreateCollectionOutputModel
