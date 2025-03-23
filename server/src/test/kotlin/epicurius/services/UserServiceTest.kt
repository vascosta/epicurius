package epicurius.services

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.exceptions.IncorrectPassword
import epicurius.domain.exceptions.UserAlreadyLoggedIn
import epicurius.domain.exceptions.UserNotFound
import epicurius.http.user.models.UpdateUserInputModel
import epicurius.utils.generateEmail
import epicurius.utils.generateRandomUsername
import epicurius.utils.generateSecurePassword
import org.junit.jupiter.api.Assertions.assertFalse
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UserServiceTest: ServicesTest() {

    @Test
    fun `Create new user and retrieve it successfully`() {
        // given user required information
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val password = generateSecurePassword()

        // when creating a user
        val token = createUser(username, email, country, password)

        // when getting the authenticated user
        val userByName = getAuthenticatedUser(token)?.userInfo

        // then the user is retrieved successfully
        assertNotNull(userByName)
        assertEquals(userByName.username, username)
        assertEquals(userByName.email, email)
        assertEquals(userByName.country, country)
        assertTrue(usersDomain.verifyPassword(password, userByName.passwordHash))
        assertEquals(userByName.privacy, false)
        assertEquals(userByName.intolerances, emptyList())
        assertEquals(userByName.diet, emptyList())
        assertNull(userByName.profilePictureName)
    }

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
        assertEquals(user.username, authenticatedUser.userInfo.username)
        assertEquals(user.email, authenticatedUser.userInfo.email)
        assertTrue(usersDomain.verifyPassword(user.password, authenticatedUser.userInfo.passwordHash))
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
        assertEquals(user.username, authenticatedUser.userInfo.username)
        assertEquals(user.email, authenticatedUser.userInfo.email)
        assertTrue(usersDomain.verifyPassword(user.password, authenticatedUser.userInfo.passwordHash))
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
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val password = generateSecurePassword()
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
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val password = generateSecurePassword()
        val passwordHash = usersDomain.encodePassword(password)
        val userToken = createUser(username, email, country, passwordHash)

        // when logging out
        logout(username)

        // then the user is logged out successfully
        val authenticatedUser = getAuthenticatedUser(userToken)
        assertNull(authenticatedUser)
    }

    @Test
    fun `Reset password successfully`() {
        // given user required information
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val password = generateSecurePassword()

        // when creating a user with a random password
        createUser(username, email, country, password)

        // when resetting the password
        val newPassword = UUID.randomUUID().toString()
        resetPassword(email, newPassword, newPassword)

        // when logging in with the new password
        val userToken = login(null, email, newPassword)

        // then the password is reset successfully
        val authenticatedUser = getAuthenticatedUser(userToken)
        assertNotNull(authenticatedUser)
        assertEquals(authenticatedUser.userInfo.username, username)
        assertEquals(authenticatedUser.userInfo.email, email)
        assertTrue(usersDomain.verifyPassword(newPassword, authenticatedUser.userInfo.passwordHash))
        assertFalse(usersDomain.verifyPassword(password, authenticatedUser.userInfo.passwordHash))
    }

    @Test
    fun `Update user profile successfully`() {
        // given user required information
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val password = generateSecurePassword()
        val passwordHash = usersDomain.encodePassword(password)

        // when creating a user
        val token = createUser(username, email, country, passwordHash)

        // when updating the user profile
        val newUsername = generateRandomUsername()
        val newEmail = generateEmail(newUsername)
        val newCountry = "ES"
        val newPassword = generateSecurePassword()
        val newPrivacy = true
        val newIntolerances = listOf(Intolerance.GLUTEN)
        val newDiet = listOf(Diet.VEGAN)

        updateProfile(
            username, UpdateUserInputModel(
                username = newUsername,
                email = newEmail,
                country = newCountry,
                password = newPassword,
                confirmPassword = newPassword,
                privacy = newPrivacy,
                intolerances = newIntolerances,
                diet = newDiet
            )
        )

        // when getting the user by name
        val user = getAuthenticatedUser(token)?.userInfo

        // then the user profile is updated successfully
        assertNotNull(user)
        assertEquals(user.username, newUsername)
        assertEquals(user.email, newEmail)
        assertEquals(user.country, newCountry)
        assertTrue(usersDomain.verifyPassword(newPassword, user.passwordHash))
        assertEquals(user.privacy, newPrivacy)
        assertEquals(user.intolerances, newIntolerances)
        assertEquals(user.diet, newDiet)
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