package epicurius.unit.services.user

import epicurius.domain.picture.PictureDomain
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class GetProfilePictureServiceTests : UserServiceTest() {

    @Test
    fun `Should retrieve the profile picture of an user successfully`() {
        // given a user (publicTestUser) with a profile picture

        // mock
        whenever(pictureRepositoryMock.getPicture(publicTestUser.profilePictureName!!, PictureDomain.USERS_FOLDER))
            .thenReturn(testPicture.bytes)

        // when retrieving the profile picture
        val profilePicture = getProfilePicture(publicTestUser.profilePictureName)

        // then the profile picture is retrieved successfully
        assertNotNull(profilePicture)
        assertContentEquals(testPicture.bytes, profilePicture)
    }

    @Test
    fun `Should retrieve null when the profile picture name is null`() {
        // given a user without a profile picture

        // when retrieving the profile picture
        val profilePicture = getProfilePicture(null)

        // then the profile picture is not retrieved
        assertNull(profilePicture)
    }
}
