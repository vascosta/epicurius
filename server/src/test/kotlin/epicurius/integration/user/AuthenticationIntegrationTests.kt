package epicurius.integration.user

import epicurius.domain.exceptions.UnauthorizedException
import epicurius.http.utils.Problem
import epicurius.http.utils.Uris
import epicurius.integration.utils.getBody
import epicurius.integration.utils.post
import epicurius.utils.generateRandomUsername
import epicurius.utils.generateSecurePassword
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthenticationIntegrationTests: UserIntegrationTest() {

    @Test
    fun `Should fail with 401 code when an unauthenticated user tries to do an authenticated operation`() {
        // given a non-authenticated user
        val username = generateRandomUsername()

        // when trying to do an authenticated operation, e.g. logout
        val unauthenticatedError = post<Problem>(
            client,
            api(Uris.User.LOGOUT),
            mapOf("name" to username, "password" to generateSecurePassword()),
            HttpStatus.UNAUTHORIZED,
            ""
        )

        // then the user couldn't do the operation and an error is returned
        val unauthenticatedErrorBody = getBody(unauthenticatedError)
        assertEquals(UnauthorizedException("Missing user token").message, unauthenticatedErrorBody.detail)
    }
}