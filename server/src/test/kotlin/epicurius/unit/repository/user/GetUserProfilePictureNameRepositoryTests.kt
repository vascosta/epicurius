package epicurius.unit.repository.user

import epicurius.utils.createTestUser
import kotlin.test.Test
import kotlin.test.assertEquals

class GetUserProfilePictureNameRepositoryTests: UserRepositoryTest() {

    @Test
    fun `Should retrieve an user profile picture name successfully`() {
        // given a user
        val user = createTestUser(tm)

        // when retrieving the user profile picture name
        val userProfilePictureName = getUserProfilePictureName(user.user.id)

        // then the user profile picture name is retrieved successfully
        assertEquals(user.user.profilePictureName, userProfilePictureName)
    }
}