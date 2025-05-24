package android.epicurius.domain.fridge

import java.time.LocalDate

data class ProductInfo(
    val productName: String,
    val quantity: Int,
    val openDate: LocalDate?,
    val expirationDate: LocalDate
)
