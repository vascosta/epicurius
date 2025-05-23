package epicurius.domain.exceptions

class InvalidProduct : RuntimeException("Inserted product is invalid")
class OpenDateIsNull : RuntimeException("If duration is given then open date can't be null")
class InvalidExpiration : RuntimeException("If open date is given then expiration date cannot be changed")
class InvalidQuantity : RuntimeException("Can not update quantity when opening a product")
class ProductNotFound(entryNumber: Int) : RuntimeException("Product with entry number $entryNumber not found")
class ProductIsAlreadyOpen : RuntimeException("Product is already open, expiration date can't be changed")
