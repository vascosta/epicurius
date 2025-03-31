package epicurius.domain.fridge

import java.util.Date

data class ProductInfo(
    val productName: String,
    val quantity: Int,
    val openDate: Date?,
    val expirationDate: Date
)
