package epicurius.integration.user

import epicurius.domain.exceptions.FollowRequestNotFound
import epicurius.domain.exceptions.InvalidSelfCancelFollowRequest
import epicurius.domain.user.FollowRequestType
import epicurius.domain.user.FollowingStatus
import epicurius.http.utils.Problem
import epicurius.http.utils.Uris
import epicurius.integration.utils.getBody
import epicurius.integration.utils.patch
import epicurius.utils.createTestUser
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class CancelFollowRequestIntegrationTests : UserIntegrationTest() {

    private val publicTestUser = createTestUser(tm)
    private val privateTestUser = createTestUser(tm, true)

    @Test
    fun `Should cancel a follow request successfully with code 204`() {
        // given two users (publicTestUser and privateTestUser)
        tm.run { it.userRepository.follow(publicTestUser.user.id, privateTestUser.user.id, FollowingStatus.PENDING.ordinal) }

        // when canceling the follow request
        // then the follow request is canceled successfully
        cancelFollowRequest(publicTestUser.token, privateTestUser.user.name)
    }

    @Test
    fun `Should fail with code 409 when canceling a follow request to himself`() {
        // given a user (publicTestUser)

        // when canceling the follow request
        val error = patch<Problem>(
            client,
            api(Uris.User.USER_FOLLOW_REQUEST.replace("{name}", publicTestUser.user.name) + "?type=${FollowRequestType.CANCEL}"),
            body = "",
            responseStatus = HttpStatus.CONFLICT,
            token = publicTestUser.token
        )

        // then the follow request is not canceled and fails with code 409
        val errorBody = getBody(error)
        assertEquals(InvalidSelfCancelFollowRequest().message, errorBody.detail)
    }

    @Test
    fun `Should fail with code 404 when canceling a follow request that does not exist`() {
        // given a user that has not sent a follow request (publicTestUser) to other user (privateTestUser)

        // when canceling the follow request
        val error = patch<Problem>(
            client,
            api(Uris.User.USER_FOLLOW_REQUEST.replace("{name}", privateTestUser.user.name) + "?type=${FollowRequestType.CANCEL}"),
            body = "",
            responseStatus = HttpStatus.NOT_FOUND,
            token = publicTestUser.token
        )

        // then the follow request is not canceled and fails with code 404
        val errorBody = getBody(error)
        assertEquals(FollowRequestNotFound(privateTestUser.user.name).message, errorBody.detail)
    }
}
