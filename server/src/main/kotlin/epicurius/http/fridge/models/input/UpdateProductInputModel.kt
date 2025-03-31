package epicurius.http.fridge.models.input

import jakarta.validation.constraints.Future
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import java.util.Date

data class UpdateProductInputModel(
    @field:Min(1)
    @field:Max(20)
    val quantity: Int? = null,

    @field:Future
    val expirationDate: Date? = null
)
