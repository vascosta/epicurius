package epicurius.integration.user

import epicurius.utils.createTestUser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UpdateUserProfilePictureIntegrationTests: UserIntegrationTest() {

    @Test
    fun `Should add a profile picture to an user successfully with code 200`() {
        // given a user without a profile picture and a picture (testPicture)
        val user = createTestUser(tm)

        // when adding a profile picture to the user
        val body = updateUserProfilePicture(user.token, testPicture)

        // then the profile picture is added successfully with code 200
        assertTrue(body.profilePictureName.isNotEmpty())
    }

    @Test
    fun `Should update a profile picture of an user successfully with code 200`() {
        // given a user and a picture (publicTestUser, testPicture2)
        val user = createTestUser(tm)
        val userProfilePictureName = updateUserProfilePicture(user.token, testPicture).profilePictureName

        // when adding a profile picture to the user
        val body = updateUserProfilePicture(user.token, testPicture2)

        // then the profile picture is added successfully with code 200
        assertEquals(userProfilePictureName, body.profilePictureName)
    }

    @Test
    fun `Should remove the profile picture of an user successfully with code 204`() {
        // given a user and a picture (publicTestUser)
        val user = createTestUser(tm)
        updateUserProfilePicture(user.token, testPicture).profilePictureName

        // when removing the profile picture of the user
        // then the profile picture is removed successfully with code 204
        removeUserProfilePicture(user.token)
    }
}