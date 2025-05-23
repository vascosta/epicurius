package epicurius.unit.http.fridge

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.fridge.Product
import epicurius.domain.user.AuthenticatedUser
import epicurius.domain.user.User
import epicurius.http.controllers.fridge.models.input.AddProductInputModel
import epicurius.http.controllers.fridge.models.input.UpdateProductInputModel
import epicurius.unit.http.HttpTest
import epicurius.utils.generateEmail
import epicurius.utils.generateRandomUsername
import java.time.LocalDate
import java.time.Period
import java.util.UUID.randomUUID

open class FridgeHttpTest : HttpTest() {

    companion object {
        val objectMapper = jacksonObjectMapper()

        private val authenticatedUsername = generateRandomUsername()
        private val token = randomUUID().toString()

        val testAuthenticatedUser = AuthenticatedUser(
            User(
                1,
                authenticatedUsername,
                generateEmail(authenticatedUsername),
                userDomain.encodePassword(randomUUID().toString()),
                userDomain.hashToken(token),
                "PT",
                false,
                listOf(Intolerance.GLUTEN),
                listOf(Diet.GLUTEN_FREE),
                randomUUID().toString()
            ),
            token,
        )

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

        const val ENTRY_NUMBER = 1
        const val NEW_ENTRY_NUMBER = 2

        val product = Product(
            name = "apple",
            entryNumber = ENTRY_NUMBER,
            quantity = 1,
            openDate = null,
            expirationDate = LocalDate.now().plusDays(7)
        )

        // ADD PRODUCT
        val addProductInputModel = AddProductInputModel(
            name = "apple",
            quantity = 1,
            expirationDate = LocalDate.now().plusDays(7)
        )

        // UPDATE PRODUCT
        const val NEW_QUANTITY = 2
        val newExpirationDate: LocalDate = LocalDate.now().plusDays(10)

        val updateProductInputModel = UpdateProductInputModel(
            quantity = NEW_QUANTITY,
            expirationDate = newExpirationDate
        )

        // OPEN PRODUCT
        val openProductInputModel = UpdateProductInputModel(
            openDate = LocalDate.now(),
            duration = Period.ofDays(7)
        )
    }
}
