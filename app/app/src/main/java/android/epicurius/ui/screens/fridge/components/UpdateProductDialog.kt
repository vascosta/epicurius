package android.epicurius.ui.screens.fridge.components

import android.epicurius.ui.screens.utils.DateField
import android.epicurius.ui.screens.utils.NumberTextField
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.Period

@Composable
fun UpdateProductDialog(
    onDismiss: () -> Unit,
    onUpdateProduct: (Int?, LocalDate?, Period?, LocalDate?) -> Unit,
) {
    var quantity by rememberSaveable { mutableStateOf<Int?>(null) }
    var openDate by rememberSaveable { mutableStateOf<LocalDate?>(null) }
    var duration by rememberSaveable { mutableStateOf<Period?>(null) }
    var expirationDate by rememberSaveable { mutableStateOf<LocalDate?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Product") },
        text = {
            Column {
                NumberTextField(
                    value = quantity?.toString() ?: "",
                    enabled = openDate == null,
                    label = "New quantity",
                    onValueChange = { quantity = it.toIntOrNull() }
                )
                Spacer(modifier = Modifier.height(8.dp))
                DateField(
                    label = "Opened Date",
                    enabled = { quantity == null && expirationDate == null },
                    onDateSelected = { openDate = it }
                )
                Spacer(modifier = Modifier.height(8.dp))
                NumberTextField(
                    value = (duration?.days ?: "").toString(),
                    label = "Duration (days)",
                    enabled = openDate != null && expirationDate == null && quantity == null,
                    onValueChange = { newValue ->
                        duration = newValue.toIntOrNull()?.let { Period.ofDays(it) }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                DateField(
                    label = "Expiration Date",
                    enabled = { openDate == null && duration == null },
                    initialDate = expirationDate,
                    onDateSelected = { expirationDate = it }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onUpdateProduct(
                    quantity,
                    openDate,
                    duration,
                    expirationDate
                )
                onDismiss()
            }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
