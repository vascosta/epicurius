package epicurius.domain.fridge

import java.time.LocalDate

data class ProductInfo(
    val name: String,
    val quantity: Int,
    val openDate: LocalDate?,
    val expirationDate: LocalDate
) {
    fun toProduct(entryNumber: Int) = Product(name, entryNumber, quantity, openDate, expirationDate)
}
