package android.epicurius.ui.screens.fridge.components

import android.epicurius.ui.screens.utils.NumberTextField
import android.epicurius.ui.screens.utils.isValidForNumberTextField
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.Period

@Composable
fun UpdateProductDialog(
    onUpdateProduct: (
        quantity: Int?,
        openDate: LocalDate?,
        duration: Period?,
        expirationDate: LocalDate?
    ) -> Unit = { _, _, _, _ -> },
    onDismiss: () -> Unit = {},
    enableButtons: Boolean
) {
    var quantity by remember { mutableStateOf<Int?>(null) }
    var openDate by remember { mutableStateOf<LocalDate?>(null) }
    var duration by remember { mutableStateOf<Period?>(null) }
    var expirationDate by remember { mutableStateOf<LocalDate?>(null) }

    AlertDialog(
        onDismissRequest = { if (enableButtons) onDismiss() },
        confirmButton = {
            TextButton(
                onClick = {
                    onUpdateProduct(
                        quantity,
                        openDate,
                        duration,
                        expirationDate
                    )
                    onDismiss()
                },
                enabled = enableButtons
            ) { Text("Confirm") }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = enableButtons
            ) { Text("Cancel") }
        },
        title = { Text("Update Product") },
        text = {
            Column {
                NumberTextField(
                    value = (quantity ?: "").toString(),
                    onValueChange = { if (isValidForNumberTextField(it)) quantity = it.toInt() },
                    enabled = enableButtons && openDate == null,
                    label = "New quantity",
                )
                Spacer(modifier = Modifier.height(8.dp))
                DateField(
                    onSelectDate = { openDate = it },
                    enabled = enableButtons && quantity == null && expirationDate == null,
                    label = "Opened Date"
                )
                Spacer(modifier = Modifier.height(8.dp))
                NumberTextField(
                    value = (duration?.days ?: "").toString(),
                    label = "Duration (days)",
                    enabled = enableButtons && openDate != null && expirationDate == null && quantity == null,
                    onValueChange = { newValue ->
                        if (isValidForNumberTextField(newValue))
                            duration = newValue.toIntOrNull()?.let { Period.ofDays(it) }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                DateField(
                    initialDate = expirationDate,
                    onSelectDate = { expirationDate = it },
                    enabled = enableButtons && openDate == null && duration == null,
                    label = "Expiration Date",
                )
            }
        }
    )
}
