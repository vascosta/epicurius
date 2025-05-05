package epicurius.domain.exceptions

class CollectionAlreadyExists : RuntimeException("Collection already exists")
class CollectionNotAccessible : RuntimeException("Collection not accessible")
class CollectionNotFound : RuntimeException("Collection not found")

class NotTheOwnerOfCollection : RuntimeException("You are not the owner of this collection")