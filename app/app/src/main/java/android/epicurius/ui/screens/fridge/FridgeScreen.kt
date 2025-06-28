package android.epicurius.ui.screens.fridge

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.epicurius.domain.fridge.Product
import android.epicurius.ui.navigation.BottomBar
import android.epicurius.ui.navigation.TopBar
import android.epicurius.ui.screens.fridge.components.AddProductDialog
import android.epicurius.ui.screens.fridge.components.ProductItemCard
import android.epicurius.ui.screens.fridge.notifications.createNotificationChannelIfNeeded
import android.epicurius.ui.screens.fridge.notifications.notifyProductExpiration
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.LoadStateRenderer
import android.epicurius.ui.screens.utils.Loaded
import android.epicurius.ui.screens.utils.apiSuccess
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import java.time.LocalDate
import java.time.Period

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun FridgeScreen(
    userFridgeState: LoadState<List<Product>>,
    onBackButton: () -> Unit = {},
    onAddProduct: (
        name: String,
        quantity: Int,
        openDate: LocalDate?,
        expirationDate: LocalDate
    ) -> Unit = { _, _, _, _ -> },
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
    var showAddProductDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    NotificationManagerCompat.from(context)

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            createNotificationChannelIfNeeded(context)
        }
    }

    val isNotificationPermissionGranted =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // older versions do not require runtime permission
        }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            createNotificationChannelIfNeeded(context)
        }
    }

    BoxWithConstraints {
        val maxHeight = constraints.maxHeight.toFloat()

        var offsetX by remember { mutableFloatStateOf(0f) }
        var offsetY by remember { mutableFloatStateOf(0f) }

        Scaffold(
            topBar = {
                TopBar(
                    titleText = "Fridge",
                    backButton = true,
                    onBackButton = onBackButton,
                    enableButtons = enableButtons
                )
            },
            bottomBar = { BottomBar(buttonsEnable = enableButtons) },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { if (enableButtons) showAddProductDialog = true },
                    modifier = Modifier
                        .offset { IntOffset(offsetX.toInt(), offsetY.toInt()) }
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                offsetY = (offsetY + dragAmount.y)
                                    .coerceIn( - (maxHeight - 232.dp.toPx()), 0f)
                            }
                        }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Product")
                }
            },
            containerColor = Color.White
        ) { paddingValues ->
            LoadStateRenderer(
                loadState = userFridgeState,
                content = { products ->
                    if (products.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier
                                .padding(16.dp)
                                .background(Color.White),
                            contentPadding = paddingValues
                        ) {
                            items(products) { product ->
                                ProductItemCard(
                                    product = product,
                                    onUpdateProduct = onUpdateProduct,
                                    onDeleteProduct = onDeleteProduct,
                                    enableButtons = enableButtons
                                )
                                val today = LocalDate.now()
                                val targetDates = listOf(
                                    today.minusDays(2),
                                    today.minusDays(1),
                                    today,
                                    today.plusDays(1),
                                    today.plusDays(2),
                                    today.plusDays(8)
                                )

                                if (product.expirationDate in targetDates ||
                                    product.expirationDate < today
                                ) {
                                    LaunchedEffect(product.entryNumber) {
                                        if (isNotificationPermissionGranted) {
                                            notifyProductExpiration(context, product)
                                        }
                                    }
                                }
                            }
                        }
                    } else if (userFridgeState is Loaded) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Your fridge is empty!",
                                modifier = Modifier.padding(10.dp),
                                textAlign = TextAlign.Center,
                                color = Color.Gray
                            )
                        }
                    }
                    if (showAddProductDialog) {
                        AddProductDialog(
                            onAddProduct = onAddProduct,
                            onDismiss = { showAddProductDialog = false },
                            enableButtons = enableButtons
                        )
                    }
                }
            )
        }
    }
}

@Preview
@Composable
fun PreviewFridgeScreen() {
    val sampleProducts = listOf<Product>(
        Product("Milk", 1, 2, LocalDate.now().minusDays(1), LocalDate.now().plusDays(5)),
        Product("Eggs", 2, 12, null, LocalDate.now().plusDays(10)),
        Product("Meat", 3, 1, LocalDate.now().minusDays(2), LocalDate.now().minusDays(1)),
        Product("Cheese", 4, 1, LocalDate.now().minusDays(3), LocalDate.now().plusDays(2)),
        Product("Yogurt", 5, 4, null, LocalDate.now().plusDays(1)),
    )

    FridgeScreen(
        userFridgeState = apiSuccess(sampleProducts),
        enableButtons = true,
    )
}
