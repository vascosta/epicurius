package epicurius.repository.jdbi.collection.models

import epicurius.domain.collection.CollectionProfile

class JdbiCollectionProfileModel(val id: Int, val name: String) {

    fun toCollectionProfile() = CollectionProfile(id, name)
}