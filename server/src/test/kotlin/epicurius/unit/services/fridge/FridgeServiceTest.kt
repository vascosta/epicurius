package epicurius.unit.services.fridge

import epicurius.domain.fridge.Product
import epicurius.domain.fridge.ProductInfo
import epicurius.domain.fridge.UpdateProductInfo
import epicurius.http.controllers.fridge.models.input.AddProductInputModel
import epicurius.http.controllers.fridge.models.input.UpdateProductInputModel
import epicurius.unit.services.ServiceTest
import java.time.LocalDate
import java.time.Period

open class FridgeServiceTest : ServiceTest() {

    companion object {
        const val USER_ID = 1
        const val PRODUCT_NAME = "apple"
        const val ENTRY_NUMBER = 1
        const val PRODUCT_QUANTITY = 1
        private val openDate: LocalDate? = null
        private val expirationDate: LocalDate = LocalDate.now().plusDays(7)
        val productsList = listOf(
            "apple",
            "applesauce",
            "apple juice",
            "apple cider",
            "apple jelly",
            "apple butter",
            "apple pie spice",
            "apple pie filling",
            "apple cider vinegar",
            "applewood smoked bacon"
        )

        // ADD PRODUCT
        val addProductInputModel = AddProductInputModel(PRODUCT_NAME, PRODUCT_QUANTITY, openDate, expirationDate)
        val productInfo = addProductInputModel.toProductInfo()
        val product = productInfo.toProduct(ENTRY_NUMBER)

        // UPDATE PRODUCT
        const val NEW_QUANTITY = 3
        val newExpirationDate: LocalDate = LocalDate.now().plusDays(20)

        val updateProductInputModel = UpdateProductInputModel(
            quantity = NEW_QUANTITY,
            expirationDate = newExpirationDate
        )
        val updateProductInfo = UpdateProductInfo(
            entryNumber = ENTRY_NUMBER,
            quantity = NEW_QUANTITY,
            expirationDate = newExpirationDate
        )

        // OPEN PRODUCT
        val open: LocalDate = LocalDate.now()
        val duration: Period = Period.ofDays(10)
        val newExpiration: LocalDate = open.plus(duration)
        const val NEW_ENTRY_NUMBER = ENTRY_NUMBER + 1

        val openedProductInfo = ProductInfo(
            name = PRODUCT_NAME,
            quantity = PRODUCT_QUANTITY,
            openDate = open,
            expirationDate = newExpiration
        )

        val openedProduct = Product(
            name = PRODUCT_NAME,
            entryNumber = NEW_ENTRY_NUMBER,
            quantity = PRODUCT_QUANTITY,
            openDate = open,
            expirationDate = newExpiration
        )

        val openProductInputModel = UpdateProductInputModel(
            openDate = open,
            duration = duration
        )

        val decreaseQuantity = UpdateProductInfo(
            entryNumber = ENTRY_NUMBER,
            quantity = 1
        )

        val increaseQuantity = UpdateProductInfo(
            entryNumber = NEW_ENTRY_NUMBER,
            quantity = PRODUCT_QUANTITY + 1
        )
    }
}
