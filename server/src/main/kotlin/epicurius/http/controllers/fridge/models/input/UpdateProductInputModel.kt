package epicurius.http.controllers.fridge.models.input

import epicurius.domain.exceptions.InvalidExpiration
import epicurius.domain.exceptions.InvalidQuantity
import epicurius.domain.exceptions.OpenDateIsNull
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.PastOrPresent
import java.time.LocalDate
import java.time.Period

data class UpdateProductInputModel(
    @field:Min(1)
    @field:Max(20)
    val quantity: Int? = null,

    @field:PastOrPresent
    val openDate: LocalDate? = null,

    val duration: Period? = null,

    @field:Future
    val expirationDate: LocalDate? = null
) {
    init {
        if (quantity != null && openDate != null) throw InvalidQuantity()
        if (openDate == null && duration != null) throw OpenDateIsNull()
        if (openDate != null && expirationDate != null) throw InvalidExpiration()
    }
}
