package epicurius.integration.user

import epicurius.domain.exceptions.AuthenticatedUserNotFound
import epicurius.domain.exceptions.MissingUserToken
import epicurius.http.media.Problem
import epicurius.http.media.Uris
import epicurius.integration.utils.getBody
import epicurius.integration.utils.post
import epicurius.utils.generateRandomUsername
import epicurius.utils.generateSecurePassword
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthenticationIntegrationTests : UserIntegrationTest() {

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

        val unauthenticatedError2 = post<Problem>(
            client,
            api(Uris.User.LOGOUT),
            mapOf("name" to username, "password" to generateSecurePassword()),
            HttpStatus.UNAUTHORIZED,
            userDomain.generateTokenValue()
        )

        // then the user couldn't do the operation and an error is returned
        val unauthenticatedErrorBody = getBody(unauthenticatedError)
        val unauthenticatedErrorBody2 = getBody(unauthenticatedError2)
        assertEquals(MissingUserToken().message, unauthenticatedErrorBody.detail)
        assertEquals(AuthenticatedUserNotFound().message, unauthenticatedErrorBody2.detail)
    }
}
