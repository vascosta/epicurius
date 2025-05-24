package android.epicurius.services.http.api.collection.models.output

import android.epicurius.domain.collection.CollectionProfile

data class GetCollectionsOutputModel(val collections: List<CollectionProfile>)