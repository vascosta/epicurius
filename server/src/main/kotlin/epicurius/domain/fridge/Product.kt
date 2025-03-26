package epicurius.domain.fridge

import java.util.*

data class Product (
    val productName: String,
    val quantity: Int,
    val openDate: Date?,
    val expirationDate: Date
)