package epicurius.http.user

import epicurius.domain.exceptions.FollowRequestAlreadyBeenSent
import epicurius.domain.exceptions.FollowRequestNotFound
import epicurius.domain.exceptions.UserAlreadyBeingFollowed
import epicurius.domain.exceptions.UserNotFollowed
import epicurius.domain.exceptions.UserNotFound
import epicurius.domain.user.FollowUser
import epicurius.domain.user.FollowingUser
import epicurius.http.HttpTest
import epicurius.http.utils.Problem
import epicurius.http.utils.Uris
import epicurius.http.utils.getBody
import epicurius.http.utils.patch
import epicurius.utils.createTestUser
import epicurius.utils.generateEmail
import epicurius.utils.generateRandomUsername
import epicurius.utils.generateSecurePassword
import org.junit.jupiter.api.BeforeEach
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class FollowControllerTests: HttpTest() {

    lateinit var publicTestUserToken: String
    lateinit var publicTestUsername: String

    @BeforeEach
    fun setup() {
        publicTestUsername = generateRandomUsername()
        publicTestUserToken = signUp(publicTestUsername, generateEmail(publicTestUsername), "PT", generateSecurePassword())
    }

    @Test
    fun `Follow a public user, unfollows him and then retrieve its followers and following successfully with code 200`() {
        // given two existing users
        val userToken = publicTestUserToken
        val publicUsername2 = generateRandomUsername()
        val publicUserToken2 = signUp(
            username = publicUsername2,
            email = generateEmail(publicUsername2),
            country = "PT",
            password = generateSecurePassword()
        )

        // when following a public user
        follow(publicUserToken2, publicTestUsername)

        // then the user is followed successfully
        val publicUserFollowersBody = getFollowers(userToken)
        val privateUserFollowingBody = getFollowing(publicUserToken2)
        assertNotNull(publicUserFollowersBody)
        assertNotNull(privateUserFollowingBody)
        assertTrue(publicUserFollowersBody.users.isNotEmpty())
        assertTrue(privateUserFollowingBody.users.isNotEmpty())
        assertEquals(1, publicUserFollowersBody.users.size)
        assertEquals(1, privateUserFollowingBody.users.size)
        assertTrue(publicUserFollowersBody.users.contains(FollowUser(publicUsername2, null)))
        assertTrue(privateUserFollowingBody.users.contains(FollowingUser(publicTestUsername, null)))

        // when unfollowing the user
        unfollow(publicUserToken2, publicTestUsername)

        // then the user is unfollowed successfully
        val publicUserFollowersAfterUnfollowBody = getFollowers(userToken)
        val privateUserFollowingAfterUnfollowBody = getFollowing(publicUserToken2)
        assertNotNull(publicUserFollowersAfterUnfollowBody)
        assertNotNull(privateUserFollowingAfterUnfollowBody)
        assertTrue(publicUserFollowersAfterUnfollowBody.users.isEmpty())
        assertTrue(privateUserFollowingAfterUnfollowBody.users.isEmpty())
    }

    @Test
    fun `Try to follow a private user, get added to its follow requests and then cancel the request successfully with code 204`() {
        // given two existing users
        val userToken = publicTestUserToken
        val privateUsername = generateRandomUsername()
        val privateUserToken = signUp(
            username = privateUsername,
            email = generateEmail(privateUsername),
            country = "PT",
            password = generateSecurePassword()
        )
        updateUser(privateUserToken, privacy = true)

        // when following a private user
        follow(userToken, privateUsername)

        // then the follow request is sent successfully
        val privateUserFollowRequestsBody = getFollowRequests(privateUserToken)
        assertNotNull(privateUserFollowRequestsBody)
        assertTrue(privateUserFollowRequestsBody.users.isNotEmpty())
        assertEquals(1, privateUserFollowRequestsBody.users.size)
        assertTrue(privateUserFollowRequestsBody.users.contains(FollowUser(publicTestUsername, null)))

        // when cancelling the follow request
        cancelFollowRequest(userToken, privateUsername)

        // then the follow request is cancelled successfully
        val privateUserFollowRequestsAfterCancelBody = getFollowRequests(privateUserToken)
        assertNotNull(privateUserFollowRequestsAfterCancelBody)
        assertTrue(privateUserFollowRequestsAfterCancelBody.users.isEmpty())
    }

    @Test
    fun `Try to follow a non-existing user and fails with code 404`() {
        // given an existing user
        val userToken = publicTestUserToken

        // when trying to follow a non-existing user
        val error = patch<Problem>(
            client,
            api(Uris.User.USER_FOLLOW),
            body = mapOf("username" to "nonExistingUser"),
            responseStatus = HttpStatus.NOT_FOUND,
            token = userToken
        )
        assertNotNull(error)

        // then the user is not followed and an error is returned with the UserNotFound message
        val errorBody = getBody(error)
        assertNotNull(errorBody)
        assertEquals(UserNotFound("nonExistingUser").message, errorBody.detail)
    }

    @Test
    fun `Try to follow a user twice and fails with code 400`() {
        // given two existing users
        val userToken = publicTestUserToken
        val publicUser2 = createTestUser(tm)

        // when following a user twice
        follow(userToken, publicUser2.username)

        val error = patch<Problem>(
            client,
            api(Uris.User.USER_FOLLOW),
            body = mapOf("username" to publicUser2.username),
            responseStatus = HttpStatus.BAD_REQUEST,
            token = userToken
        )

        // then an error is returned with the UserAlreadyFollowed message
        val errorBody = getBody(error)
        assertNotNull(errorBody)
        assertEquals(UserAlreadyBeingFollowed(publicUser2.username).message, errorBody.detail)
    }

    @Test
    fun `Try to follow a private user twice and fails with code 400`() {
        // given two existing users
        val userToken = publicTestUserToken
        val privateUsername = generateRandomUsername()
        val privateUserToken = signUp(
            username = privateUsername,
            email = generateEmail(privateUsername),
            country = "PT",
            password = generateSecurePassword()
        )
        updateUser(privateUserToken, privacy = true)

        // when following a private user twice
        follow(userToken, privateUsername)

        val error = patch<Problem>(
            client,
            api(Uris.User.USER_FOLLOW),
            body = mapOf("username" to privateUsername),
            responseStatus = HttpStatus.BAD_REQUEST,
            token = userToken
        )

        // then an error is returned with the UserAlreadyFollowed message
        val errorBody = getBody(error)
        assertNotNull(errorBody)
        assertEquals(FollowRequestAlreadyBeenSent(privateUsername).message, errorBody.detail)
    }

    @Test
    fun `Try to unfollow a non-existing user and fails with code 404`() {
        // given an existing user
        val userToken = publicTestUserToken

        // when trying to unfollow a non-existing user
        val error = patch<Problem>(
            client,
            api(Uris.User.USER_UNFOLLOW),
            body = mapOf("username" to "nonExistingUser"),
            responseStatus = HttpStatus.NOT_FOUND,
            token = userToken
        )
        assertNotNull(error)

        // then the user is not unfollowed and an error is returned with the UserNotFound message
        val errorBody = getBody(error)
        assertNotNull(errorBody)
        assertEquals(UserNotFound("nonExistingUser").message, errorBody.detail)
    }

    @Test
    fun `Try to unfollow a user that is not being followed and fails with code 400`() {
        // given two existing users
        val userToken = publicTestUserToken
        val publicUser2 = createTestUser(tm)

        // when unfollowing a user that is not being followed
        val error = patch<Problem>(
            client,
            api(Uris.User.USER_UNFOLLOW),
            body = mapOf("username" to publicUser2.username),
            responseStatus = HttpStatus.BAD_REQUEST,
            token = userToken
        )

        // then an error is returned with the UserNotFound message
        val errorBody = getBody(error)
        assertNotNull(errorBody)
        assertEquals(UserNotFollowed(publicUser2.username).message, errorBody.detail)
    }

    @Test
    fun `Try to cancel a follow request to a non-existing user and fails with code 404`() {
        // given an existing user
        val userToken = publicTestUserToken

        // when trying to cancel a follow request to a non-existing user
        val error = patch<Problem>(
            client,
            api(Uris.User.USER_FOLLOW_REQUESTS),
            body = mapOf("username" to "nonExistingUser"),
            responseStatus = HttpStatus.NOT_FOUND,
            token = userToken
        )
        assertNotNull(error)

        // then the follow request is not cancelled and an error is returned with the UserNotFound message
        val errorBody = getBody(error)
        assertNotNull(errorBody)
        assertEquals(UserNotFound("nonExistingUser").message, errorBody.detail)
    }

    @Test
    fun `Try to cancel a non-existing follow request and fails with code 404`() {
        // given two existing users
        val userToken = publicTestUserToken
        val publicUser2 = createTestUser(tm)

        // when cancelling a non-existing follow request
        val error = patch<Problem>(
            client,
            api(Uris.User.USER_FOLLOW_REQUESTS),
            body = mapOf("username" to publicUser2.username),
            responseStatus = HttpStatus.NOT_FOUND,
            token = userToken
        )

        // then an error is returned with the UserNotFound message
        val errorBody = getBody(error)
        assertNotNull(errorBody)
        assertEquals(FollowRequestNotFound(publicUser2.username).message, errorBody.detail)
    }
}

