package epicurius.domain.fridge

import java.time.LocalDate

data class UpdateProductInfo(
    val entryNumber: Int,
    val quantity: Int? = null,
    val openDate: LocalDate? = null,
    val expirationDate: LocalDate? = null
)
