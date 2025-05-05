package epicurius.domain.exceptions

class CollectionAlreadyExists : RuntimeException("Collection already exists")
class CollectionNotAccessible : RuntimeException("Collection not accessible")
class CollectionNotFound : RuntimeException("Collection not found")

class RecipeAlreadyInCollection : RuntimeException("Recipe already in collection")
class RecipeNotInCollection : RuntimeException("Recipe not in collection")

class NotTheOwnerOfCollection : RuntimeException("You are not the owner of this collection")
