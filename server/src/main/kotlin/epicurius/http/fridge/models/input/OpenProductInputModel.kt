package epicurius.http.fridge.models.input

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PastOrPresent
import java.time.LocalDate
import java.time.Period

data class OpenProductInputModel(
    @field:NotNull
    @field:PastOrPresent
    val openDate: LocalDate,

    @field:NotNull
    val duration: Period,
)
