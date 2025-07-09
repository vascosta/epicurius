package android.epicurius.ui.screens.fridge

import android.epicurius.ui.EpicuriusActivity
import android.epicurius.ui.screens.utils.Idle
import android.epicurius.ui.screens.utils.idle
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period

class FridgeActivity : EpicuriusActivity() {
    override val viewModel: FridgeViewModel by getViewModel<FridgeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            viewModel.userFridge.collectLatest { state ->
                if (state is Idle) viewModel.getUserFridge()
            }
        }
        setContent {
            val userFridgeState = viewModel.userFridge.collectAsState(idle())
            val productsResultState = viewModel.searchedProducts.collectAsState(idle())
            FridgeScreen(
                userFridgeState = userFridgeState.value,
                productsResultState = productsResultState.value,
                onSearchProduct = { partialName -> viewModel.searchProducts(partialName) },
                onAddProduct = {
                    name: String,
                    quantity: Int,
                    openDate: LocalDate?,
                    expirationDate: LocalDate
                    ->
                    viewModel.addProductToFridge(name, quantity, openDate, expirationDate)
                },
                onUpdateProduct = {
                    entryNumber: Int,
                    quantity: Int?,
                    openDate: LocalDate?,
                    duration: Period?,
                    expirationDate: LocalDate?
                    ->
                    viewModel.updateFridgeProduct(entryNumber, quantity, openDate, duration, expirationDate)
                },
                onDeleteProduct = { entryNumber: Int -> viewModel.removeFridgeProduct(entryNumber) },
                onProductsResultClear = { viewModel.clearSearchedProducts() },
                enableButtons = viewModel.enableButtons
            )
        }
    }
}
