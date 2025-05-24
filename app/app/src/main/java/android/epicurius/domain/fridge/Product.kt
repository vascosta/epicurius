package android.epicurius.domain.fridge

import java.time.LocalDate

data class Product(
    val name: String,
    val entryNumber: Int,
    val quantity: Int,
    val openDate: LocalDate?,
    val expirationDate: LocalDate
)
