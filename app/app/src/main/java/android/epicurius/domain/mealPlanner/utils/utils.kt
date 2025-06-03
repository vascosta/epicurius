package android.epicurius.domain.mealPlanner.utils

import java.time.DayOfWeek
import java.time.LocalDate

fun getWeek(date: LocalDate): List<LocalDate> {
    val daysFromStartOfWeek = (7 + date.dayOfWeek.value - DayOfWeek.MONDAY.value) % 7
    val startOfWeek = date.minusDays(daysFromStartOfWeek.toLong())

    return (0..6).map { startOfWeek.plusDays(it.toLong()) }
}
