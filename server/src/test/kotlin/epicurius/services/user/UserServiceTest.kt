package epicurius.services.user

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.exceptions.InvalidCountry
import epicurius.domain.exceptions.PasswordsDoNotMatch
import epicurius.domain.exceptions.UserAlreadyExists
import epicurius.domain.exceptions.UserNotFound
import epicurius.domain.user.User
import epicurius.http.user.models.input.UpdateUserInputModel
import epicurius.services.ServiceTest
import epicurius.utils.createTestUser
import epicurius.utils.generateEmail
import epicurius.utils.generateRandomUsername
import epicurius.utils.generateSecurePassword
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UserServiceTest : ServiceTest() {

    private lateinit var publicTestUser: User
    private lateinit var privateTestUser: User

    @BeforeEach
    fun setUp() {
        publicTestUser = createTestUser(tm)
        privateTestUser = createTestUser(tm, false)
    }

    @Test
    fun `Retrieves the user profile without a picture successfully`() {
        // given an existing user
        val user = publicTestUser

        // when getting the user profile
        val userProfile = getUserProfile(user.username)

        // then the user profile is retrieved successfully
        assertEquals(user.username, userProfile.username)
        assertEquals(user.country, userProfile.country)
        assertEquals(user.privacy, userProfile.privacy)
        assertNull(userProfile.profilePicture)
        assertTrue(userProfile.followers.isEmpty())
        assertTrue(userProfile.following.isEmpty())
    }

    @Test
    fun `Try to retrieve a non-existing user profile and throws UserNotFound Exception`() {
        // given a non-existing username
        val username = UUID.randomUUID().toString()

        // when getting the user profile
        // then the user profile cannot be retrieved and throws UserNotFound Exception
        assertFailsWith<UserNotFound> { getUserProfile(username) }
    }

    @Test
    fun `Add a profile picture to an user, retrieves the user profile and then delete the profile picture successfully`() {
        // given an existing user
        val user = publicTestUser

        // when adding a profile picture
        val profilePictureName = updateProfilePicture(user.username, profilePicture = testProfilePicture)
        assertNotNull(profilePictureName)

        // then the user profile is retrieved successfully with the new profile picture
        val userProfile = getUserProfile(user.username)
        assertEquals(user.username, userProfile.username)
        assertEquals(user.country, userProfile.country)
        assertFalse(userProfile.privacy)
        assertNotNull(userProfile.profilePicture)
        assertContentEquals(testProfilePicture.bytes, userProfile.profilePicture)
        assertTrue(userProfile.followers.isEmpty())
        assertTrue(userProfile.following.isEmpty())

        // when deleting the profile picture
        val deletedProfilePictureName = updateProfilePicture(user.username, profilePictureName)

        // then the profile picture is deleted successfully
        assertNull(deletedProfilePictureName)
        val userProfileAfterProfilePictureDeletion = getUserProfile(user.username)
        assertNull(userProfileAfterProfilePictureDeletion.profilePicture)
    }

    @Test
    fun `Update the profile picture of an user and then retrieves it successfully`() {
        // given an existing user with a profile picture
        val user = publicTestUser
        val profilePictureName = updateProfilePicture(user.username, profilePicture = testProfilePicture)

        // when updating the profile picture
        val newProfilePictureName = updateProfilePicture(user.username, profilePictureName, testProfilePicture2)
        assertNotNull(newProfilePictureName)

        // then the user profile is retrieved successfully with the new profile picture
        val updatedProfilePicture = getProfilePicture(newProfilePictureName)

        assertEquals(profilePictureName, newProfilePictureName)
        assertContentEquals(testProfilePicture2.bytes, updatedProfilePicture)
    }

    @Test
    fun `Update user successfully`() {
        // given an existing user
        val user = publicTestUser

        // when updating the user
        val newUsername = generateRandomUsername()
        val newEmail = generateEmail(newUsername)
        val newCountry = "ES"
        val newPassword = generateSecurePassword()
        val newPrivacy = true
        val newIntolerances = listOf(Intolerance.GLUTEN)
        val newDiet = listOf(Diet.VEGAN)

        val updatedUser = updateUser(
            user.username,
            UpdateUserInputModel(
                username = newUsername,
                email = newEmail,
                country = newCountry,
                password = newPassword,
                confirmPassword = newPassword,
                privacy = newPrivacy,
                intolerances = newIntolerances,
                diets = newDiet
            )
        )

        // then the user is updated successfully
        assertEquals(newUsername, updatedUser.username)
        assertEquals(newEmail, updatedUser.email)
        assertEquals(newCountry, updatedUser.country)
        assertEquals(newPrivacy, updatedUser.privacy)
        assertEquals(newIntolerances, updatedUser.intolerances)
        assertEquals(newDiet, updatedUser.diets)
    }

    @Test
    fun `Try to update user with existing username or email and throws UserAlreadyExists Exception`() {
        // given two existing users
        val user1 = publicTestUser
        val user2 = privateTestUser

        // when updating the user with an existing username
        // then the user cannot be updated and throws UserAlreadyExists Exception
        assertFailsWith<UserAlreadyExists> {
            updateUser(
                user1.username,
                UpdateUserInputModel(
                    username = user2.username
                )
            )
        }

        // when updating the user with an existing email
        // then the user cannot be updated and throws UserAlreadyExists Exception
        assertFailsWith<UserAlreadyExists> {
            updateUser(
                user1.username,
                UpdateUserInputModel(
                    email = user2.email
                )
            )
        }

        // when updating the user with an existing username and email
        // then the user cannot be updated and throws UserAlreadyExists Exception
        assertFailsWith<UserAlreadyExists> {
            updateUser(
                user1.username,
                UpdateUserInputModel(
                    username = user2.username,
                    email = user2.email
                )
            )
        }
    }

    @Test
    fun `Try to update user with an invalid country and throws InvalidCountry Exception`() {
        // given an existing user
        val user = publicTestUser

        // when updating the user with an invalid country
        // then the user cannot be updated and throws InvalidCountry Exception
        assertFailsWith<InvalidCountry> {
            updateUser(
                user.username,
                UpdateUserInputModel(
                    country = "XX"
                )
            )
        }
    }

    @Test
    fun `Try to update user with different passwords and throws PasswordsDoNotMatch Exception`() {
        // given an existing user
        val user = publicTestUser

        // when updating the user with different passwords
        // then the user cannot be updated and throws PasswordsDoNotMatch Exception
        assertFailsWith<PasswordsDoNotMatch> {
            updateUser(
                user.username,
                UpdateUserInputModel(
                    password = UUID.randomUUID().toString(),
                    confirmPassword = UUID.randomUUID().toString()
                )
            )
        }

        assertFailsWith<PasswordsDoNotMatch> {
            updateUser(
                user.username,
                UpdateUserInputModel(
                    password = UUID.randomUUID().toString(),
                    confirmPassword = null
                )
            )
        }
    }
}
