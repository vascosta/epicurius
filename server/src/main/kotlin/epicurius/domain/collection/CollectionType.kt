package epicurius.domain.collection

enum class CollectionType {
    FAVOURITE,
    KITCHEN_BOOK;

    companion object {
        fun fromInt(value: Int): CollectionType {
            return CollectionType.entries.getOrNull(value) ?:
                throw IllegalArgumentException("Invalid value for CollectionType: $value")
        }
    }
}