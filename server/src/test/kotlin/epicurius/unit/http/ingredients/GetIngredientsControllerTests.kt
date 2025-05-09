package epicurius.unit.http.ingredients

import epicurius.domain.user.AuthenticatedUser
import epicurius.domain.user.User
import epicurius.http.controllers.ingredients.models.output.GetIngredientsOutputModel
import epicurius.unit.http.HttpTest
import epicurius.utils.generateEmail
import epicurius.utils.generateRandomUsername
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import kotlin.test.Test

class GetIngredientsControllerTests : HttpTest() {

    private val testIngredients = listOf(
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

    private val testUsername = generateRandomUsername()
    private val testAuthenticatedUser = AuthenticatedUser(
        User(1, testUsername, generateEmail(testUsername), "", "", "PT", false, emptyList(), emptyList(), ""),
        ""
    )

    @Test
    fun `Should retrieve ingredients given a partial name successfully`() {
        // given a partial name
        val partialName = "app"

        // mock
        whenever(runBlocking { ingredientsServiceMock.getIngredients(partialName) }).thenReturn(testIngredients)
        whenever(authenticationRefreshHandlerMock.refreshToken(testAuthenticatedUser.token)).thenReturn(mockCookie)

        // when retrieving the ingredients
        val response = runBlocking { getIngredients(testAuthenticatedUser, partialName, mockResponse) }
        val body = response.body as GetIngredientsOutputModel

        // then the products list is retrieved successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(testIngredients, body.ingredients)
    }

    @Test
    fun `Should retrieve an empty list of ingredients when no ingredients are found`() {
        // given a partial name
        val nonExistingPartialName = "nonexistent"

        // mock
        whenever(runBlocking { ingredientsServiceMock.getIngredients(nonExistingPartialName) }).thenReturn(emptyList())
        whenever(authenticationRefreshHandlerMock.refreshToken(testAuthenticatedUser.token)).thenReturn(mockCookie)

        // when retrieving the ingredients
        val response = runBlocking { getIngredients(testAuthenticatedUser, nonExistingPartialName, mockResponse) }
        val body = response.body as GetIngredientsOutputModel

        // then the products list is retrieved successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(emptyList<String>(), body.ingredients)
    }
}
