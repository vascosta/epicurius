package epicurius.unit.repository.user

import epicurius.utils.generateEmail
import epicurius.utils.generateRandomUsername
import epicurius.utils.generateSecurePassword
import org.junit.jupiter.api.Assertions.assertNull
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

class CreateUserRepositoryTests : UserRepositoryTest() {

    @Test
    fun `Should create a new user and retrieve it successfully`() {
        // given information to create a user
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val passwordHash = userDomain.encodePassword(generateSecurePassword())

        // when creating a user
        createUser(username, email, country, passwordHash)

        // when getting the user by name
        val userByName = getUserByName(username)

        // then the user is retrieved successfully
        assertNotNull(userByName)
        assertEquals(username, userByName.name)
        assertEquals(email, userByName.email)
        assertEquals(country, userByName.country)
        assertEquals(passwordHash, userByName.passwordHash)
        assertFalse(userByName.privacy)
        assertEquals(emptyList(), userByName.intolerances)
        assertEquals(emptyList(), userByName.diets)
        assertNull(userByName.profilePictureName)
    }
}
