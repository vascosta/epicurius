package epicurius.integration.fridge

import epicurius.domain.user.AuthenticatedUser
import epicurius.http.controllers.fridge.models.output.FridgeOutputModel
import epicurius.http.utils.Uris
import epicurius.integration.EpicuriusIntegrationTest
import epicurius.integration.utils.delete
import epicurius.integration.utils.get
import epicurius.integration.utils.patch
import epicurius.integration.utils.post
import epicurius.utils.createTestUser
import org.junit.jupiter.api.BeforeEach
import org.springframework.http.HttpStatus
import java.time.LocalDate
import java.time.Period

class FridgeIntegrationTest : EpicuriusIntegrationTest() {

    lateinit var testUser: AuthenticatedUser

    @BeforeEach
    fun setup() {
        testUser = createTestUser(tm)
    }

    fun getFridge(token: String) = get<FridgeOutputModel>(client, api(Uris.Fridge.FRIDGE), token = token)

    fun getProductsList(token: String, partial: String) =
        get<List<String>>(client, api(Uris.Ingredients.INGREDIENTS) + "?partial=$partial", token = token)

    fun addProducts(token: String, productName: String, quantity: Int, openDate: LocalDate? = null, expirationDate: LocalDate) =
        post<FridgeOutputModel>(
            client,
            api(Uris.Fridge.FRIDGE),
            mapOf(
                "productName" to productName,
                "quantity" to quantity,
                "openDate" to openDate,
                "expirationDate" to expirationDate
            ),
            HttpStatus.CREATED,
            token = token
        )

    fun updateFridgeProduct(token: String, entryNumber: Int, quantity: Int? = null, expirationDate: LocalDate? = null) =
        patch<FridgeOutputModel>(
            client,
            api(Uris.Fridge.PRODUCT.take(16) + entryNumber),
            body = mapOf("quantity" to quantity, "expirationDate" to expirationDate),
            responseStatus = HttpStatus.OK,
            token = token
        )

    fun openFridgeProduct(
        token: String,
        entryNumber: Int,
        openDate: LocalDate,
        duration: Period
    ) = patch<FridgeOutputModel>(
        client,
        api(Uris.Fridge.PRODUCT.take(16) + entryNumber),
        body = mapOf("openDate" to openDate, "duration" to duration),
        responseStatus = HttpStatus.OK,
        token = token
    )

    fun removeProduct(token: String, entryNumber: Int) =
        delete<FridgeOutputModel>(
            client,
            api(Uris.Fridge.PRODUCT.take(16) + entryNumber),
            HttpStatus.OK,
            token = token
        )
}
