package epicurius.unit.services.user

import epicurius.domain.exceptions.UserNotFound
import org.junit.jupiter.api.Assertions.assertFalse
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UpdateUserProfileServiceTests : UserServiceTest() {

    @Test
    fun `Retrieves the user profile without a picture successfully`() {
        // given an existing user
        val user = testUser

        // when getting the user profile
        val userProfile = getUserProfile(user.name)

        // then the user profile is retrieved successfully
        assertEquals(user.name, userProfile.name)
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
        val user = testUser

        // when adding a profile picture
        val profilePictureName = updateProfilePicture(user.name, profilePicture = testPicture)
        assertNotNull(profilePictureName)

        // then the user profile is retrieved successfully with the new profile picture
        val userProfile = getUserProfile(user.name)
        assertEquals(user.name, userProfile.name)
        assertEquals(user.country, userProfile.country)
        assertFalse(userProfile.privacy)
        assertNotNull(userProfile.profilePicture)
        assertContentEquals(testPicture.bytes, userProfile.profilePicture)
        assertTrue(userProfile.followers.isEmpty())
        assertTrue(userProfile.following.isEmpty())

        // when deleting the profile picture
        val deletedProfilePictureName = updateProfilePicture(user.name, profilePictureName)

        // then the profile picture is deleted successfully
        assertNull(deletedProfilePictureName)
        val userProfileAfterProfilePictureDeletion = getUserProfile(user.name)
        assertNull(userProfileAfterProfilePictureDeletion.profilePicture)
    }

    @Test
    fun `Update the profile picture of an user and then retrieves it successfully`() {
        // given an existing user with a profile picture
        val user = testUser
        val profilePictureName = updateProfilePicture(user.name, profilePicture = testPicture)

        // when updating the profile picture
        val newProfilePictureName = updateProfilePicture(user.name, profilePictureName, testPicture2)
        assertNotNull(newProfilePictureName)

        // then the user profile is retrieved successfully with the new profile picture
        val updatedProfilePicture = getProfilePicture(newProfilePictureName)

        assertEquals(profilePictureName, newProfilePictureName)
        assertContentEquals(testPicture2.bytes, updatedProfilePicture)
    }
}
