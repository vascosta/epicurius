package android.epicurius.services.api.fridge.input

import java.time.LocalDate

data class AddProductInputModel(
    val name: String,
    val quantity: Int,
    val openDate: LocalDate? = null,
    val expirationDate: LocalDate
)
