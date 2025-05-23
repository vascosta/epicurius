package epicurius.unit.http.ingredients

import epicurius.domain.user.AuthenticatedUser
import epicurius.domain.user.User
import epicurius.http.controllers.ingredients.models.output.IdentifyIngredientsInPictureOutputModel
import epicurius.unit.http.HttpTest
import epicurius.utils.generateEmail
import epicurius.utils.generateRandomUsername
import kotlinx.coroutines.runBlocking
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class IdentifyIngredientsInPictureControllerTests : HttpTest() {

    private val testIngredients = listOf("tomato")

    private val testUsername = generateRandomUsername()
    private val testAuthenticatedUser = AuthenticatedUser(
        User(1, testUsername, generateEmail(testUsername), "", "", "PT", false, emptyList(), emptyList(), ""),
        ""
    )

    @Test
    fun `Should detect ingredients in a picture successfully`() {
        // given a picture (testTomatoPicture)

        // mock
        whenever(
            runBlocking { ingredientsServiceMock.identifyIngredientsInPicture(testTomatoPicture) }
        ).thenReturn(testIngredients)

        // when retrieving the ingredients from the picture
        val response = runBlocking { identifyIngredientsInPicture(testAuthenticatedUser, testTomatoPicture) }
        val body = response.body as IdentifyIngredientsInPictureOutputModel

        // then the ingredients are detected successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(testIngredients, body.ingredients)
    }

    @Test
    fun `Should return an empty list for a picture with no ingredients`() {
        // given a picture with no ingredients (testPicture)

        // mock
        whenever(
            runBlocking { ingredientsServiceMock.identifyIngredientsInPicture(testPicture) }
        ).thenReturn(emptyList())

        // when retrieving the ingredients from the picture
        val response = runBlocking { identifyIngredientsInPicture(testAuthenticatedUser, testPicture) }
        val body = response.body as IdentifyIngredientsInPictureOutputModel

        // then the ingredients are empty
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(emptyList(), body.ingredients)
    }
}
