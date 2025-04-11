package epicurius.unit.repository.user

import epicurius.domain.PagingParams
import epicurius.unit.repository.RepositoryTest
import epicurius.repository.jdbi.user.models.SearchUserModel
import epicurius.utils.createTestUser
import epicurius.utils.generateEmail
import epicurius.utils.generateRandomUsername
import epicurius.utils.generateSecurePassword
import org.junit.jupiter.api.Assertions.assertNull
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AuthenticationRepositoryTest : RepositoryTest() {

    @Test
    fun `Create new user and retrieve it successfully`() {
        // given user required information
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val passwordHash = usersDomain.encodePassword(generateSecurePassword())

        // when creating a user
        createUser(username, email, country, passwordHash)

        // when getting the user by name
        val userByName = getUserByName(username)

        // then the user is retrieved successfully
        assertNotNull(userByName)
        assertEquals(username, userByName.username)
        assertEquals(email, userByName.email)
        assertEquals(country, userByName.country)
        assertEquals(passwordHash, userByName.passwordHash)
        assertFalse(userByName.privacy)
        assertEquals(emptyList(), userByName.intolerances)
        assertEquals(emptyList(), userByName.diets)
        assertNull(userByName.profilePictureName)

        // when getting the user by email
        val userByEmail = getUserByEmail(email)

        // then the user is retrieved successfully
        assertNotNull(userByEmail)
        assertEquals(username, userByEmail.username)
        assertEquals(email, userByEmail.email)
        assertEquals(country, userByEmail.country)
        assertEquals(passwordHash, userByEmail.passwordHash)
        assertFalse(userByEmail.privacy)
        assertEquals(emptyList(), userByEmail.intolerances)
        assertEquals(emptyList(), userByEmail.diets)
        assertNull(userByEmail.profilePictureName)
    }

    @Test
    fun `Create new users and retrieve them successfully`() {
        // given 2 created users
        val username = "partial"
        val username2 = "partialUsername"
        val email = generateEmail(username)
        val email2 = generateEmail(username2)
        val country = "PT"
        val passwordHash = usersDomain.encodePassword(generateSecurePassword())
        createUser(username, email, country, passwordHash)
        createUser(username2, email2, country, passwordHash)

        // when getting the users by a partial username
        val users = getUsers("partial", PagingParams())

        // then the users are retrieved successfully
        assertTrue(users.isNotEmpty())
        assertEquals(2, users.size)
        assertTrue(users.contains(SearchUserModel(username, null)))
        assertTrue(users.contains(SearchUserModel(username2, null)))
    }

    @Test
    fun `Reset password successfully`() {
        // given user required information
        val user = createTestUser(tm)

        // when resetting the password
        val newPassword = UUID.randomUUID().toString()
        val newPasswordHash = usersDomain.encodePassword(newPassword)
        resetPassword(user.email, newPasswordHash)

        // when getting the user by name
        val userAfterResetPassword = getUserByName(user.username)

        // then the password is reset successfully
        assertNotNull(userAfterResetPassword)
        assertEquals(user.username, userAfterResetPassword.username)
        assertEquals(user.email, userAfterResetPassword.email)
        assertEquals(newPasswordHash, userAfterResetPassword.passwordHash)
        assertNotEquals(user.passwordHash, userAfterResetPassword.passwordHash)
    }

    @Test
    fun `Checks if an existing user exists successfully`() {
        // given an existing user with a token hash
        val user = createTestUser(tm)
        val token = usersDomain.generateTokenValue()
        val tokenHash = usersDomain.hashToken(token)
        createToken(tokenHash, user.username)

        // when checking if the user exists by name
        val userExistsByName = getUserByName(user.username)

        // when checking if the user exists by email
        val userExistsByEmail = getUserByEmail(user.email)

        // when checking if the user exists by token hash
        val userExistsByTokenHash = getUserByTokenHash(tokenHash)

        // then the user exists
        assertNotNull(userExistsByName)
        assertNotNull(userExistsByEmail)
        assertNotNull(userExistsByTokenHash)
        assertEquals(user.username, userExistsByName.username)
        assertEquals(user.username, userExistsByEmail.username)
        assertEquals(user.username, userExistsByTokenHash.username)
        assertEquals(user.email, userExistsByName.email)
        assertEquals(user.email, userExistsByEmail.email)
        assertEquals(user.email, userExistsByTokenHash.email)
    }

    @Test
    fun `Checks if an non-existing user exists successfully`() {
        // given a non-existing user with non-existing token hash
        val username = ""
        val email = ""
        val tokenHash = ""

        // when checking if the user exists by name
        val userExistsByName = getUserByName(username)

        // when checking if the user exists by email
        val userExistsByEmail = getUserByEmail(email)

        // when checking if the user exists by token hash
        val userExistsByTokenHash = getUserByTokenHash(tokenHash)

        // then the user does not exist
        assertNull(userExistsByName)
        assertNull(userExistsByEmail)
        assertNull(userExistsByTokenHash)
    }

    @Test
    fun `Checks if an existing user is logged in successfully`() {
        // given an existing user logged in
        val user = createTestUser(tm)
        val token = usersDomain.generateTokenValue()
        val tokenHash = usersDomain.hashToken(token)
        createToken(tokenHash, user.username)

        // when checking if the user is logged in
        val userExistsByName = checkIfUserIsLoggedIn(user.username)

        // when checking if the user exists by email
        val userExistsByEmail = checkIfUserIsLoggedIn(email = user.email)

        // then the user is logged in
        assertTrue(userExistsByName)
        assertTrue(userExistsByEmail)
    }

    @Test
    fun `Checks if not logged in user is not logged in successfully`() {
        // given an existing user not logged in
        val user = createTestUser(tm)

        // when checking if the user is logged in
        val userExistsByName = checkIfUserIsLoggedIn(user.username)

        // when checking if the user exists by email
        val userExistsByEmail = checkIfUserIsLoggedIn(email = user.email)

        // then the user is not logged in
        assertFalse(userExistsByName)
        assertFalse(userExistsByEmail)
    }
}
