package epicurius.http.fridge.models.input

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PastOrPresent
import java.time.Period
import java.util.Date

data class OpenProductInputModel(
    @field:NotNull
    @field:PastOrPresent
    val openDate: Date,

    @field:NotNull
    val duration: Period,
)
