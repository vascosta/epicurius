package epicurius.integration.fridge

import epicurius.domain.user.AuthenticatedUser
import epicurius.http.controllers.fridge.models.output.AddProductOutputModel
import epicurius.http.controllers.fridge.models.output.GetFridgeOutputModel
import epicurius.http.controllers.fridge.models.output.RemoveProductOutputModel
import epicurius.http.controllers.fridge.models.output.UpdateProductOutputModel
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

    fun getFridge(token: String) = get<GetFridgeOutputModel>(client, api(Uris.Fridge.FRIDGE), token = token)

    fun getProductsList(token: String, partial: String) =
        get<List<String>>(client, api(Uris.Ingredients.INGREDIENTS) + "?partial=$partial", token = token)

    fun addProducts(token: String, productName: String, quantity: Int, openDate: LocalDate? = null, expirationDate: LocalDate) =
        post<AddProductOutputModel>(
            client,
            api(Uris.Fridge.FRIDGE),
            mapOf(
                "name" to productName,
                "quantity" to quantity,
                "openDate" to openDate,
                "expirationDate" to expirationDate
            ),
            HttpStatus.CREATED,
            token = token
        )

    fun updateFridgeProduct(token: String, entryNumber: Int, quantity: Int? = null, expirationDate: LocalDate? = null) =
        patch<UpdateProductOutputModel>(
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
    ) = patch<UpdateProductOutputModel>(
        client,
        api(Uris.Fridge.PRODUCT.take(16) + entryNumber),
        body = mapOf("openDate" to openDate, "duration" to duration),
        responseStatus = HttpStatus.OK,
        token = token
    )

    fun removeProduct(token: String, entryNumber: Int) =
        delete<RemoveProductOutputModel>(
            client,
            api(Uris.Fridge.PRODUCT.take(16) + entryNumber),
            HttpStatus.OK,
            token = token
        )
}
