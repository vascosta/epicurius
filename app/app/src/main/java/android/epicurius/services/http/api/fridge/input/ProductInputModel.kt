package android.epicurius.services.http.api.fridge.input

import java.time.LocalDate

data class ProductInputModel(
    val productName: String,
    val quantity: Int,

    val openDate: LocalDate? = null,

    val expirationDate: LocalDate
)
