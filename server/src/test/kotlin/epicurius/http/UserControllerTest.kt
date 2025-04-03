package epicurius.http

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.exceptions.FollowRequestAlreadyBeenSent
import epicurius.domain.exceptions.IncorrectPassword
import epicurius.domain.exceptions.InvalidCountry
import epicurius.domain.exceptions.PasswordsDoNotMatch
import epicurius.domain.exceptions.UserAlreadyBeingFollowed
import epicurius.domain.exceptions.UserAlreadyExists
import epicurius.domain.exceptions.UserAlreadyLoggedIn
import epicurius.domain.exceptions.UserNotFound
import epicurius.domain.user.FollowUser
import epicurius.domain.user.FollowingUser
import epicurius.domain.user.SearchUser
import epicurius.domain.user.UserDomain
import epicurius.http.utils.Problem
import epicurius.http.utils.Regex.VALID_PASSWORD_MSG
import epicurius.http.utils.Regex.VALID_STRING_MSG
import epicurius.http.utils.Uris
import epicurius.http.utils.get
import epicurius.http.utils.getBody
import epicurius.http.utils.patch
import epicurius.http.utils.post
import epicurius.utils.createTestUser
import epicurius.utils.generateEmail
import epicurius.utils.generateRandomUsername
import epicurius.utils.generateSecurePassword
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UserControllerTest : HttpTest() {

    @Test
    fun `Retrieve its own user profile successfully`() {
        // given an existing logged-in user
        val username = generateRandomUsername()
        val token = signUp(
            username = username,
            email = generateEmail(username),
            country = "PT",
            password = generateSecurePassword()
        )

        // when retrieving the user profile
        val body = getUserProfile(token)

        // then the user profile is retrieved successfully
        assertNotNull(body)
        assertEquals(username, body.userProfile.username)
        assertEquals("PT", body.userProfile.country)
        assertFalse(body.userProfile.privacy)
        assertNull(body.userProfile.profilePicture)
        assertTrue(body.userProfile.followers.isEmpty())
        assertTrue(body.userProfile.following.isEmpty())
    }

    @Test
    fun `Retrieve another user profile successfully`() {
        // given an existing logged-in user and another user
        val username = generateRandomUsername()
        val token = signUp(
            username = username,
            email = generateEmail(username),
            country = "PT",
            password = generateSecurePassword()
        )
        val user = createTestUser(tm)

        // when retrieving the user profile
        val body = getUserProfile(token, user.username)

        // then the user profile is retrieved successfully
        assertNotNull(body)
        assertEquals(user.username, body.userProfile.username)
        assertEquals(user.country, body.userProfile.country)
        assertEquals(user.privacy, body.userProfile.privacy)
        assertNull(body.userProfile.profilePicture)
        assertTrue(body.userProfile.followers.isEmpty())
        assertTrue(body.userProfile.following.isEmpty())
    }

    @Test
    fun `Try to retrieve a profile from a non-existing user and fails with code 404`() {
        // given an existing logged-in user
        val username = generateRandomUsername()
        val token = signUp(
            username = username,
            email = generateEmail(username),
            country = "PT",
            password = generateSecurePassword()
        )

        // when trying to retrieve a profile from a non-existing user
        val error = get<Problem>(
            client,
            api(Uris.User.USER_PROFILE) + "?username=nonExistingUser",
            HttpStatus.NOT_FOUND,
            token
        )

        // then the user profile is not retrieved and an error is returned with the UserNotFound message
        assertNotNull(error)
        assertEquals(UserNotFound("nonExistingUser").message, error.detail)
    }

    @Test
    fun `Retrieves 2 users successfully`() {
        // given 2 existing users
        val username = "partial"
        val username2 = "partialUsername"
        val email = generateEmail(username)
        val email2 = generateEmail(username2)
        val country = "PT"
        val password = generateSecurePassword()
        val token = signUp(username, email, country, password)
        signUp(username2, email2, country, password)

        // when getting the users
        val result = getUsers(token, username)

        // then the users are retrieved successfully
        assertNotNull(result)
        assertEquals(2, result.users.size)
        assertTrue(result.users.contains(SearchUser(username, null)))
        assertTrue(result.users.contains(SearchUser(username2, null)))
    }

    @Test
    fun `Retrieve the intolerances of the user successfully`() {
        // given an existing logged-in user
        val username = generateRandomUsername()
        val token = signUp(
            username = username,
            email = generateEmail(username),
            country = "PT",
            password = generateSecurePassword()
        )

        // when retrieving the intolerances
        val body = getIntolerances(token)

        // then the intolerances are retrieved successfully
        assertNotNull(body)
        assertTrue(body.intolerances.isEmpty())
    }

    @Test
    fun `Retrieve the diets of the user successfully`() {
        // given an existing logged-in user
        val username = generateRandomUsername()
        val token = signUp(
            username = username,
            email = generateEmail(username),
            country = "PT",
            password = generateSecurePassword()
        )

        // when retrieving the diets
        val body = getDiets(token)

        // then the diets are retrieved successfully
        assertNotNull(body)
        assertTrue(body.diet.isEmpty())
    }

    @Test
    fun `Create new user and retrieve it successfully`() {
        // given user required information
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val password = generateSecurePassword()

        // when creating a user
        val token = signUp(username, email, country, password)

        // when getting the user
        val user = getUser(token)

        // then the user is retrieved successfully
        assertNotNull(user)
        assertEquals(user.user.username, username)
        assertEquals(user.user.email, email)
        assertEquals(user.user.country, country)
        assertEquals(user.user.privacy, false)
        assertEquals(user.user.intolerances, emptyList())
        assertEquals(user.user.diets, emptyList())
        assertNull(user.user.profilePictureName)
    }

    @Test
    fun `Try to create a user with existing name or email and fails with code 400`() {
        // given information for a new user and an existing user
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val password = generateSecurePassword()
        val country = "PT"

        val existingUser = publicTestUser

        // when trying to create a user with the same username
        val usernameError = post<Problem>(
            client,
            api(Uris.User.SIGNUP),
            mapOf(
                "username" to existingUser.username,
                "email" to email,
                "password" to password,
                "confirmPassword" to password,
                "country" to country
            ),
            HttpStatus.BAD_REQUEST
        )

        // then the user is not created
        assertNotNull(usernameError)
        val usernameErrorBody = getBody(usernameError)
        assertNotNull(usernameErrorBody)
        assertEquals(UserAlreadyExists().message, usernameErrorBody.detail)

        // when trying to create a user with the same email
        val emailError = post<Problem>(
            client,
            api(Uris.User.SIGNUP),
            mapOf(
                "username" to username,
                "email" to existingUser.email,
                "password" to password,
                "confirmPassword" to password,
                "country" to country
            ),
            HttpStatus.BAD_REQUEST
        )

        // then the user is not created
        assertNotNull(emailError)
        val emailErrorBody = getBody(emailError)
        assertNotNull(emailErrorBody)
        assertEquals(UserAlreadyExists().message, emailErrorBody.detail)

        // when trying to create a user with the same username and email
        val error = post<Problem>(
            client,
            api(Uris.User.SIGNUP),
            mapOf(
                "username" to existingUser.username,
                "email" to existingUser.email,
                "password" to password,
                "confirmPassword" to password,
                "country" to country
            ),
            HttpStatus.BAD_REQUEST
        )

        // then the user is not created
        assertNotNull(error)
        val errorBody = getBody(error)
        assertNotNull(errorBody)
        assertEquals(UserAlreadyExists().message, errorBody.detail)
    }

    @Test
    fun `Try to create a user with invalid country and fails with code 200`() {
        // given information for a new user
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val password = generateSecurePassword()
        val country = "XX"

        // when trying to create a user with an invalid country
        val error = post<Problem>(
            client,
            api(Uris.User.SIGNUP),
            mapOf(
                "username" to username,
                "email" to email,
                "password" to password,
                "confirmPassword" to password,
                "country" to country
            ),
            HttpStatus.BAD_REQUEST
        )

        // then the user is not created
        assertNotNull(error)
        val errorBody = getBody(error)
        assertNotNull(errorBody)
        assertEquals(InvalidCountry().message, errorBody.detail)
    }

    @Test
    fun `Try to create a user with different passwords and fails with code 400`() {
        // given information for a new user
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val password = generateSecurePassword()
        val country = "PT"

        // when trying to create a user with an invalid password
        val error = post<Problem>(
            client,
            api(Uris.User.SIGNUP),
            mapOf(
                "username" to username,
                "email" to email,
                "password" to password,
                "confirmPassword" to generateSecurePassword(),
                "country" to country
            ),
            HttpStatus.BAD_REQUEST
        )

        // then the user is not created
        assertNotNull(error)
        val errorBody = getBody(error)
        assertNotNull(errorBody)
        assertEquals(PasswordsDoNotMatch().message, errorBody.detail)
    }

    @Test
    fun `Logout an user successfully and then login him by name successfully`() {
        // given an existing user logged out
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val password = generateSecurePassword()
        val token = signUp(username, email, country, password)
        assertNotNull(token)

        val oldToken = logout(token)
        assertTrue(oldToken.isEmpty())

        // when logging in
        val newToken = login(username = username, password = password)
        assertNotNull(newToken)

        // then the user is logged in successfully
        val authenticatedUser = getUser(newToken)
        assertNotNull(authenticatedUser)
        assertEquals(username, authenticatedUser.user.username)
        assertEquals(email, authenticatedUser.user.email)
        assertEquals(country, authenticatedUser.user.country)
    }

    @Test
    fun `Logout an user successfully and then login him by email successfully`() {
        // given an existing user logged out
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val password = generateSecurePassword()
        val token = signUp(username, email, country, password)
        assertNotNull(token)

        val oldToken = logout(token)
        assertTrue(oldToken.isEmpty())

        // when logging in
        val newToken = login(email = email, password = password)
        assertNotNull(newToken)

        // then the user is logged in successfully
        val authenticatedUser = getUser(newToken)
        assertNotNull(authenticatedUser)
        assertEquals(username, authenticatedUser.user.username)
        assertEquals(email, authenticatedUser.user.email)
        assertEquals(country, authenticatedUser.user.country)
    }

    @Test
    fun `Try to login an user with a non-existing username and fails with code 404`() {
        // given a non-existing username
        val username = generateRandomUsername()
        val password = generateSecurePassword()

        // when trying to login with a non-existing username
        val error = post<Problem>(
            client,
            api(Uris.User.LOGIN),
            mapOf("username" to username, "password" to password),
            HttpStatus.NOT_FOUND
        )
        assertNotNull(error)

        // then the user is not logged in and an error is returned with the UserNotFound message

        val errorBody = getBody(error)
        assertNotNull(errorBody)
        assertEquals(UserNotFound(username).message, errorBody.detail)
    }

    @Test
    fun `Try to login an user with a non-existing email and fails with code 404`() {
        // given a non-existing username
        val email = generateEmail("user")
        val password = generateSecurePassword()

        // when trying to login with a non-existing username
        val error = post<Problem>(
            client,
            api(Uris.User.LOGIN),
            mapOf("email" to email, "password" to password),
            HttpStatus.NOT_FOUND
        )
        assertNotNull(error)

        // then the user is not logged in and an error is returned with the UserNotFound message
        val errorBody = getBody(error)
        assertNotNull(errorBody)
        assertEquals(UserNotFound(email).message, errorBody.detail)
    }

    @Test
    fun `Try to login an already logged in user with and fails with code 400`() {
        // given a logged-in user
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val password = generateSecurePassword()
        signUp(username, email, country, password)

        // when trying to login again
        val error = post<Problem>(
            client,
            api(Uris.User.LOGIN),
            mapOf("username" to username, "password" to password),
            HttpStatus.BAD_REQUEST
        )
        assertNotNull(error)

        // then the user was already logged in and an error is returned
        val errorBody = getBody(error)
        assertNotNull(errorBody)
        assertEquals(UserAlreadyLoggedIn().message, errorBody.detail)
    }

    @Test
    fun `Try to login an user with a different password and fails with code 401`() {
        // given an existing user
        val user = createTestUser(tm)

        // when trying to login with a different password
        val error = post<Problem>(
            client,
            api(Uris.User.LOGIN),
            mapOf("username" to user.username, "password" to generateSecurePassword()),
            HttpStatus.BAD_REQUEST
        )
        assertNotNull(error)

        // then the user is not logged in and an error is returned
        val errorBody = getBody(error)
        assertNotNull(errorBody)
        assertEquals(IncorrectPassword().message, errorBody.detail)
    }

    @Test
    fun `Try to login with an invalid username and fails with code 400`() {
        // given invalids usernames
        val usernameToShort = "ab"
        val usernameToLong = "wPIETGFH29THshfgOPHohasfn21h"
        val invalidUsernameString = "/-+==;:"

        // when trying to login with an invalid username
        val errorWithShortUsername = post<Problem>(
            client,
            api(Uris.User.LOGIN),
            mapOf("username" to usernameToShort, "password" to generateSecurePassword()),
            HttpStatus.BAD_REQUEST
        )
        assertNotNull(errorWithShortUsername)

        val errorWithLongUsername = post<Problem>(
            client,
            api(Uris.User.LOGIN),
            mapOf("username" to usernameToLong, "password" to generateSecurePassword()),
            HttpStatus.BAD_REQUEST
        )
        assertNotNull(errorWithLongUsername)

        val errorWithInvalidUsernameString = post<Problem>(
            client,
            api(Uris.User.LOGIN),
            mapOf("username" to invalidUsernameString, "password" to generateSecurePassword()),
            HttpStatus.BAD_REQUEST
        )
        assertNotNull(errorWithInvalidUsernameString)

        // then the user is not logged in and an error is returned
        val errorBodyWithShortUsername = getBody(errorWithShortUsername)
        val errorBodyWithLongUsername = getBody(errorWithLongUsername)
        val errorBodyWithInvalidUsernameString = getBody(errorWithInvalidUsernameString)
        assertNotNull(errorBodyWithShortUsername)
        assertNotNull(errorBodyWithLongUsername)
        assertNotNull(errorBodyWithInvalidUsernameString)
        assertEquals("Username " + UserDomain.USERNAME_LENGTH_MSG, errorBodyWithShortUsername.detail)
        assertEquals("Username " + UserDomain.USERNAME_LENGTH_MSG, errorBodyWithLongUsername.detail)
        assertEquals("Username " + VALID_STRING_MSG, errorBodyWithInvalidUsernameString.detail)
    }

    @Test
    fun `Try to login with an invalid email and fails with code 400`() {
        // given invalids emails
        val invalidEmail = "invalidEmail"
        val invalidEmail2 = "invalidEmail@"

        // when trying to login with an invalid email
        val errorWithInvalidEmail = post<Problem>(
            client,
            api(Uris.User.LOGIN),
            mapOf("email" to invalidEmail, "password" to generateSecurePassword()),
            HttpStatus.BAD_REQUEST
        )
        assertNotNull(errorWithInvalidEmail)

        val errorWithInvalidEmail2 = post<Problem>(
            client,
            api(Uris.User.LOGIN),
            mapOf("email" to invalidEmail2, "password" to generateSecurePassword()),
            HttpStatus.BAD_REQUEST
        )
        assertNotNull(errorWithInvalidEmail2)

        // then the user is not logged in and an error is returned
        val errorBodyWithInvalidEmail = getBody(errorWithInvalidEmail)
        val errorBodyWithInvalidEmail2 = getBody(errorWithInvalidEmail2)
        assertNotNull(errorBodyWithInvalidEmail)
        assertNotNull(errorBodyWithInvalidEmail2)
        assertEquals("Email " + UserDomain.VALID_EMAIL_MSG, errorBodyWithInvalidEmail.detail)
        assertEquals("Email " + UserDomain.VALID_EMAIL_MSG, errorBodyWithInvalidEmail2.detail)
    }

    @Test
    fun `Try to login with an invalid password and fails with code 400`() {
        // given an existing user
        val user = createTestUser(tm)

        // when trying to login with an invalid password
        val errorWithInvalidPassword = post<Problem>(
            client,
            api(Uris.User.LOGIN),
            mapOf("username" to user.username, "password" to "invalidPassword"),
            HttpStatus.BAD_REQUEST
        )
        assertNotNull(errorWithInvalidPassword)

        // then the user is not logged in and an error is returned
        val errorBodyWithInvalidPassword = getBody(errorWithInvalidPassword)
        assertNotNull(errorBodyWithInvalidPassword)
        assertEquals("Password " + VALID_PASSWORD_MSG, errorBodyWithInvalidPassword.detail)
    }

    @Test
    fun `Reset password successfully`() {
        // given an existing user
        val user = publicTestUser

        // when resetting the password
        val newPassword = generateSecurePassword()
        resetPassword(publicTestUser.email, newPassword, newPassword)

        // then the password is reset successfully
        val newToken = login(email = user.email, password = newPassword)
        assertNotNull(newToken)

        // then the user is logged in successfully
        val authenticatedUser = getUser(newToken)
        assertNotNull(authenticatedUser)
        assertEquals(publicTestUser.username, authenticatedUser.user.username)
        assertEquals(publicTestUser.email, authenticatedUser.user.email)
    }

    @Test
    fun `Try to reset password with different passwords and fails with code 400`() {
        // given an existing user
        val user = publicTestUser

        // when trying to reset the password with different passwords
        val newPassword = generateSecurePassword()
        val error = patch<Problem>(
            client,
            api(Uris.User.USER_RESET_PASSWORD),
            mapOf(
                "email" to user.email,
                "newPassword" to newPassword,
                "confirmPassword" to generateSecurePassword()
            ),
            HttpStatus.BAD_REQUEST
        )

        // then the password is not reset and an error is returned
        assertNotNull(error)
        val errorBody = getBody(error)
        assertNotNull(errorBody)
        assertEquals(PasswordsDoNotMatch().message, errorBody.detail)
    }

    @Test
    fun `Update user successfully`() {
        // given information for a new user
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val password = generateSecurePassword()
        val token = signUp(username, email, country, password)
        assertNotNull(token)

        // when updating the user
        val newUsername = generateRandomUsername()
        val newEmail = generateEmail(newUsername)
        val newCountry = "ES"
        val newPassword = generateSecurePassword()
        val newIntolerances = listOf(Intolerance.SOY)
        val newDiets = listOf(Diet.WHOLE30)

        val updatedUser = updateUser(
            token = token,
            username = newUsername,
            email = newEmail,
            country = newCountry,
            password = newPassword,
            confirmPassword = newPassword,
            privacy = true,
            intolerances = newIntolerances,
            diets = newDiets
        )

        // then the user is updated successfully
        assertNotNull(updatedUser)
        assertEquals(newUsername, updatedUser.userInfo.username)
        assertEquals(newEmail, updatedUser.userInfo.email)
        assertEquals(newCountry, updatedUser.userInfo.country)
        assertTrue(updatedUser.userInfo.privacy)
        assertEquals(newIntolerances, updatedUser.userInfo.intolerances)
        assertEquals(newDiets, updatedUser.userInfo.diets)

        // when logging out
        logout(token)

        // when logging in with the new username and password
        val newToken = login(username = newUsername, password = newPassword)
        assertNotNull(newToken)

        // then the user is logged in successfully with the new password
        val authenticatedUser = getUser(newToken)
        assertNotNull(authenticatedUser)
        assertEquals(newUsername, authenticatedUser.user.username)
    }

    @Test
    fun `Try to update user with existing username or email and fails with code 400`() {
        // given information for a new user
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val password = generateSecurePassword()
        val token = signUp(username, email, country, password)
        assertNotNull(token)

        // given information for an existing user
        val existingUser = publicTestUser

        // when trying to update the user with an existing username
        val usernameError = patch<Problem>(
            client,
            api(Uris.User.USER),
            mapOf(
                "username" to existingUser.username
            ),
            HttpStatus.BAD_REQUEST,
            token
        )

        // then the user is not updated
        assertNotNull(usernameError)
        val usernameErrorBody = getBody(usernameError)
        assertNotNull(usernameErrorBody)
        assertEquals(UserAlreadyExists().message, usernameErrorBody.detail)

        // when trying to update the user with an existing email
        val emailError = patch<Problem>(
            client,
            api(Uris.User.USER),
            mapOf(
                "email" to existingUser.email
            ),
            HttpStatus.BAD_REQUEST,
            token
        )

        // then the user is not updated
        assertNotNull(emailError)
        val emailErrorBody = getBody(emailError)
        assertNotNull(emailErrorBody)
        assertEquals(UserAlreadyExists().message, emailErrorBody.detail)

        // when trying to update the user with an existing username and email
        val error = patch<Problem>(
            client,
            api(Uris.User.USER),
            mapOf(
                "username" to existingUser.username,
                "email" to existingUser.email
            ),
            HttpStatus.BAD_REQUEST,
            token
        )

        // then the user is not updated
        assertNotNull(error)
        val errorBody = getBody(error)
        assertNotNull(errorBody)
        assertEquals(UserAlreadyExists().message, errorBody.detail)
    }

    @Test
    fun `Try to update user with invalid country and fails with code 400`() {
        // given information for a new user
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val password = generateSecurePassword()
        val token = signUp(username, email, country, password)
        assertNotNull(token)

        // when updating the user with an invalid country
        val error = patch<Problem>(
            client,
            api(Uris.User.USER),
            mapOf(
                "country" to "XX"
            ),
            HttpStatus.BAD_REQUEST,
            token
        )

        // then the user is not updated
        assertNotNull(error)
        val errorBody = getBody(error)
        assertNotNull(errorBody)
        assertEquals(InvalidCountry().message, errorBody.detail)
    }

    @Test
    fun `Try to update user with different passwords and fails with code 400`() {
        // given information for a new user
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val password = generateSecurePassword()
        val token = signUp(username, email, country, password)
        assertNotNull(token)

        // when updating the user with different passwords
        val error = patch<Problem>(
            client,
            api(Uris.User.USER),
            mapOf(
                "password" to password,
                "confirmPassword" to generateSecurePassword()
            ),
            HttpStatus.BAD_REQUEST,
            token
        )

        // then the user is not updated
        assertNotNull(error)
        val errorBody = getBody(error)
        assertNotNull(errorBody)
        assertEquals(PasswordsDoNotMatch().message, errorBody.detail)
    }

    @Test
    fun `Follow a public user, unfollows him and then retrieve its followers and following successfully`() {
        // given two existing users
        val publicUsername = generateRandomUsername()
        val publicUserToken = signUp(
            username = publicUsername,
            email = generateEmail(publicUsername),
            country = "PT",
            password = generateSecurePassword()
        )
        val publicUsername2 = generateRandomUsername()
        val publicUserToken2 = signUp(
            username = publicUsername2,
            email = generateEmail(publicUsername2),
            country = "PT",
            password = generateSecurePassword()
        )

        // when following a public user
        follow(publicUserToken2, publicUsername)

        // then the user is followed successfully
        val publicUserFollowers = getFollowers(publicUserToken)
        val privateUserFollowing = getFollowing(publicUserToken2)
        assertNotNull(publicUserFollowers)
        assertNotNull(privateUserFollowing)
        assertTrue(publicUserFollowers.users.isNotEmpty())
        assertTrue(privateUserFollowing.users.isNotEmpty())
        assertEquals(publicUserFollowers.users.size, 1)
        assertEquals(privateUserFollowing.users.size, 1)
        assertTrue(publicUserFollowers.users.contains(FollowUser(publicUsername2, null)))
        assertTrue(privateUserFollowing.users.contains(FollowingUser(publicUsername, null)))

        // when unfollowing the user
        unfollow(publicUserToken2, publicUsername)

        // then the user is unfollowed successfully
        val publicUserFollowersAfterUnfollow = getFollowers(publicUserToken)
        val privateUserFollowingAfterUnfollow = getFollowing(publicUserToken2)
        assertNotNull(publicUserFollowersAfterUnfollow)
        assertNotNull(privateUserFollowingAfterUnfollow)
        assertTrue(publicUserFollowersAfterUnfollow.users.isEmpty())
        assertTrue(privateUserFollowingAfterUnfollow.users.isEmpty())
    }

    @Test
    fun `Try to follow a private user, get added to its follow requests and then cancel the request successfully`() {
        // given two existing users
        val publicUsername = generateRandomUsername()
        val publicUserToken = signUp(
            username = publicUsername,
            email = generateEmail(publicUsername),
            country = "PT",
            password = generateSecurePassword()
        )
        val privateUsername = generateRandomUsername()
        val privateUserToken = signUp(
            username = privateUsername,
            email = generateEmail(privateUsername),
            country = "PT",
            password = generateSecurePassword()
        )
        updateUser(privateUserToken, privacy = true)

        // when following a private user
        follow(publicUserToken, privateUsername)

        // then the follow request is sent successfully
        val privateUserFollowRequests = getFollowRequests(privateUserToken)
        assertNotNull(privateUserFollowRequests)
        assertTrue(privateUserFollowRequests.users.isNotEmpty())
        assertEquals(privateUserFollowRequests.users.size, 1)
        assertTrue(privateUserFollowRequests.users.contains(FollowUser(publicUsername, null)))

        // when cancelling the follow request
        cancelFollowRequest(publicUserToken, privateUsername)

        // then the follow request is cancelled successfully
        val privateUserFollowRequestsAfterCancel = getFollowRequests(privateUserToken)
        assertNotNull(privateUserFollowRequestsAfterCancel)
        assertTrue(privateUserFollowRequestsAfterCancel.users.isEmpty())
    }

    @Test
    fun `Try to follow a non-existing user and fails with code 404`() {
        // given an existing user
        val publicUserUsername = generateRandomUsername()
        val publicUserToken =
            signUp(publicUserUsername, generateEmail(publicUserUsername), "PT", generateSecurePassword())

        // when trying to follow a non-existing user
        val error = patch<Problem>(
            client,
            api(Uris.User.USER_FOLLOW),
            mapOf("username" to "nonExistingUser"),
            HttpStatus.NOT_FOUND,
            publicUserToken
        )
        assertNotNull(error)

        // then the user is not followed and an error is returned with the UserNotFound message
        val errorBody = getBody(error)
        assertNotNull(errorBody)
        assertEquals(UserNotFound("nonExistingUser").message, errorBody.detail)
    }

    @Test
    fun `Try to follow a user twice and fails with code 400`() {
        // given two existing users
        val publicUsername = generateRandomUsername()
        val publicUserToken = signUp(
            username = publicUsername,
            email = generateEmail(publicUsername),
            country = "PT",
            password = generateSecurePassword()
        )
        val publicUser2 = createTestUser(tm)

        // when following a user twice
        follow(publicUserToken, publicUser2.username)

        val error = patch<Problem>(
            client,
            api(Uris.User.USER_FOLLOW),
            mapOf("username" to publicUser2.username),
            HttpStatus.BAD_REQUEST,
            publicUserToken
        )

        // then an error is returned with the UserAlreadyFollowed message
        val errorBody = getBody(error)
        assertNotNull(errorBody)
        assertEquals(UserAlreadyBeingFollowed(publicUser2.username).message, errorBody.detail)
    }

    @Test
    fun `Try to follow a private user twice and fails with code 400`() {
        // given two existing users
        val publicUsername = generateRandomUsername()
        val publicUserToken = signUp(
            username = publicUsername,
            email = generateEmail(publicUsername),
            country = "PT",
            password = generateSecurePassword()
        )
        val privateUsername = generateRandomUsername()
        val privateUserToken = signUp(
            username = privateUsername,
            email = generateEmail(privateUsername),
            country = "PT",
            password = generateSecurePassword()
        )
        updateUser(privateUserToken, privacy = true)

        // when following a private user twice
        follow(publicUserToken, privateUsername)

        val error = patch<Problem>(
            client,
            api(Uris.User.USER_FOLLOW),
            mapOf("username" to privateUsername),
            HttpStatus.BAD_REQUEST,
            publicUserToken
        )

        // then an error is returned with the UserAlreadyFollowed message
        val errorBody = getBody(error)
        assertNotNull(errorBody)
        assertEquals(FollowRequestAlreadyBeenSent(privateUsername).message, errorBody.detail)
    }
}
