package android.epicurius.ui.screens.fridge

import android.epicurius.domain.fridge.Product
import android.epicurius.ui.navigation.BottomBar
import android.epicurius.ui.navigation.TopBar
import android.epicurius.ui.screens.fridge.components.AddProductDialog
import android.epicurius.ui.screens.fridge.components.ProductItemCard
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.Period

@Composable
fun FridgeScreen(
    products: List<Product>,
    onBackButton: () -> Unit = {},
    onAddProduct: (String, Int, LocalDate?, LocalDate) -> Unit = { _, _, _, _ -> },
    onUpdateProduct: (Int?, LocalDate?, Period?, LocalDate?) -> Unit = { _, _, _, _ -> },
    onDeleteProduct: (Int) -> Unit = { _ -> }
) {
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopBar(
                titleText = "Fridge",
                backButton = true,
                onBackButton = onBackButton,
                enableButtons = true
            )
        },
        bottomBar = { BottomBar(buttonsEnable = true) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Product")
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(16.dp)
                .background(Color.White),
            contentPadding = paddingValues
        ) {
            items(products) { product ->
                ProductItemCard(
                    product = product,
                    onDelete = onDeleteProduct,
                    onUpdateProduct = onUpdateProduct
                )
            }
        }
    }

    if (showDialog) {
        AddProductDialog(
            onAddProduct = onAddProduct,
            onDismiss = { showDialog = false }
        )
    }
}

@Preview
@Composable
fun PreviewFridgeScreen() {
    val sampleProducts = listOf(
        Product("Milk", 1, 2, LocalDate.now().minusDays(1), LocalDate.now().plusDays(5)),
        Product("Eggs", 2, 12, null, LocalDate.now().plusDays(10)),
        Product("Meat", 3, 1, LocalDate.now().minusDays(2), LocalDate.now().minusDays(1)),
        Product("Cheese", 4, 1, LocalDate.now().minusDays(3), LocalDate.now().plusDays(2)),
        Product("Yogurt", 5, 4, null, LocalDate.now().plusDays(1)),
    )

    FridgeScreen(
        products = sampleProducts
    )
}
