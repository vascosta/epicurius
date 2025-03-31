package epicurius.domain.fridge

import java.util.Date

data class UpdateProductInfo(
    val entryNumber: Int,
    val quantity: Int? = null,
    val openDate: Date? = null,
    val expirationDate: Date? = null
)
