package epicurius.integration.user

import epicurius.domain.exceptions.FollowRequestNotFound
import epicurius.domain.exceptions.InvalidSelfRejectFollowRequest
import epicurius.domain.user.FollowRequestType
import epicurius.domain.user.FollowingStatus
import epicurius.http.media.Problem
import epicurius.http.media.Uris
import epicurius.integration.utils.getBody
import epicurius.integration.utils.patch
import epicurius.utils.createTestUser
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class RejectFollowRequestIntegrationTests : UserIntegrationTest() {

    private val publicTestUser = createTestUser(tm)
    private val privateTestUser = createTestUser(tm, true)

    @Test
    fun `Should reject a follow request successfully with code 204`() {
        // given two users (publicTestUser and privateTestUser)
        tm.run { it.userRepository.follow(publicTestUser.user.id, privateTestUser.user.id, FollowingStatus.PENDING.ordinal) }

        // when rejecting the follow request
        // then the follow request is rejected successfully with code 204
        rejectFollowRequest(privateTestUser.token, publicTestUser.user.name)
    }

    @Test
    fun `Should fail with code 409 when rejecting a follow request to himself`() {
        // given a user (publicTestUser)

        // when rejecting the follow request
        val error = patch<Problem>(
            client,
            api(Uris.User.USER_FOLLOW_REQUEST.replace("{name}", privateTestUser.user.name) + "?type=${FollowRequestType.REJECT}"),
            body = "",
            responseStatus = HttpStatus.CONFLICT,
            token = privateTestUser.token
        )

        // then the follow request is not rejected and fails with code 409
        val errorBody = getBody(error)
        assertEquals(InvalidSelfRejectFollowRequest().message, errorBody.detail)
    }

    @Test
    fun `Should fail with code 404 when rejecting a follow request that does not exist`() {
        // given a user that has not sent a follow request (publicTestUser) to other user (privateTestUser)

        // when rejecting the follow request
        val error = patch<Problem>(
            client,
            api(Uris.User.USER_FOLLOW_REQUEST.replace("{name}", publicTestUser.user.name) + "?type=${FollowRequestType.ACCEPT}"),
            body = "",
            responseStatus = HttpStatus.NOT_FOUND,
            token = privateTestUser.token
        )

        // then the follow request is not rejected and fails with code 404
        val errorBody = getBody(error)
        assertEquals(FollowRequestNotFound(privateTestUser.user.name).message, errorBody.detail)
    }
}