package epicurius.http.user

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.exceptions.InvalidCountry
import epicurius.domain.exceptions.PasswordsDoNotMatch
import epicurius.domain.exceptions.UserAlreadyExists
import epicurius.domain.exceptions.UserNotFound
import epicurius.domain.user.SearchUser
import epicurius.http.HttpTest
import epicurius.http.utils.Problem
import epicurius.http.utils.Uris
import epicurius.http.utils.get
import epicurius.http.utils.getBody
import epicurius.http.utils.patch
import epicurius.utils.createTestUser
import epicurius.utils.generateEmail
import epicurius.utils.generateRandomUsername
import epicurius.utils.generateSecurePassword
import org.junit.jupiter.api.BeforeEach
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class UserControllerTest : HttpTest() {

    lateinit var publicTestUserToken: String
    lateinit var publicTestUsername: String

    @BeforeEach
    fun setup() {
        publicTestUsername = generateRandomUsername()
        publicTestUserToken = signUp(publicTestUsername, generateEmail(publicTestUsername), "PT", generateSecurePassword())
    }

    @Test
    fun `Retrieves 2 users successfully with code 200`() {
        // given 2 existing users
        val username = "partial"
        val username2 = "partialUsername"
        val email = generateEmail(username)
        val email2 = generateEmail(username2)
        val country = "PT"
        val password = generateSecurePassword()
        val userToken = signUp(username, email, country, password)
        signUp(username2, email2, country, password)

        // when getting the users
        val usersBody = getUsers(userToken, username)

        // then the users are retrieved successfully
        assertNotNull(usersBody)
        assertEquals(2, usersBody.users.size)
        assertTrue(usersBody.users.contains(SearchUser(username, null)))
        assertTrue(usersBody.users.contains(SearchUser(username2, null)))
    }

    @Test
    fun `Retrieve the intolerances of the user successfully with code 200`() {
        // given an existing logged-in user
        val userToken = publicTestUserToken

        // when retrieving the intolerances
        val intolerancesBody = getIntolerances(userToken)

        // then the intolerances are retrieved successfully
        assertNotNull(intolerancesBody)
        assertTrue(intolerancesBody.intolerances.isEmpty())
    }

    @Test
    fun `Retrieve the diets of the user successfully with code 200`() {
        // given an existing logged-in user
        val userToken = publicTestUserToken

        // when retrieving the diets
        val dietsBody = getDiets(userToken)

        // then the diets are retrieved successfully
        assertNotNull(dietsBody)
        assertTrue(dietsBody.diet.isEmpty())
    }

    @Test
    fun `Update user successfully with code 200`() {
        // given information for a new user
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val password = generateSecurePassword()
        val userToken = signUp(username, email, country, password)
        assertNotNull(userToken)

        // when updating the user
        val newUsername = generateRandomUsername()
        val newEmail = generateEmail(newUsername)
        val newCountry = "ES"
        val newPassword = generateSecurePassword()
        val newIntolerances = listOf(Intolerance.SOY)
        val newDiets = listOf(Diet.WHOLE30)

        val updatedUserBody = updateUser(
            token = userToken,
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
        assertNotNull(updatedUserBody)
        assertEquals(newUsername, updatedUserBody.userInfo.username)
        assertEquals(newEmail, updatedUserBody.userInfo.email)
        assertEquals(newCountry, updatedUserBody.userInfo.country)
        assertTrue(updatedUserBody.userInfo.privacy)
        assertEquals(newIntolerances, updatedUserBody.userInfo.intolerances)
        assertEquals(newDiets, updatedUserBody.userInfo.diets)

        // when logging out
        logout(userToken)

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
        val userToken = signUp(username, email, country, password)
        assertNotNull(userToken)

        // given information for an existing user
        val existingUser = createTestUser(tm)

        // when trying to update the user with an existing username
        val usernameError = patch<Problem>(
            client,
            api(Uris.User.USER),
            body = mapOf(
                "username" to existingUser.username
            ),
            responseStatus = HttpStatus.BAD_REQUEST,
            token = userToken
        )
        assertNotNull(usernameError)

        // then the user is not updated
        val usernameErrorBody = getBody(usernameError)
        assertNotNull(usernameErrorBody)
        assertEquals(UserAlreadyExists().message, usernameErrorBody.detail)

        // when trying to update the user with an existing email
        val emailError = patch<Problem>(
            client,
            api(Uris.User.USER),
            body = mapOf(
                "email" to existingUser.email
            ),
            responseStatus = HttpStatus.BAD_REQUEST,
            token = userToken
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
            body = mapOf(
                "username" to existingUser.username,
                "email" to existingUser.email
            ),
            responseStatus = HttpStatus.BAD_REQUEST,
            token = userToken
        )

        // then the user is not updated
        assertNotNull(error)
        val errorBody = getBody(error)
        assertNotNull(errorBody)
        assertEquals(UserAlreadyExists().message, errorBody.detail)
    }

    @Test
    fun `Try to update user with invalid country and fails with code 400`() {
        // given an existing user
        val userToken = publicTestUserToken

        // when updating the user with an invalid country
        val error = patch<Problem>(
            client,
            api(Uris.User.USER),
            body = mapOf(
                "country" to "XX"
            ),
            responseStatus = HttpStatus.BAD_REQUEST,
            token = userToken
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
        val userToken = publicTestUserToken

        // when updating the user with different passwords
        val error = patch<Problem>(
            client,
            api(Uris.User.USER),
            body = mapOf(
                "password" to generateSecurePassword(),
                "confirmPassword" to generateSecurePassword()
            ),
            responseStatus = HttpStatus.BAD_REQUEST,
            token = userToken
        )

        // then the user is not updated
        assertNotNull(error)
        val errorBody = getBody(error)
        assertNotNull(errorBody)
        assertEquals(PasswordsDoNotMatch().message, errorBody.detail)
    }

    @Test
    fun `Add a profile picture to an user and then retrieves the user profile successfully with code 200`() {
        // given an existing logged-in user
        val userToken = publicTestUserToken

        // when adding a profile picture
        val updateProfilePictureBody = updateProfilePicture(userToken, testProfilePicture)
        assertNotNull(updateProfilePictureBody)
        assertTrue(updateProfilePictureBody.profilePictureName.isNotBlank())

        // then the picture was added successfully
        val userProfileBody = getUserProfile(userToken, publicTestUsername)
        assertNotNull(userProfileBody)
        assertContentEquals(testProfilePicture.bytes, userProfileBody.userProfile.profilePicture)
        assertEquals(userProfileBody.userProfile.username, publicTestUsername)
    }

    @Test
    fun `Update the profile picture of an user and then retrieves the user profile successfully with code 200`() {
        // given an existing logged-in user with a profile picture
        val username = generateRandomUsername()
        val userToken = signUp(username, generateEmail(username), "PT", generateSecurePassword())
        val updateProfilePictureBody = updateProfilePicture(userToken, testProfilePicture)
        assertNotNull(updateProfilePictureBody)

        // when updating the profile picture
        val newUpdateProfilePictureBody = updateProfilePicture(userToken, testProfilePicture2)
        assertNotNull(newUpdateProfilePictureBody)

        // then the picture was updated successfully
        val userProfileBody = getUserProfile(userToken, username)
        assertNotNull(userProfileBody)
        assertEquals(newUpdateProfilePictureBody.profilePictureName, updateProfilePictureBody.profilePictureName)
        assertContentEquals(testProfilePicture2.bytes, userProfileBody.userProfile.profilePicture)
    }

    @Test
    fun `Retrieve another user profile successfully with code 200`() {
        // given an existing logged-in user and another user
        val existingUserToken = publicTestUserToken
        val username = generateRandomUsername()
        val userToken = signUp(username, generateEmail(username), "PT", generateSecurePassword())
        updateProfilePicture(userToken, testProfilePicture)

        // when retrieving the user profile
        val userProfileBody = getUserProfile(existingUserToken, username)

        // then the user profile is retrieved successfully
        assertNotNull(userProfileBody)
        assertEquals(username, userProfileBody.userProfile.username)
        assertEquals("PT", userProfileBody.userProfile.country)
        assertFalse(userProfileBody.userProfile.privacy)
        assertContentEquals(testProfilePicture.bytes, userProfileBody.userProfile.profilePicture)
        assertTrue(userProfileBody.userProfile.followers.isEmpty())
        assertTrue(userProfileBody.userProfile.following.isEmpty())
    }

    @Test
    fun `Try to retrieve a profile from a non-existing user and fails with code 404`() {
        // given an existing logged-in user
        val userToken = publicTestUserToken

        // when trying to retrieve a profile from a non-existing user
        val error = get<Problem>(
            client,
            api(Uris.User.USER_PROFILE.replace("{username}", "nonExistingUser")),
            HttpStatus.NOT_FOUND,
            userToken
        )

        // then the user profile is not retrieved and an error is returned with the UserNotFound message
        assertNotNull(error)
        assertEquals(UserNotFound("nonExistingUser").message, error.detail)
    }
}
