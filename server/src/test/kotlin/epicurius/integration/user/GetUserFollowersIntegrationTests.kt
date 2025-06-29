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

class GetUserFollowersIntegrationTests : UserIntegrationTest() {

    @Test
    fun `Should retrieve the followers of an user successfully with code 200`() {
        // given a user
        val user = createTestUser(tm)

        // when retrieving the followers of the user
        val body = getUserFollowers(user.token, null, null, null, 10)

        // then the followers are retrieved successfully with code 200
        assertNotNull(body)
        assertTrue(body.users.isEmpty())
    }

    @Test
    fun `Should search the followers of the user successfully with code 200`() {
        // given a user
        val user = createTestUser(tm)
        val user2 = createTestUser(tm)
        tm.run { it.userRepository.follow(user.user.id, user2.user.id, FollowingStatus.ACCEPTED.ordinal) }

        // when searching the followers of the user
        val body = getUserFollowers(user2.token, null, user.user.name, null, 10)

        // then the followers are retrieved successfully with code 200
        assertNotNull(body)
        assertTrue(body.users.find { it.id == user.user.id } != null)
    }

    @Test
    fun `Should retrieve the followers of another user successfully`() {
        // given a user
        val user = createTestUser(tm)
        val user2 = createTestUser(tm)
        tm.run { it.userRepository.follow(user.user.id, user2.user.id, FollowingStatus.ACCEPTED.ordinal) }

        // when retrieving the followers of a user
        val body = getUserFollowers(user.token, user2.user.name, null, null, 10)

        // then the followers are retrieved successfully with code 200
        assertNotNull(body)
        assertTrue(body.users.find { it.id == user.user.id } != null)
    }

    @Test
    fun `Should search the followers of another user successfully`() {
        // given a user
        val user = createTestUser(tm)
        val user2 = createTestUser(tm)
        tm.run { it.userRepository.follow(user.user.id, user2.user.id, FollowingStatus.ACCEPTED.ordinal) }

        // when searching the followers of a user
        val body = getUserFollowers(user.token, user2.user.name, user.user.name, null, 10)

        // then the followers are retrieved successfully with code 200
        assertNotNull(body)
        assertTrue(body.users.find { it.id == user.user.id } != null)
    }

    @Test
    fun `Should fail with code 404 when retrieving or searching the followers from non-existing user`() {
        // given a non-existing user
        val user = createTestUser(tm)
        val nonExistingUsername = UUID.randomUUID().toString()

        // when retrieving the followers of a non-existing user
        val error = get<Problem>(
            client,
            api(Uris.User.USER_FOLLOWERS).addQueryParams(
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

        // then the followers cannot be retrieved and fails with code 404
        assertNotNull(error)
        assertEquals(UserNotFound(nonExistingUsername).message, error.detail)
    }
}
