package epicurius.domain.exceptions

class InvalidProduct : Exception("Inserted product is invalid")
class DurationIsNull : Exception("If product is open then duration can't be null")
class ProductNotFound(entryNumber: Int) : Exception("Product with entry number $entryNumber not found")
class ProductIsAlreadyOpen : Exception("Product is already open, expiration date can't be changed")
