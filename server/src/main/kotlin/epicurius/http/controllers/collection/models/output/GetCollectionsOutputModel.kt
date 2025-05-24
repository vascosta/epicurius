package epicurius.http.controllers.collection.models.output

import epicurius.domain.collection.CollectionProfile

data class GetCollectionsOutputModel(val collections: List<CollectionProfile>)