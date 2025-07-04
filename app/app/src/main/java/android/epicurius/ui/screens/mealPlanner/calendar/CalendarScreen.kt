package android.epicurius.ui.screens.mealPlanner.calendar

import android.epicurius.ui.navigation.BottomBar
import android.epicurius.ui.navigation.TopBar
import android.epicurius.ui.screens.mealPlanner.components.WeekCalendarRow
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.VerticalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.DayPosition
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onWeeklyMealPlannerRequest: () -> Unit = {},
    onDailyMealPlannerRequest: (date: LocalDate) -> Unit = {},
) {
    val beginDate = LocalDate.of(2025, 6, 2)
    val today = LocalDate.now()
    val startMonth = YearMonth.from(beginDate)
    val endMonth = YearMonth.from(today.plusMonths(1))

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstDayOfWeek = DayOfWeek.MONDAY,
    )

    LaunchedEffect(Unit) {
        state.scrollToMonth(YearMonth.from(today))
    }
    Scaffold(
        topBar = { TopBar("Meal Planner", enableButtons = true) },
        bottomBar = { BottomBar(buttonsEnable = true) },
        content = { paddingValues ->
            VerticalCalendar(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .background(Color.White),
                state = state,
                calendarScrollPaged = true,
                dayContent = { day ->
                    if (day.position == DayPosition.MonthDate) {
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .background(
                                    if (day.date == today) Color(0xFFCDFA7D) else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { onDailyMealPlannerRequest(day.date) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = day.date.dayOfMonth.toString())
                        }
                    }
                },
                monthHeader = { month ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = month.yearMonth.month.name,
                            modifier = Modifier.padding(8.dp),
                            color = Color.Black
                        )
                        if (month.yearMonth.month == today.month) {
                            TextButton(
                                onClick = { onWeeklyMealPlannerRequest() }
                            ) { Text("Weekly Planner") }
                        }
                    }
                    WeekCalendarRow()
                }
            )
        },
        containerColor = Color.White
    )
}


@Preview
@Composable
fun CalendarScreenPreview() {
    CalendarScreen()
}
