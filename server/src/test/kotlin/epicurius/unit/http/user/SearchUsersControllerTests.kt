package epicurius.unit.http.user

import epicurius.domain.PagingParams
import epicurius.domain.user.AuthenticatedUser
import epicurius.domain.user.SearchUser
import epicurius.domain.user.User
import epicurius.http.controllers.user.models.output.SearchUsersOutputModel
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import java.util.UUID.randomUUID
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class SearchUsersControllerTests : UserControllerTest() {

    @Test
    fun `Should search for users and retrieve them successfully`() {
        // given two users with their names containing a common string and a user searching for them
        val authenticatedUser = AuthenticatedUser(
            User(1904, "", "", "", "", "", false, emptyList(), emptyList(), ""),
            randomUUID().toString(),
        )
        val commonName = "test"

        // mock
        val mockSearchUser = SearchUser(publicTestUsername, null)
        val mockSearchUser2 = SearchUser(privateTestUsername, null)
        val mockSearchUsers = listOf(mockSearchUser, mockSearchUser2)
        whenever(userServiceMock.searchUsers(authenticatedUser.user.id, commonName, PagingParams()))
            .thenReturn(mockSearchUsers)

        // when retrieving the users by a common string
        val response = searchUsers(authenticatedUser, commonName, PagingParams())
        val body = response.body as SearchUsersOutputModel

        // then the users are retrieved successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertContentEquals(mockSearchUsers, body.users)
    }
}
