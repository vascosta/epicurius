package epicurius.http.collection.models.output

import epicurius.domain.collection.Collection

data class CreateCollectionOutputModel(val collection: Collection)

typealias GetCollectionOutputModel = CreateCollectionOutputModel

typealias UpdateCollectionOutputModel = CreateCollectionOutputModel
