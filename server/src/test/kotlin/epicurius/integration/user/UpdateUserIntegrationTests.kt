package epicurius.integration.user

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.exceptions.InvalidCountry
import epicurius.domain.exceptions.PasswordsDoNotMatch
import epicurius.domain.exceptions.UserAlreadyExists
import epicurius.http.utils.Problem
import epicurius.http.utils.Uris
import epicurius.integration.utils.getBody
import epicurius.integration.utils.patch
import epicurius.utils.createTestUser
import epicurius.utils.generateEmail
import epicurius.utils.generateRandomUsername
import epicurius.utils.generateSecurePassword
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class UpdateUserIntegrationTests : UserIntegrationTest() {

    private val testUser = createTestUser(tm)

    @Test
    fun `Should update a user successfully with code 200`() {
        // given information to update a user
        val newUsername = generateRandomUsername()
        val newEmail = generateEmail(newUsername)
        val newPassword = generateSecurePassword()
        val newCountry = "ES"
        val newPrivacy = true
        val newIntolerances = listOf(Intolerance.GLUTEN)
        val newDiets = listOf(Diet.VEGAN)

        // when updating the user
        val body = updateUser(
            testUser.token,
            newUsername,
            newEmail,
            newCountry,
            newPassword,
            newPassword,
            newPrivacy,
            newIntolerances,
            newDiets
        )

        // then the user is updated successfully with code 200
        assertNotNull(body)
        assertEquals(newUsername, body.userInfo.name)
        assertEquals(newEmail, body.userInfo.email)
        assertEquals(newCountry, body.userInfo.country)
        assertEquals(newPrivacy, body.userInfo.privacy)
        assertEquals(newIntolerances, body.userInfo.intolerances)
        assertEquals(newDiets, body.userInfo.diets)
    }

    @Test
    fun `Should fail with code 409 when updating a user with an existing username or email`() {
        // given two users
        val user = createTestUser(tm)
        val user2 = createTestUser(tm)

        // when updating the user with an existing username
        val errorExistingUsername = patch<Problem>(
            client,
            api(Uris.User.USER),
            body = mapOf(
                "name" to user2.user.name,
            ),
            responseStatus = HttpStatus.CONFLICT,
            token = user.token
        )

        // when updating the user with an existing email
        val errorExistingEmail = patch<Problem>(
            client,
            api(Uris.User.USER),
            body = mapOf(
                "email" to user2.user.email,
            ),
            responseStatus = HttpStatus.CONFLICT,
            token = user.token
        )

        // when updating the user with an existing username and email
        val errorExistingUsernameAndEmail = patch<Problem>(
            client,
            api(Uris.User.USER),
            body = mapOf(
                "name" to user2.user.name,
                "email" to user2.user.email,
            ),
            responseStatus = HttpStatus.CONFLICT,
            token = user.token
        )

        // then the user cannot be updated and fails with code 409
        val errorExistingUsernameBody = getBody(errorExistingUsername)
        val errorExistingEmailBody = getBody(errorExistingEmail)
        val errorExistingUsernameAndEmailBody = getBody(errorExistingUsernameAndEmail)
        assertEquals(UserAlreadyExists().message, errorExistingUsernameBody.detail)
        assertEquals(UserAlreadyExists().message, errorExistingEmailBody.detail)
        assertEquals(UserAlreadyExists().message, errorExistingUsernameAndEmailBody.detail)
    }

    @Test
    fun `Should fail with code 400 when updating a user with an invalid country`() {
        // given a user (testUser) and an invalid country
        val invalidCountry = "XX"

        // when updating the user with an invalid country
        val error = patch<Problem>(
            client,
            api(Uris.User.USER),
            body = mapOf(
                "country" to invalidCountry,
            ),
            responseStatus = HttpStatus.BAD_REQUEST,
            token = testUser.token
        )

        // then the user cannot be updated and fails with code 400
        val errorBody = getBody(error)
        assertEquals(InvalidCountry().message, errorBody.detail)
    }

    @Test
    fun `Should fail with code 400 when updating a user with different passwords`() {
        // given a user (testUser) and different passwords
        val password1 = generateSecurePassword()
        val password2 = generateSecurePassword()

        // when updating the user with different passwords
        val error = patch<Problem>(
            client,
            api(Uris.User.USER),
            body = mapOf(
                "password" to password1,
                "confirmPassword" to password2,
            ),
            responseStatus = HttpStatus.BAD_REQUEST,
            token = testUser.token
        )

        // then the user cannot be updated and fails with code 400
        val errorBody = getBody(error)
        assertEquals(PasswordsDoNotMatch().message, errorBody.detail)
    }
}
