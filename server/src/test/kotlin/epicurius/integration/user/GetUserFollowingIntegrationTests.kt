package epicurius.integration.user

import epicurius.domain.exceptions.UserNotFound
import epicurius.domain.user.FollowingStatus
import epicurius.http.media.Problem
import epicurius.http.media.Uris
import epicurius.integration.utils.addQueryParams
import epicurius.integration.utils.get
import epicurius.utils.createTestUser
import org.springframework.http.HttpStatus
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GetUserFollowingIntegrationTests : UserIntegrationTest() {

    @Test
    fun `Should retrieve the following of the user successfully with code 200`() {
        // given a user
        val user = createTestUser(tm)

        // when retrieving the following of the user
        val body = getUserFollowing(user.token, null, null, null, 10)

        // then the following are retrieved successfully with code 200
        assertNotNull(body)
        assertTrue(body.users.isEmpty())
    }

    @Test
    fun `Should search the following of the user successfully with code 200`() {
        // given a user
        val user = createTestUser(tm)
        val user2 = createTestUser(tm)
        tm.run { it.userRepository.follow(user.user.id, user2.user.id, FollowingStatus.ACCEPTED.ordinal) }

        // when searching the following of the user
        val body = getUserFollowing(user.token, null, user2.user.name, null, 10)

        // then the following are retrieved successfully with code 200
        assertNotNull(body)
        assertTrue(body.users.find { it.id == user2.user.id } != null)
    }

    @Test
    fun `Should retrieve the following of another user successfully`() {
        // given a user
        val user = createTestUser(tm)
        val user2 = createTestUser(tm)
        tm.run { it.userRepository.follow(user2.user.id, user.user.id, FollowingStatus.ACCEPTED.ordinal) }

        // when retrieving the following of a user
        val body = getUserFollowing(user.token, user2.user.name, null, null, 10)

        // then the following are retrieved successfully with code 200
        assertNotNull(body)
        assertTrue(body.users.find { it.id == user.user.id } != null)
    }

    @Test
    fun `Should search the following of another user successfully`() {
        // given a user
        val user = createTestUser(tm)
        val user2 = createTestUser(tm)
        tm.run { it.userRepository.follow(user2.user.id, user.user.id, FollowingStatus.ACCEPTED.ordinal) }

        // when searching the following of a user
        val body = getUserFollowing(user.token, user2.user.name, user.user.name, null, 10)

        // then the following are retrieved successfully with code 200
        assertNotNull(body)
        assertTrue(body.users.find { it.id == user.user.id } != null)
    }

    @Test
    fun `Should fail with code 404 when retrieving or searching the following from non-existing user`() {
        // given a non-existing user
        val user = createTestUser(tm)
        val nonExistingUsername = UUID.randomUUID().toString()

        // when retrieving the following of a non-existing user
        val error = get<Problem>(
            client,
            api(Uris.User.USER_FOLLOWING).addQueryParams(
                mapOf(
                    "username" to nonExistingUsername,
                    "partialFollowingName" to null,
                    "lastFollowingId" to null,
                    "limit" to 10
                )
            ),
            responseStatus = HttpStatus.NOT_FOUND,
            token = user.token,
        )

        // then the following cannot be retrieved and fails with code 404
        assertNotNull(error)
        assertEquals(UserNotFound(nonExistingUsername).message, error.detail)
    }
}
