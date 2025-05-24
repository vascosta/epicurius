package android.epicurius.services.http.api.collection.models.input

import epicurius.domain.collection.CollectionType

data class CreateCollectionInputModel(val name: String, val type: CollectionType)
