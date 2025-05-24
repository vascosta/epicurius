package epicurius.http.controllers.fridge.models.input

import epicurius.domain.fridge.FridgeDomain
import epicurius.domain.fridge.ProductInfo
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Pattern
import java.time.LocalDate

data class ProductInputModel(
    @field:NotBlank
    @field:Pattern(regexp = FridgeDomain.VALID_STRING, message = FridgeDomain.VALID_STRING_MSG)
    val name: String,

    @field:Min(1)
    @field:Max(20)
    val quantity: Int,

    @field:PastOrPresent
    val openDate: LocalDate? = null,

    @field:NotNull
    @field:Future
    val expirationDate: LocalDate
) {
    fun toProductInfo() = ProductInfo(
        name = name,
        quantity = quantity,
        openDate = openDate,
        expirationDate = expirationDate
    )
}
