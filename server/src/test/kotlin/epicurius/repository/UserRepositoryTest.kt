package epicurius.repository

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.services.models.UpdateUserModel
import epicurius.utils.generateRandomUsername
import epicurius.utils.generateSecurePassword
import org.junit.jupiter.api.Assertions.assertNull
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class UserRepositoryTest: RepositoryTest() {

    @Test
    fun `Create new user and retrieve it successfully`() {
        // given user required information
        val username = generateRandomUsername()
        val email = "$username@email.com"
        val country = "PT"
        val passwordHash = usersDomain.encodePassword(generateSecurePassword())

        // when creating a user
        createUser(username, email, country, passwordHash)

        // when getting the user by name
        val userByName = getUserByName(username)

        // then the user is retrieved successfully
        assertEquals(userByName.username, username)
        assertEquals(userByName.email, email)
        assertEquals(userByName.country, country)
        assertEquals(userByName.passwordHash, passwordHash)
        assertEquals(userByName.privacy, false)
        assertEquals(userByName.intolerances, emptyList())
        assertEquals(userByName.diet, emptyList())
        assertNull(userByName.profilePictureName)
    }

    @Test
    fun `Reset password successfully`() {
        // given user required information
        val username = generateRandomUsername()
        val email = "$username@email.com"
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
        assertEquals(user.username, username)
        assertEquals(user.email, email)
        assertEquals(user.passwordHash, newPasswordHash)
        assertNotEquals(user.passwordHash, passwordHash)
    }

    @Test
    fun `Update user profile successfully`() {
        // given user required information
        val username = generateRandomUsername()
        val email = "$username@email.com"
        val country = "PT"
        val password = generateSecurePassword()
        val passwordHash = usersDomain.encodePassword(password)

        // when creating a user
        createUser(username, email, country, passwordHash)

        // when updating the user profile
        val newUsername = generateRandomUsername()
        val newEmail = "$newUsername@email.com"
        val newCountry = "ES"
        val newPassword = generateSecurePassword()
        val newPasswordHash = usersDomain.encodePassword(newPassword)
        val newPrivacy = true
        val newIntolerances = listOf(Intolerance.GLUTEN)
        val newDiet = listOf(Diet.VEGAN)

        updateProfile(
            username, UpdateUserModel(
                username = newUsername,
                email = newEmail,
                country = newCountry,
                passwordHash = newPasswordHash,
                privacy = newPrivacy,
                intolerances = newIntolerances.map { Intolerance.entries.indexOf(it) },
                diet = newDiet.map { Diet.entries.indexOf(it) }
            )
        )

        // when getting the user by name
        val user = getUserByName(newUsername)

        // then the user profile is updated successfully
        assertEquals(user.username, newUsername)
        assertEquals(user.email, newEmail)
        assertEquals(user.country, newCountry)
        assertEquals(user.passwordHash, newPasswordHash)
        assertEquals(user.privacy, newPrivacy)
        assertEquals(user.intolerances, newIntolerances)
        assertEquals(user.diet, newDiet)
    }
}
