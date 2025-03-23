package epicurius.services

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.exceptions.IncorrectPassword
import epicurius.domain.exceptions.InvalidCountry
import epicurius.domain.exceptions.PasswordsDoNotMatch
import epicurius.domain.exceptions.UserAlreadyExists
import epicurius.domain.exceptions.UserAlreadyLoggedIn
import epicurius.domain.exceptions.UserNotFound
import epicurius.http.user.models.input.UpdateUserInputModel
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
    fun `create new user and retrieve it successfully`() {
        // given user required information
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val password = generateSecurePassword()

        // when creating a user
        val token = createUser(username, email, country, password, password)

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
    fun `try to create user with an existing username or email and throws UserAlreadyExists Exception`() {
        // given an existing user and a different username and email
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val password = generateSecurePassword()
        val randomUsername = generateRandomUsername()
        val randomEmail= generateEmail(username)

        createUser(username, email, country, password, password)

        // when creating a user with an existing username
        // then the user cannot be created and throws UserAlreadyExists Exception
        assertFailsWith<UserAlreadyExists> {
            createUser(username, randomEmail, "PT", password, password)
        }

        // when creating a user with an existing email
        // then the user cannot be created and throws UserAlreadyExists Exception
        assertFailsWith<UserAlreadyExists> {
            createUser(randomUsername, email, "PT", password, password)
        }

        // when creating a user with an existing username and email
        // then the user cannot be created and throws UserAlreadyExists Exception
        assertFailsWith<UserAlreadyExists> {
            createUser(username, email, "PT", password, password)
        }
    }

    @Test
    fun `try to create user with an invalid country and throws InvalidCountry Exception`() {
        // given an invalid country
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "XX"
        val password = generateSecurePassword()

        // when creating a user with an invalid country
        // then the user cannot be created and throws InvalidCountry Exception
        assertFailsWith<InvalidCountry> { createUser(username, email, country, password, password) }
    }

    @Test
    fun `try to create user with different passwords and throws PasswordsDoNotMatch Exception`() {
        // given a different password and confirm password
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val password = generateSecurePassword()

        // when creating a user with different passwords
        // then the user cannot be created and throws PasswordsDoNotMatch Exception
        assertFailsWith<PasswordsDoNotMatch> {
            createUser(username, email, country, password, generateSecurePassword())
        }
    }

    @Test
    fun `login a user by name successfully`() {
        // given an existing user logged out
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val password = generateSecurePassword()

        createUser(username, email, country, password, password)
        logout(username)

        // when logging in by name
        val userToken = login(username, null, password)

        // then the user is logged in successfully
        val authenticatedUser = getAuthenticatedUser(userToken)
        assertNotNull(authenticatedUser)
        assertEquals(username, authenticatedUser.userInfo.username)
        assertEquals(email, authenticatedUser.userInfo.email)
        assertTrue(usersDomain.verifyPassword(password, authenticatedUser.userInfo.passwordHash))
    }

    @Test
    fun `login a user by email successfully`() {
        // given an existing user logged out
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val password = generateSecurePassword()

        createUser(username, email, country, password, password)
        logout(username)

        // when logging in by email
        val userToken = login(null, email, password)

        // then the user is logged in successfully
        val authenticatedUser = getAuthenticatedUser(userToken)
        assertNotNull(authenticatedUser)
        assertEquals(username, authenticatedUser.userInfo.username)
        assertEquals(email, authenticatedUser.userInfo.email)
        assertTrue(usersDomain.verifyPassword(password, authenticatedUser.userInfo.passwordHash))
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
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val password = generateSecurePassword()

        createUser(username, email, country, password, password)
        logout(username)
        val incorrectPassword = UUID.randomUUID().toString()

        // when logging in with an incorrect password
        // then the user is cannot be logged in and throws IncorrectPassword Exception
        assertFailsWith<IncorrectPassword> { login(username, null, incorrectPassword) }
        assertFailsWith<IncorrectPassword> { login(null, email, incorrectPassword) }
    }

    @Test
    fun `try to login with an already logged in user and throws UserAlreadyLoggedIn Exception`() {
        // given an existing logged in user
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val password = generateSecurePassword()
        createUser(username, email, country, password, password)

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
        val userToken = createUser(username, email, country, password, password)

        // when logging out
        logout(username)

        // then the user is logged out successfully
        val authenticatedUser = getAuthenticatedUser(userToken)
        assertNull(authenticatedUser)
    }

    @Test
    fun `reset password successfully`() {
        // given user required information
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val password = generateSecurePassword()

        // when creating a user with a random password
        createUser(username, email, country, password, password)

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
    fun `try to reset password with different passwords and throws PasswordsDoNotMatch Exception`() {
        // given an existing user
        val user = publicTestUser

        // when resetting the password with different passwords
        // then the password cannot be reset and throws PasswordsDoNotMatch Exception
        assertFailsWith<PasswordsDoNotMatch> {
            resetPassword(user.email, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        }
    }

    @Test
    fun `update user profile successfully`() {
        // given user required information
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val password = generateSecurePassword()

        // when creating a user
        val token = createUser(username, email, country, password, password)

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

    @Test
    fun `try to update user profile with existing username or email and throws UserAlreadyExists Exception`() {
        // given two existing users
        val user1 = publicTestUser
        val user2 = privateTestUser

        // when updating the user profile with an existing username
        // then the user profile cannot be updated and throws UserAlreadyExists Exception
        assertFailsWith<UserAlreadyExists> {
            updateProfile(
                user1.username, UpdateUserInputModel(
                    username = user2.username
                )
            )
        }

        // when updating the user profile with an existing email
        // then the user profile cannot be updated and throws UserAlreadyExists Exception
        assertFailsWith<UserAlreadyExists> {
            updateProfile(
                user1.username, UpdateUserInputModel(
                    email = user2.email
                )
            )
        }

        // when updating the user profile with an existing username and email
        // then the user profile cannot be updated and throws UserAlreadyExists Exception
        assertFailsWith<UserAlreadyExists> {
            updateProfile(
                user1.username, UpdateUserInputModel(
                    username = user2.username,
                    email = user2.email
                )
            )
        }
    }

    @Test
    fun `try to update user profile with an invalid country and throws InvalidCountry Exception`() {
        // given an existing user
        val user = publicTestUser

        // when updating the user profile with an invalid country
        // then the user profile cannot be updated and throws InvalidCountry Exception
        assertFailsWith<InvalidCountry> {
            updateProfile(
                user.username, UpdateUserInputModel(
                    country = "XX"
                )
            )
        }
    }

    @Test
    fun `try to update user profile with different passwords and throws PasswordsDoNotMatch Exception`() {
        // given an existing user
        val user = publicTestUser

        // when updating the user profile with different passwords
        // then the user profile cannot be updated and throws PasswordsDoNotMatch Exception
        assertFailsWith<PasswordsDoNotMatch> {
            updateProfile(
                user.username, UpdateUserInputModel(
                    password = UUID.randomUUID().toString(),
                    confirmPassword = UUID.randomUUID().toString()
                )
            )
        }
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