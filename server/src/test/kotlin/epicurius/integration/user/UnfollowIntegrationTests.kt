package epicurius.integration.user

import epicurius.domain.exceptions.InvalidSelfUnfollow
import epicurius.domain.exceptions.UserNotFollowed
import epicurius.domain.exceptions.UserNotFound
import epicurius.domain.user.FollowingStatus
import epicurius.http.utils.Problem
import epicurius.http.utils.Uris
import epicurius.integration.utils.delete
import epicurius.integration.utils.getBody
import epicurius.utils.createTestUser
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class UnfollowIntegrationTests : UserIntegrationTest() {

    private val testUser = createTestUser(tm)
    private val testUser2 = createTestUser(tm)

    @Test
    fun `Should unfollow a user successfully with code 204`() {
        // given two users (testUser, testUser2)
        tm.run { it.userRepository.follow(testUser.user.id, testUser2.user.id, FollowingStatus.ACCEPTED.ordinal) }

        // when unfollowing a user
        // then the user is unfollowed successfully
        unfollow(testUser.token, testUser2.user.name)
    }

    @Test
    fun `Should fail with code 409 when unfollowing yourself`() {
        // given a user (testUser)

        // when unfollowing himself
        val error = delete<Problem>(
            client,
            api(Uris.User.USER_FOLLOW.replace("{name}", testUser.user.name)),
            HttpStatus.CONFLICT,
            testUser.token
        )

        // then the user cannot be unfollowed and fails with code 409
        val errorBody = getBody(error)
        assertEquals(InvalidSelfUnfollow().message, errorBody.detail)
    }

    @Test
    fun `Should fail with code 404 when unfollowing a non-existing user`() {
        // given a non-existing user
        val nonExistingUser = "nonExistingUser"

        // when following a non-existing user
        val error = delete<Problem>(
            client,
            api(Uris.User.USER_FOLLOW.replace("{name}", nonExistingUser)),
            HttpStatus.NOT_FOUND,
            testUser.token
        )

        // then the user cannot be unfollowed and fails with code 404
        val errorBody = getBody(error)
        assertEquals(UserNotFound(nonExistingUser).message, errorBody.detail)
    }

    @Test
    fun `Should fail with code 409 when unfollowing a user that is not being followed`() {
        // given two users (testUser, testUser2)

        // when trying to unfollow a user that is not being followed
        val error = delete<Problem>(
            client,
            api(Uris.User.USER_FOLLOW.replace("{name}", testUser2.user.name)),
            HttpStatus.CONFLICT,
            testUser.token
        )

        // then the user cannot be unfollowed and fails with code 409
        val errorBody = getBody(error)
        assertEquals(UserNotFollowed(testUser2.user.name).message, errorBody.detail)
    }
}
