package epicurius.unit.services.user

import epicurius.domain.PagingParams
import epicurius.domain.picture.PictureDomain
import epicurius.repository.jdbi.user.models.SearchUserModel
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SearchUsersServiceTests : UserServiceTest() {

    @Test
    fun `Should search for users and retrieve them successfully`() {
        // given two users with their names containing a common string and a user searching for them
        val userId = 1904
        val commonName = "test"

        // mock
        val mockSearchUserModel = SearchUserModel(publicTestUsername, publicTestUser.profilePictureName)
        val mockSearchUserModel2 = SearchUserModel(privateTestUsername, privateTestUser.profilePictureName)
        whenever(jdbiUserRepositoryMock.searchUsers(userId, commonName, PagingParams()))
            .thenReturn(listOf(mockSearchUserModel, mockSearchUserModel2))
        whenever(pictureRepositoryMock.getPicture(publicTestUser.profilePictureName!!, PictureDomain.USERS_FOLDER))
            .thenReturn(byteArrayOf(1, 2, 3))
        whenever(pictureRepositoryMock.getPicture(privateTestUser.profilePictureName!!, PictureDomain.USERS_FOLDER))
            .thenReturn(byteArrayOf(1, 2, 3))

        // when retrieving the users by a common string
        val users = searchUsers(userId, commonName, PagingParams())

        // then the users are retrieved successfully
        assertTrue(users.isNotEmpty())
        assertEquals(2, users.size)
        assertEquals(publicTestUsername, users[0].name)
        assertContentEquals(byteArrayOf(1, 2, 3), users[0].profilePicture)
        assertEquals(privateTestUsername, users[1].name)
        assertContentEquals(byteArrayOf(1, 2, 3), users[1].profilePicture)
    }
}
