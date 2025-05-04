package epicurius.domain.collection

class CollectionDomain {

    companion object {
        const val MIN_COLLECTION_NAME_LENGTH = 3
        const val MAX_COLLECTION_NAME_LENGTH = 20
        const val COLLECTION_NAME_LENGTH_MSG = "must be between $MIN_COLLECTION_NAME_LENGTH and $MAX_COLLECTION_NAME_LENGTH characters"
    }
}