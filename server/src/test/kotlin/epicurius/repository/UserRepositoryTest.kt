package epicurius.repository

import kotlin.test.Test
import kotlin.test.assertEquals

class UserRepositoryTest: RepositoryTest() {

@Test
    fun `Getting user by name or email successfully`() {
        // given a user
        val user = testUser

        // when getting the user by name
        val userByName = getUserByName(user.first)

        // when getting the user by email
        val userByEmail = getUserByEmail(user.second)

        // then the user is retrieved successfully
        assertEquals(userByName.username, user.first)
        assertEquals(userByEmail.username, user.first)
        assertEquals(userByName.email, user.second)
        assertEquals(userByEmail.email, user.second)
    }



}
