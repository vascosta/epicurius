package epicurius.domain.fridge

import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.Period

@Component
class FridgeDomain {
    fun calculateExpirationDate(openDate: LocalDate, duration: Period): LocalDate = openDate.plus(duration)

    companion object {
        const val VALID_STRING = "^[A-Za-zÀ-ÿ\\s]+$"
        const val VALID_STRING_MSG = "Product can only contain letters"
    }
}
