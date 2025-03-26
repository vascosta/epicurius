package epicurius.repository

import epicurius.domain.Diet
import epicurius.domain.FollowingStatus
import epicurius.domain.Intolerance
import epicurius.domain.user.SocialUser
import epicurius.domain.user.UpdateUserInfo
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

class UserRepositoryTest : RepositoryTest() {

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
        assertEquals(userByName.username, username)
        assertEquals(userByName.email, email)
        assertEquals(userByName.country, country)
        assertEquals(userByName.passwordHash, passwordHash)
        assertEquals(userByName.privacy, false)
        assertEquals(userByName.intolerances, emptyList())
        assertEquals(userByName.diet, emptyList())
        assertNull(userByName.profilePictureName)

        // when getting the user by email
        val userByEmail = getUserByEmail(email)

        // then the user is retrieved successfully
        assertNotNull(userByEmail)
        assertEquals(userByEmail.username, username)
        assertEquals(userByEmail.email, email)
        assertEquals(userByEmail.country, country)
        assertEquals(userByEmail.passwordHash, passwordHash)
        assertEquals(userByEmail.privacy, false)
        assertEquals(userByEmail.intolerances, emptyList())
        assertEquals(userByEmail.diet, emptyList())
        assertNull(userByEmail.profilePictureName)
    }

    @Test
    fun `Reset password successfully`() {
        // given user required information
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val password = generateSecurePassword()
        val passwordHash = usersDomain.encodePassword(password)

        // when creating a user
        createUser(username, email, country, passwordHash)

        // when resetting the password
        val newPassword = UUID.randomUUID().toString()
        val newPasswordHash = usersDomain.encodePassword(newPassword)
        resetPassword(email, newPasswordHash)

        // when getting the user by name
        val user = getUserByName(username)

        // then the password is reset successfully
        assertNotNull(user)
        assertEquals(user.username, username)
        assertEquals(user.email, email)
        assertEquals(user.passwordHash, newPasswordHash)
        assertNotEquals(user.passwordHash, passwordHash)
    }

    @Test
    fun `Update user successfully`() {
        // given user required information
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val password = generateSecurePassword()
        val passwordHash = usersDomain.encodePassword(password)

        // when creating a user
        createUser(username, email, country, passwordHash)

        // when updating the user
        val newUsername = generateRandomUsername()
        val newEmail = generateEmail(newUsername)
        val newCountry = "ES"
        val newPassword = generateSecurePassword()
        val newPasswordHash = usersDomain.encodePassword(newPassword)
        val newPrivacy = true
        val newIntolerances = listOf(Intolerance.GLUTEN)
        val newDiet = listOf(Diet.VEGAN)

        val user = updateUser(
            username,
            UpdateUserInfo(
                username = newUsername,
                email = newEmail,
                country = newCountry,
                passwordHash = newPasswordHash,
                privacy = newPrivacy,
                intolerances = newIntolerances.map { Intolerance.entries.indexOf(it) },
                diet = newDiet.map { Diet.entries.indexOf(it) }
            )
        )

        // then the user is updated successfully
        assertEquals(user.username, newUsername)
        assertEquals(user.email, newEmail)
        assertEquals(user.country, newCountry)
        assertEquals(user.passwordHash, newPasswordHash)
        assertEquals(user.privacy, newPrivacy)
        assertEquals(user.intolerances, newIntolerances)
        assertEquals(user.diet, newDiet)
    }

    @Test
    fun `Checks if an existing user exists successfully`() {
        // given an existing user with a token hash
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val passwordHash = usersDomain.encodePassword(generateSecurePassword())
        val token = usersDomain.generateTokenValue()
        val tokenHash = usersDomain.hashToken(token)

        createUser(username, email, country, passwordHash)
        createToken(tokenHash, username)

        // when checking if the user exists by name
        val userExistsByName = getUserByName(username)

        // when checking if the user exists by email
        val userExistsByEmail = getUserByEmail(email)

        // when checking if the user exists by token hash
        val userExistsByTokenHash = getUserByTokenHash(tokenHash)

        // then the user exists
        assertNotNull(userExistsByName)
        assertNotNull(userExistsByEmail)
        assertNotNull(userExistsByTokenHash)
        assertEquals(userExistsByName.username, username)
        assertEquals(userExistsByEmail.username, username)
        assertEquals(userExistsByTokenHash.username, username)
        assertEquals(userExistsByName.email, email)
        assertEquals(userExistsByEmail.email, email)
        assertEquals(userExistsByTokenHash.email, email)
    }

    @Test
    fun `Checks if an non user exists successfully`() {
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
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val passwordHash = usersDomain.encodePassword(generateSecurePassword())
        val token = usersDomain.generateTokenValue()
        val tokenHash = usersDomain.hashToken(token)

        createUser(username, email, country, passwordHash)
        createToken(tokenHash, username)

        // when checking if the user is logged in
        val userExistsByName = checkIfUserIsLoggedIn(username)

        // when checking if the user exists by email
        val userExistsByEmail = checkIfUserIsLoggedIn(email = email)

        // then the user is logged in
        assertTrue(userExistsByName)
        assertTrue(userExistsByEmail)
    }

    @Test
    fun `Checks if not logged in user is not logged in successfully`() {
        // given an existing user not logged in
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val passwordHash = usersDomain.encodePassword(generateSecurePassword())

        createUser(username, email, country, passwordHash)

        // when checking if the user is logged in
        val userExistsByName = checkIfUserIsLoggedIn(username)

        // when checking if the user exists by email
        val userExistsByEmail = checkIfUserIsLoggedIn(email = email)

        // then the user is not logged in
        assertFalse(userExistsByName)
        assertFalse(userExistsByEmail)
    }

    @Test
    fun `Follow a public user and then retrieve its followers and following successfully`() {
        // given two users
        val publicUser = publicTestUser
        val privateUser = privateTestUser

        // when following a public user
        follow(privateUser.id, publicUser.id, FollowingStatus.ACCEPTED.ordinal)

        // then the user is followed successfully
        val publicUserFollowers = getFollowers(publicUser.id)
        val privateUserFollowing = getFollowing(privateUser.id)
        assertTrue(publicUserFollowers.isNotEmpty())
        assertTrue(privateUserFollowing.isNotEmpty())
        assertEquals(publicUserFollowers.size, 1)
        assertEquals(privateUserFollowing.size, 1)
        assertTrue(publicUserFollowers.contains(SocialUser(privateUser.username, privateUser.profilePictureName)))
        assertTrue(privateUserFollowing.contains(SocialUser(publicUser.username, publicUser.profilePictureName)))
    }
}
