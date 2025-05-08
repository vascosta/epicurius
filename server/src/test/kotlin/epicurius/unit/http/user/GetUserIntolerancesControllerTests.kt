package epicurius.unit.http.user

import epicurius.http.user.models.output.GetUserIntolerancesOutputModel
import epicurius.unit.http.recipe.RecipeHttpTest.Companion.testAuthenticatedUser
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class GetUserIntolerancesControllerTests : UserHttpTest() {

    @Test
    fun `Should retrieve the intolerances of an user successfully`() {
        // given a user (publicTestUser)

        // mock
        whenever(authenticationRefreshHandlerMock.refreshToken(publicTestUser.token)).thenReturn(mockCookie)

        // when retrieving the user's intolerances
        val response = getUserIntolerances(publicTestUser, mockResponse)
        val body = response.body as GetUserIntolerancesOutputModel

        // then the user's intolerances are retrieved successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(publicTestUser.user.toUserInfo().intolerances, body.intolerances)
    }
}
