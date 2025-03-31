package epicurius.domain.fridge

import org.springframework.stereotype.Component
import java.time.Period
import java.time.ZoneId
import java.util.Date

@Component
class FridgeDomain {
    fun calculateExpirationDate(openDate: Date, duration: Period): Date {
        val instant = openDate.toInstant()

        val localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate()
        val newLocalDate = localDate.plus(duration)

        return Date.from(newLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
    }
}