package android.epicurius.services.http.api.fridge.input

import java.time.LocalDate
import java.time.Period

data class UpdateProductInputModel(
    val quantity: Int? = null,

    val openDate: LocalDate? = null,

    val duration: Period? = null,

    val expirationDate: LocalDate? = null
)
