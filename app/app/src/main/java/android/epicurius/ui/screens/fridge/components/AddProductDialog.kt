package android.epicurius.ui.screens.fridge.components

import android.epicurius.ui.screens.utils.TextField
import android.epicurius.ui.screens.utils.dropdownMenu.SearchDropdownMenuComponent
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
    onSearchProduct: (partialName: String) -> List<String>,
    onAddProduct: (
        name: String,
        quantity: Int,
        openDate: LocalDate?,
        expirationDate: LocalDate
    ) -> Unit = { _, _, _, _ -> },
    onDismiss: () -> Unit,
    enableButtons: Boolean
) {
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableIntStateOf(0) }
    var openDate by remember { mutableStateOf<LocalDate?>(null) }
    var expirationDate by remember { mutableStateOf<LocalDate?>(null) }

    var productList by remember { mutableStateOf(emptyList<String>()) }
    var selectedProduct by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { if (enableButtons) onDismiss() },
        confirmButton = {
            TextButton(
                onClick = {
                    val safeExpiration = expirationDate
                    if (safeExpiration != null) { // other validations are on the enabled parameter
                        onAddProduct(name, quantity, openDate, safeExpiration)
                        onDismiss()
                    }
                },
                enabled =
                    enableButtons &&
                    name.isNotBlank() && quantity > 0 && expirationDate != null &&
                    selectedProduct
            ) { Text("Add") }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = enableButtons
            ) { Text("Cancel") }
        },
        title = { Text("Add new product") },
        text = {
            Column {
                SearchDropdownMenuComponent(
                    options = productList,
                    value = name,
                    onValueChange = {
                        name = it
                        selectedProduct = productList.contains(name)
                    },
                    onIconClick = { productList = onSearchProduct(name) },
                    enabled = enableButtons
                )
                TextField(
                    value = if (quantity == 0) "" else quantity.toString(),
                    onValueChange = { quantity = it.toIntOrNull() ?: 0 },
                    enabled = true,
                    label = "Quantity"
                )
                DateField(
                    onSelectDate = { openDate = it },
                    enabled = enableButtons,
                    label = "Opened Date"
                )
                DateField(
                    initialDate = expirationDate,
                    onSelectDate = { expirationDate = it },
                    enabled = enableButtons,
                    label = "Expiration Date"
                )
            }
        },
    )
}
