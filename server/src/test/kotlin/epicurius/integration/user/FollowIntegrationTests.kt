package epicurius.integration.user

import epicurius.domain.exceptions.FollowRequestAlreadyBeenSent
import epicurius.domain.exceptions.InvalidSelfFollow
import epicurius.domain.exceptions.UserAlreadyBeingFollowed
import epicurius.domain.exceptions.UserNotFound
import epicurius.http.utils.Problem
import epicurius.http.utils.Uris
import epicurius.integration.utils.getBody
import epicurius.integration.utils.patch
import epicurius.integration.utils.post
import epicurius.utils.createTestUser
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class FollowIntegrationTests: UserIntegrationTest() {

    private val publicTestUser = createTestUser(tm)
    private val privateTestUser = createTestUser(tm, true)

    @Test
    fun `Should follow a public user successfully with code 204`() {
        // given two users (publicTestUser and privateTestUser)

        // when following a public user
        // then the user is followed successfully
        follow(privateTestUser.token, publicTestUser.user.name)
    }

    @Test
    fun `Should get added to a private user follow requests list when following him successfully with code 204`() {
        // given two (publicTestUser and privateTestUser)

        // when following a private user
        // then a follow request is sent
        follow(publicTestUser.token, privateTestUser.user.name)
    }

    @Test
    fun `Should fail with code 409 when following yourself`() {
        // given a user (publicTestUser)

        // when following himself
        val error = patch<Problem>(
            client,
            api(Uris.User.USER_FOLLOW.replace("{name}", publicTestUser.user.name)),
            body = "",
            responseStatus = HttpStatus.CONFLICT,
            token = publicTestUser.token
        )

        // then the user cannot follow himself and fails with code 409
        val errorBody = getBody(error)
        assertEquals(InvalidSelfFollow().message, errorBody.detail)
    }

    @Test
    fun `Should fail with code 404 when following a non-existing user`() {
        // given a non-existing user
        val nonExistingUser = "nonExistingUser"

        // when following a non-existing user
        val error = patch<Problem>(
            client,
            api(Uris.User.USER_FOLLOW.replace("{name}", nonExistingUser)),
            body = "",
            responseStatus = HttpStatus.NOT_FOUND,
            token = publicTestUser.token
        )

        // then the user cannot follow himself and fails with code 404
        val errorBody = getBody(error)
        assertEquals(UserNotFound(nonExistingUser).message, errorBody.detail)
    }

    @Test
    fun `Should fail with code 409 when following a user twice`() {
        // given a user
        val user = createTestUser(tm)

        // when following a user twice
        follow(publicTestUser.token, user.user.name)

        val error = patch<Problem>(
            client,
            api(Uris.User.USER_FOLLOW.replace("{name}", user.user.name)),
            body = "",
            responseStatus = HttpStatus.CONFLICT,
            token = publicTestUser.token
        )

        // then the user cannot follow himself and fails with code 409
        val errorBody = getBody(error)
        assertEquals(UserAlreadyBeingFollowed(user.user.name).message, errorBody.detail)
    }

    @Test
    fun `Should fail with code 409 when following a private user twice`() {
        // given a user
        val user = createTestUser(tm)

        // when following a user twice
        follow(user.token, privateTestUser.user.name)

        val error = patch<Problem>(
            client,
            api(Uris.User.USER_FOLLOW.replace("{name}", privateTestUser.user.name)),
            body = "",
            responseStatus = HttpStatus.CONFLICT,
            token = user.token
        )

        // then the user cannot follow himself and fails with code 409
        val errorBody = getBody(error)
        assertEquals(FollowRequestAlreadyBeenSent(privateTestUser.user.name).message, errorBody.detail)
    }
}