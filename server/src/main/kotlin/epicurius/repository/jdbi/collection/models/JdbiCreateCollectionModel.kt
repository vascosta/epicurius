package epicurius.repository.jdbi.collection.models

import epicurius.domain.collection.CollectionType

data class JdbiCreateCollectionModel(val name: String, val type: CollectionType)
