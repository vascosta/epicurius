package epicurius.http.fridge.models.input

import jakarta.validation.constraints.Future
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PastOrPresent
import java.util.Date

data class ProductInputModel(
    @field:NotBlank
    val productName: String,

    @field:Min(1)
    @field:Max(20)
    val quantity: Int,

    @field:PastOrPresent
    val openDate: Date? = null,

    @field:NotNull
    @field:Future
    val expirationDate: Date
)
