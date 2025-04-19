package epicurius.unit.services.fridge

import epicurius.domain.fridge.UpdateProductInfo
import epicurius.http.fridge.models.input.ProductInputModel
import epicurius.http.fridge.models.input.UpdateProductInputModel
import epicurius.unit.services.ServiceTest
import java.time.LocalDate

open class FridgeServiceTest : ServiceTest() {

    companion object {
        const val USER_ID = 1
        private const val PRODUCT_NAME = "apple"
        const val ENTRY_NUMBER = 1
        private const val PRODUCT_QUANTITY = 1
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
        val productInputModel = ProductInputModel(PRODUCT_NAME, PRODUCT_QUANTITY, openDate, expirationDate)
        val productInfo = productInputModel.toProductInfo()
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
    }
}
