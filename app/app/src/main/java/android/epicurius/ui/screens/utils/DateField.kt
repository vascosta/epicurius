package android.epicurius.ui.screens.utils

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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DateField(
    label: String,
    enabled: () -> Boolean = { true },
    initialDate: LocalDate? = null,
    onDateSelected: (LocalDate?) -> Unit
) {
    val context = LocalContext.current
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    var selectedDate by rememberSaveable { mutableStateOf(initialDate) }
    var text by rememberSaveable { mutableStateOf(initialDate?.format(formatter) ?: "") }

    LaunchedEffect(initialDate) {
        selectedDate = initialDate
        text = initialDate?.format(formatter) ?: ""
    }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val picked = LocalDate.of(year, month, dayOfMonth)
            selectedDate = picked
            text = picked.format(formatter)
            onDateSelected(picked)
        },
        (selectedDate ?: LocalDate.now()).year,
        (selectedDate ?: LocalDate.now()).monthValue - 1,
        (selectedDate ?: LocalDate.now()).dayOfMonth
    )

    OutlinedTextField(
        value = text,
        onValueChange = { },
        enabled = enabled(),
        readOnly = true,
        label = { Text(label) },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Pick date",
                modifier = Modifier.clickable { datePickerDialog.show() }
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled()) { datePickerDialog.show() }
    )
}
