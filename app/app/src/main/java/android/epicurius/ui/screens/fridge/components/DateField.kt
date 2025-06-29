package android.epicurius.ui.screens.fridge.components

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DateField(
    initialDate: LocalDate? = null,
    onSelectDate: (date: LocalDate?) -> Unit,
    enabled: Boolean,
    label: String
) {
    val context = LocalContext.current

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    var selectedDate by remember { mutableStateOf(initialDate) }
    var text by remember { mutableStateOf(initialDate?.format(formatter) ?: "") }

    LaunchedEffect(initialDate) { // check if needed
        selectedDate = initialDate
        text = initialDate?.format(formatter) ?: ""
    }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val picked = LocalDate.of(year, month + 1, dayOfMonth)
            selectedDate = picked
            text = picked.format(formatter)
            onSelectDate(picked)
        },
        (selectedDate ?: LocalDate.now()).year,
        (selectedDate ?: LocalDate.now()).monthValue - 1,
        (selectedDate ?: LocalDate.now()).dayOfMonth
    )

    OutlinedTextField(
        value = text,
        onValueChange = { },
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { datePickerDialog.show() },
        readOnly = true,
        label = { Text(label) },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Pick date",
                modifier = Modifier.clickable(enabled = enabled) { datePickerDialog.show() }
            )
        },
    )
}
