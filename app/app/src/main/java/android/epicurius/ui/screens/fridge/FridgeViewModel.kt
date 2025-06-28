package android.epicurius.ui.screens.fridge

import android.content.Context
import android.epicurius.domain.fridge.Fridge
import android.epicurius.domain.fridge.Product
import android.epicurius.domain.fridge.validateName
import android.epicurius.domain.fridge.validateQuantity
import android.epicurius.services.EpicuriusService
import android.epicurius.services.api.fridge.input.AddProductInputModel
import android.epicurius.services.api.fridge.input.UpdateProductInputModel
import android.epicurius.storage.Session
import android.epicurius.ui.EpicuriusViewModel
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.apiSuccess
import android.epicurius.ui.screens.utils.idle
import android.epicurius.ui.screens.utils.loading
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period

class FridgeViewModel(
    service: EpicuriusService,
    session: Session,
    context: Context
): EpicuriusViewModel(service, session, context) {

    private val userFridgeFlow = MutableStateFlow<LoadState<List<Product>>>(idle())

    val userFridge = userFridgeFlow.asStateFlow()

    fun getUserFridge() {
        disableButtons()
        userFridgeFlow.value = loading()
        viewModelScope.launch { fetchUserFridge() }
    }

    fun addProductToFridge(
        name: String,
        quantity: Int,
        openDate: LocalDate?,
        expirationDate: LocalDate
    ) {
        disableButtons()
        if (!validateProductInfo(name, quantity)) {
            enableButtons()
            return
        }
        val addProductInfo = AddProductInputModel(name, quantity, openDate, expirationDate)
        viewModelScope.launch { handleAddFridgeProduct(addProductInfo) }
    }

    fun updateFridgeProduct(
        entryNumber: Int,
        quantity: Int?,
        openDate: LocalDate?,
        duration: Period?,
        expirationDate: LocalDate?
    ) {
        disableButtons()
        if (!validateProductInfo(null, quantity)) {
            enableButtons()
            return
        }
        val updateProductInfo = UpdateProductInputModel(quantity, openDate, duration, expirationDate)
        viewModelScope.launch { handleUpdateFridgeProduct(entryNumber, updateProductInfo) }
    }

    fun removeFridgeProduct(entryNumber: Int) {
        disableButtons()
        viewModelScope.launch { handleRemoveFridgeProduct(entryNumber) }
    }

    private suspend fun fetchUserFridge() {
        val result = request {
            val token = session.getToken()
            service.fridgeService.getFridge(token)
        }
        when {
            result.isSuccess -> {
                val fetchedFridge = result.getValueOrThrow().fridge.products
                userFridgeFlow.value = apiSuccess(fetchedFridge)
            }
        }
        enableButtons()
    }

    private suspend fun handleAddFridgeProduct(
        addProductInfo: AddProductInputModel
    ) {
        val result = request {
            val token = session.getToken()
            service.fridgeService.addProduct(token, addProductInfo)
        }
        when {
            result.isSuccess -> {
                val updatedFridge = result.getValueOrThrow().fridge.products
                userFridgeFlow.value = apiSuccess(updatedFridge)
            }
        }
        enableButtons()
    }

    private suspend fun handleUpdateFridgeProduct(
        entryNumber: Int,
        updateProductInfo: UpdateProductInputModel
    ) {
        val result = request {
            val token = session.getToken()
            service.fridgeService.updateFridgeProduct(token, entryNumber, updateProductInfo)
        }
        when {
            result.isSuccess -> {
                val updatedFridge = result.getValueOrThrow().fridge.products
                userFridgeFlow.value = apiSuccess(updatedFridge)
            }
        }
        enableButtons()
    }

    private suspend fun handleRemoveFridgeProduct(entryNumber: Int) {
        val result = request {
            val token = session.getToken()
            service.fridgeService.removeFridgeProduct(token, entryNumber)
        }
        when {
            result.isSuccess -> {
                val updatedFridge = result.getValueOrThrow().fridge.products
                userFridgeFlow.value = apiSuccess(updatedFridge)
            }
        }
        enableButtons()
    }

    private fun validateProductInfo(
        name: String?,
        quantity: Int?
    ): Boolean =
        when {
            name != null && !validateName(name, ::showToast) -> false
            quantity != null && !validateQuantity(quantity, ::showToast) -> false
            else -> true
        }
}