package epicurius.unit.repository.user

import epicurius.domain.user.FollowingStatus
import epicurius.utils.createTestUser
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ChecksUserRepositoryTests : UserRepositoryTest() {

    private val publicTestUser = createTestUser(tm)
    private val privateTestUser = createTestUser(tm, true)

    @Test
    fun `Should checks if an existing user is logged in successfully`() {
        // given an existing user logged in (publicTestUser)

        // when checking if the user is logged in
        val isLoggedIn = checkIfUserIsLoggedIn(publicTestUser.id)

        // then the user is logged in
        assertTrue(isLoggedIn)
    }

    @Test
    fun `Should check if an user is being followed by other user successfully`() {
        // given 2 users (publicTestUser and privateTestUser)

        // when checking if the user is being followed by the other user
        val userBeingFollowedBy = checkIfUserIsBeingFollowedBy(privateTestUser.id, publicTestUser.id)

        // then the user is not being followed by the other user
        assertFalse(userBeingFollowedBy)
    }

    @Test
    fun `Should check if an user already sent a follow request to other user successfully`() {
        // given 2 users (publicTestUser and privateTestUser)

        // when checking if the user already sent a follow request to the other user
        follow(publicTestUser.id, privateTestUser.id, FollowingStatus.PENDING.ordinal)
        val userAlreadySentFollowRequest = checkIfUserAlreadySentFollowRequest(privateTestUser.id, publicTestUser.id)

        // then a follow request was already sent
        assertTrue(userAlreadySentFollowRequest)
    }

    @Test
    fun `Should check if an user has access to a public user profile successfully`() {
        // given 2 users (publicTestUser and privateTestUser)

        // when checking if the user has access to the other user's profile
        val userVisibility = checkUserVisibility(publicTestUser.name, privateTestUser.name)

        // then the user does not have access to the other user's profile
        assertTrue(userVisibility)
    }

    @Test
    fun `Should check if an user has access to a private user profile successfully`() {
        // given 2 users (publicTestUser and privateTestUser)

        // when checking if the user has access to the other user's profile
        val userVisibility = checkUserVisibility(privateTestUser.name, publicTestUser.name)

        // then the user does not have access to the other user's profile
        assertFalse(userVisibility)
    }
}
