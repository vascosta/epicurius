package android.epicurius.ui.screens.fridge.components

import android.epicurius.ui.screens.utils.DateField
import android.epicurius.ui.screens.utils.TextField
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import java.time.LocalDate

@Composable
fun AddProductDialog(
    onAddProduct: (String, Int, LocalDate?, LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableIntStateOf(0) }
    var openDate by remember { mutableStateOf<LocalDate?>(null) }
    var expirationDate by remember { mutableStateOf<LocalDate?>(null) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Add new product") },
        text = {
            Column {
                TextField(
                    value = name,
                    label = "Product Name",
                    onValueChange = { name = it }
                )
                TextField(
                    value = if (quantity == 0) "" else quantity.toString(),
                    label = "Quantity",
                    onValueChange = { quantity = it.toIntOrNull() ?: 0 }
                )
                DateField(
                    label = "Opened Date",
                    onDateSelected = { openDate = it }
                )
                DateField(
                    label = "Expiration Date",
                    initialDate = expirationDate,
                    onDateSelected = { expirationDate = it }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val safeExpiration = expirationDate
                if (name.isNotBlank() && quantity > 0 && safeExpiration != null) {
                    onAddProduct(name, quantity, openDate, safeExpiration)
                    onDismiss()
                }
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}
