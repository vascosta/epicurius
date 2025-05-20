package epicurius.integration.user

import epicurius.domain.exceptions.UserNotFound
import epicurius.http.utils.Problem
import epicurius.http.utils.Uris
import epicurius.integration.utils.get
import epicurius.utils.createTestUser
import org.springframework.http.HttpStatus
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GetUserProfileIntegrationTests: UserIntegrationTest() {

    private val testUser = createTestUser(tm)

    @Test
    fun `Should retrieve the user profile successfully with code 200`() {
        // given a user (testUser)

        // when retrieving the user profile
        val body = getUserProfile(testUser.token, testUser.user.name)

        // then the user profile is retrieved successfully
        assertNotNull(body)
        assertEquals(testUser.user.name, body.userProfile.name)
        assertEquals(testUser.user.country, body.userProfile.country)
        assertEquals(testUser.user.privacy, body.userProfile.privacy)
        assertContentEquals(null, body.userProfile.profilePicture)
        assertEquals(0, body.userProfile.followersCount)
        assertEquals(0, body.userProfile.followingCount)
    }

    @Test
    fun `Should retrieve another user profile successfully with code 200`() {
        // given two users
        val testUser2 = createTestUser(tm)

        // when retrieving the other user profile
        val body = getUserProfile(testUser.token, testUser2.user.name)

        // then the user profile is retrieved successfully
        assertNotNull(body)
        assertEquals(testUser2.user.name, body.userProfile.name)
        assertEquals(testUser2.user.country, body.userProfile.country)
        assertEquals(testUser2.user.privacy, body.userProfile.privacy)
        assertContentEquals(null, body.userProfile.profilePicture)
        assertEquals(0, body.userProfile.followersCount)
        assertEquals(0, body.userProfile.followingCount)
    }

    @Test
    fun `Should fail with code 404 when retrieving a non-existing user profile`() {
        // given a user (publicTestUser) and non-existing username
        val nonExistingUsername = UUID.randomUUID().toString()

        // when getting the user profile
        val errorBody = get<Problem>(
            client,
            api(Uris.User.USER_PROFILE.replace("{name}", nonExistingUsername)),
            HttpStatus.NOT_FOUND,
            testUser.token
        )

        // then the user profile cannot be retrieved and fails with code 404
        assertNotNull(errorBody)
        assertEquals(UserNotFound(nonExistingUsername).message, errorBody.detail)

    }
}