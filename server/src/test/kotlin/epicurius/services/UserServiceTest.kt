package epicurius.services

import epicurius.domain.exceptions.IncorrectPassword
import epicurius.domain.exceptions.UserAlreadyLoggedIn
import epicurius.domain.exceptions.UserNotFound
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue


class UserServiceTest: ServicesTest() {

    @Test
    fun `login a user by name successfully`() {
        // given an existing user logged out
        val user = publicTestUser
        logout(user.username)

        // when logging in
        val userToken = login(user.username, null, user.password)

        // then the user is logged in successfully
        val authenticatedUser = getAuthenticatedUser(userToken)
        assertNotNull(authenticatedUser)
        assertEquals(user.username, authenticatedUser.user.username)
        assertEquals(user.email, authenticatedUser.user.email)
        assertTrue(usersDomain.verifyPassword(user.password, authenticatedUser.user.passwordHash))
    }

    @Test
    fun `login a user by email successfully`() {
        // given an existing user logged out
        val user = publicTestUser
        logout(user.username)

        // when logging in
        val userToken = login(null, user.email, user.password)

        // then the user is logged in successfully
        val authenticatedUser = getAuthenticatedUser(userToken)
        assertNotNull(authenticatedUser)
        assertEquals(user.username, authenticatedUser.user.username)
        assertEquals(user.email, authenticatedUser.user.email)
        assertTrue(usersDomain.verifyPassword(user.password, authenticatedUser.user.passwordHash))
    }

    @Test
    fun `try to login with an non existing user and throws UserNotFound Exception`() {
        // given a non-existing username and email
        val username = ""
        val email = ""
        val password = ""

        // when logging in
        // then the user is cannot be logged in and throws UserNotFound Exception
        assertFailsWith<UserNotFound> { login(username, null, password) }
        assertFailsWith<UserNotFound> { login(null, email, password) }
    }

    @Test
    fun `try to login with an incorrect password and throws IncorrectPassword Exception`() {
        // given an existing user logged out and an incorrect password
        val user = publicTestUser
        logout(user.username)
        val incorrectPassword = UUID.randomUUID().toString()

        // when logging in with an incorrect password
        // then the user is cannot be logged in and throws IncorrectPassword Exception
        assertFailsWith<IncorrectPassword> { login(user.username, null, incorrectPassword) }
        assertFailsWith<IncorrectPassword> { login(null, user.email, incorrectPassword) }
    }

    @Test
    fun `try to login with an already logged in user and throws UserAlreadyLoggedIn Exception`() {
        // given an existing logged in user
        val username = "test${Math.random()}"
        val email = "$username@email.com"
        val country = "PT"
        val password = UUID.randomUUID().toString()
        val passwordHash = usersDomain.encodePassword(password)
        createUser(username, email, country, passwordHash)

        // when logging in
        // then the user is cannot be logged in and throws UserAlreadyLoggedIn Exception
        assertFailsWith<UserAlreadyLoggedIn> { login(username, null, password) }
        assertFailsWith<UserAlreadyLoggedIn> { login(null, email, password) }
    }

    @Test
    fun `logout a user successfully`() {
        // given an existing logged in user
        val username = "test${Math.random()}"
        val email = "$username@email.com"
        val country = "PT"
        val password = UUID.randomUUID().toString()
        val passwordHash = usersDomain.encodePassword(password)
        val userToken = createUser(username, email, country, passwordHash)

        // when logging out
        logout(username)

        // then the user is logged out successfully
        val authenticatedUser = getAuthenticatedUser(userToken)
        assertNull(authenticatedUser)
    }

//    @Test
//    fun `follow a public user successfully`() {
//        // given an existing user
//        val user = privateTestUser
//
//        // when following a public user
//        val publicUser = publicTestUser
//        follow(user.username, publicTestUser.username)
//    }
}