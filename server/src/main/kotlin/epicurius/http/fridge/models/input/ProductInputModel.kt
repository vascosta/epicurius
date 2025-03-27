package epicurius.http.fridge.models.input

import epicurius.http.utils.Regex
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Pattern
import java.util.Date

data class ProductInputModel(
    @field:NotBlank
    @field:Pattern(regexp = Regex.VALID_STRING, message = Regex.VALID_STRING_MSG)
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
