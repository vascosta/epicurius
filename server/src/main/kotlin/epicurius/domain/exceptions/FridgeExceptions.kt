package epicurius.domain.exceptions

class InvalidProduct : RuntimeException("Inserted product is invalid")
class DurationIsNull : RuntimeException("If product is open then duration can't be null")
class ProductNotFound(entryNumber: Int) : RuntimeException("Product with entry number $entryNumber not found")
class ProductIsAlreadyOpen : RuntimeException("Product is already open, expiration date can't be changed")
