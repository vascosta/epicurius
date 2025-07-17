package android.epicurius.ui.screens.mealPlanner.components

import android.epicurius.ui.screens.theme.DarkPurple
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek

@Composable
fun WeekCalendarRow() {
    Row(Modifier.fillMaxWidth()) {
        DayOfWeek.entries.forEach { dayOfWeek ->
            Text(
                text = dayOfWeek.name.take(3),
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color = DarkPurple
            )
        }
    }
}
