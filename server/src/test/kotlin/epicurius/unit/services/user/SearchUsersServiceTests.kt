package epicurius.unit.services.user

import epicurius.domain.PagingParams
import epicurius.domain.PictureDomain
import epicurius.repository.jdbi.user.models.SearchUserModel
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SearchUsersServiceTests : UserServiceTest() {

    @Test
    fun `Should search for users and retrieve them successfully`() {
        // given users (testUser, testUser2) with their names containing a common string
        val commonName = "test"

        // mock
        val mockSearchUserModel = SearchUserModel(testUsername, testUser.profilePictureName)
        val mockSearchUserModel2 = SearchUserModel(testUsername2, testUser2.profilePictureName)
        whenever(jdbiUserRepositoryMock.searchUsers(commonName, PagingParams()))
            .thenReturn(listOf(mockSearchUserModel, mockSearchUserModel2))
        whenever(pictureRepositoryMock.getPicture(testUser.profilePictureName!!, PictureDomain.USERS_FOLDER))
            .thenReturn(byteArrayOf(1, 2, 3))
        whenever(pictureRepositoryMock.getPicture(testUser2.profilePictureName!!, PictureDomain.USERS_FOLDER))
            .thenReturn(byteArrayOf(1, 2, 3))

        // when retrieving the users by a common string
        val users = searchUsers(commonName, PagingParams())
        println(users)

        // then the users are retrieved successfully
        assertTrue(users.isNotEmpty())
        assertEquals(2, users.size)
        assertEquals(testUsername, users[0].name)
        assertContentEquals(byteArrayOf(1, 2, 3), users[0].profilePicture)
        assertEquals(testUsername2, users[1].name)
        assertContentEquals(byteArrayOf(1, 2, 3), users[1].profilePicture)
    }
}
