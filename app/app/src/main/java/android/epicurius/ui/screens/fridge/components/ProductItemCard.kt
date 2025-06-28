package android.epicurius.ui.screens.fridge.components

import android.epicurius.domain.fridge.Product
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.Period

@Composable
fun ProductItemCard(
    product: Product,
    onUpdateProduct: (
        entryNumber: Int,
        quantity: Int?,
        openDate: LocalDate?,
        duration: Period?,
        expirationDate: LocalDate?
    ) -> Unit = { _, _, _, _, _ -> },
    onDeleteProduct: (entryNumber: Int) -> Unit = {},
    enableButtons: Boolean
) {
    var showUpdateProductDialog by remember { mutableStateOf(false) }

    val isExpiringSoon by remember { mutableStateOf(product.expirationDate.isBefore(LocalDate.now().plusDays(3))) }
    val expired by remember { mutableStateOf(product.expirationDate.isBefore(LocalDate.now())) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                expired -> Color(0xFFFFCDD2)
                isExpiringSoon -> Color(0xFFFFF9C4)
                else -> Color.White
            }
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = product.name, style = MaterialTheme.typography.titleMedium)
            Text(text = "Quantity: ${product.quantity}")
            product.openDate?.let { Text(text = "Opened date: $it") }
            Text(
                text = "Expiration date: ${product.expirationDate}",
                color = if (expired) Color.Red else Color.Unspecified,
                fontWeight = if (expired) {
                    FontWeight.Bold
                } else FontWeight.Normal
            )
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = { showUpdateProductDialog = true },
                    enabled = enableButtons
                ) { Icon(Icons.Default.Edit, contentDescription = "Update Product") }
                IconButton(
                    onClick = { onDeleteProduct(product.entryNumber) },
                    enabled = enableButtons
                ) { Icon(Icons.Default.Delete, contentDescription = "Delete") }
            }

            if (showUpdateProductDialog) {
                UpdateProductDialog(
                    onUpdateProduct = { quantity, openDate, duration, expirationDate ->
                        onUpdateProduct(product.entryNumber, quantity, openDate, duration, expirationDate)
                        showUpdateProductDialog = false
                    },
                    onDismiss = { showUpdateProductDialog = false },
                    enableButtons = enableButtons
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewProductItemCard() {
    val sampleProduct = Product(
        name = "Milk",
        quantity = 1,
        entryNumber = 1,
        openDate = LocalDate.now().minusDays(1),
        expirationDate = LocalDate.now().plusDays(5)
    )

    ProductItemCard(
        product = sampleProduct,
        enableButtons = true
    )
}
