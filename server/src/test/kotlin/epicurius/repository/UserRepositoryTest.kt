package epicurius.repository

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class UserRepositoryTest: RepositoryTest() {

    @Test
    fun `Create new user and retrieve it successfully`() {
        // given user required information
        val username = "test${Math.random()}"
        val email = "$username@email.com"
        val country = "PT"
        val passwordHash = usersDomain.encodePassword(UUID.randomUUID().toString())

        // when creating a user
        createUser(username, email, country, passwordHash)

        // when getting the user by name
        val userByName = getUserByName(username)

        // when getting the user by email
        val userByEmail = getUserByEmail(email)

        // then the user is retrieved successfully
        assertEquals(userByName.username, username)
        assertEquals(userByEmail.username, username)
        assertEquals(userByName.email, email)
        assertEquals(userByEmail.email, email)
    }
}
