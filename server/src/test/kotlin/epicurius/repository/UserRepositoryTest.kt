package epicurius.repository

import epicurius.domain.Diet
import epicurius.domain.FollowingStatus
import epicurius.domain.Intolerance
import epicurius.domain.PagingParams
import epicurius.domain.user.SocialUser
import epicurius.domain.user.UpdateUserInfo
import epicurius.utils.createTestUser
import epicurius.utils.generateEmail
import epicurius.utils.generateRandomUsername
import epicurius.utils.generateSecurePassword
import org.junit.jupiter.api.Assertions.assertNull
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class UserRepositoryTest : RepositoryTest() {

    @Test
    fun `Create new user and retrieve it successfully`() {
        // given user required information
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val passwordHash = usersDomain.encodePassword(generateSecurePassword())

        // when creating a user
        createUser(username, email, country, passwordHash)

        // when getting the user by name
        val userByName = getUserByName(username)

        // then the user is retrieved successfully
        assertNotNull(userByName)
        assertEquals(userByName.username, username)
        assertEquals(userByName.email, email)
        assertEquals(userByName.country, country)
        assertEquals(userByName.passwordHash, passwordHash)
        assertEquals(userByName.privacy, false)
        assertEquals(userByName.intolerances, emptyList())
        assertEquals(userByName.diets, emptyList())
        assertNull(userByName.profilePictureName)

        // when getting the user by email
        val userByEmail = getUserByEmail(email)

        // then the user is retrieved successfully
        assertNotNull(userByEmail)
        assertEquals(userByEmail.username, username)
        assertEquals(userByEmail.email, email)
        assertEquals(userByEmail.country, country)
        assertEquals(userByEmail.passwordHash, passwordHash)
        assertEquals(userByEmail.privacy, false)
        assertEquals(userByEmail.intolerances, emptyList())
        assertEquals(userByEmail.diets, emptyList())
        assertNull(userByEmail.profilePictureName)
    }

    @Test
    fun `Create new users and retrieve them successfully`() {
        // given 2 created users
        val username = "partial"
        val username2 = "partialUsername"
        val email = generateEmail(username)
        val email2 = generateEmail(username2)
        val country = "PT"
        val passwordHash = usersDomain.encodePassword(generateSecurePassword())
        createUser(username, email, country, passwordHash)
        createUser(username2, email2, country, passwordHash)

        // when getting the users by a partial username
        val users = getUsers("partial", PagingParams())

        // then the users are retrieved successfully
        assertTrue(users.isNotEmpty())
        assertEquals(users.size, 2)
        assertTrue(users.contains(SocialUser(username, null)))
        assertTrue(users.contains(SocialUser(username2, null)))
    }

    @Test
    fun `Adds a profile picture to the Cloud Storage and then retrieves it successfully`() {
        // given a profile picture
        val profilePicture = testProfilePicture
        val profilePictureName = UUID.randomUUID().toString()

        // when adding a profile picture
        updateProfilePicture(profilePictureName, profilePicture)

        // then the profile picture is added successfully
        val newProfilePicture = getProfilePicture(profilePictureName)
        assertNotNull(newProfilePicture)
        assertContentEquals(profilePicture.bytes, newProfilePicture)
    }

    @Test
    fun `Updates a profile picture already in the Cloud Storage and then retrieves it successfully`() {
        // given a profile picture in the Cloud Storage
        val profilePicture = testProfilePicture
        val profilePictureName = UUID.randomUUID().toString()
        updateProfilePicture(profilePictureName, profilePicture)

        // when updating the profile picture
        val newProfilePicture = testProfilePicture2
        updateProfilePicture(profilePictureName, newProfilePicture)

        // then the profile picture is updated successfully
        val updatedProfilePicture = getProfilePicture(profilePictureName)
        assertNotNull(newProfilePicture)
        assertContentEquals(newProfilePicture.bytes, updatedProfilePicture)
    }

    @Test
    fun `Update user successfully`() {
        // given user required information
        val user = createTestUser(tm)

        // when updating the user
        val newUsername = generateRandomUsername()
        val newEmail = generateEmail(newUsername)
        val newCountry = "ES"
        val newPassword = generateSecurePassword()
        val newPasswordHash = usersDomain.encodePassword(newPassword)
        val newPrivacy = true
        val newIntolerances = listOf(Intolerance.GLUTEN)
        val newDiet = listOf(Diet.VEGAN)

        val updatedUser = updateUser(
            user.username,
            UpdateUserInfo(
                username = newUsername,
                email = newEmail,
                country = newCountry,
                passwordHash = newPasswordHash,
                privacy = newPrivacy,
                intolerances = newIntolerances.map { Intolerance.entries.indexOf(it) },
                diet = newDiet.map { Diet.entries.indexOf(it) }
            )
        )

        // then the user is updated successfully
        assertEquals(updatedUser.username, newUsername)
        assertEquals(updatedUser.email, newEmail)
        assertEquals(updatedUser.country, newCountry)
        assertEquals(updatedUser.passwordHash, newPasswordHash)
        assertEquals(updatedUser.privacy, newPrivacy)
        assertEquals(updatedUser.intolerances, newIntolerances)
        assertEquals(updatedUser.diets, newDiet)
    }

    @Test
    fun `Reset password successfully`() {
        // given user required information
        val user = createTestUser(tm)

        // when resetting the password
        val newPassword = UUID.randomUUID().toString()
        val newPasswordHash = usersDomain.encodePassword(newPassword)
        resetPassword(user.email, newPasswordHash)

        // when getting the user by name
        val userAfterResetPassword = getUserByName(user.username)

        // then the password is reset successfully
        assertNotNull(userAfterResetPassword)
        assertEquals(userAfterResetPassword.username, user.username)
        assertEquals(userAfterResetPassword.email, user.email)
        assertEquals(userAfterResetPassword.passwordHash, newPasswordHash)
        assertNotEquals(userAfterResetPassword.passwordHash, user.passwordHash)
    }

    @Test
    fun `Follow a public user, unfollows him and then retrieve its followers and following successfully`() {
        // given two existing users
        val publicUser = createTestUser(tm)
        val privateUser = createTestUser(tm, true)

        // when following a public user
        follow(privateUser.id, publicUser.id, FollowingStatus.ACCEPTED.ordinal)

        // then the user is followed successfully
        val publicUserFollowers = getFollowers(publicUser.id)
        val privateUserFollowing = getFollowing(privateUser.id)
        assertTrue(publicUserFollowers.isNotEmpty())
        assertTrue(privateUserFollowing.isNotEmpty())
        assertEquals(publicUserFollowers.size, 1)
        assertEquals(privateUserFollowing.size, 1)
        assertTrue(publicUserFollowers.contains(SocialUser(privateUser.username, privateUser.profilePictureName)))
        assertTrue(privateUserFollowing.contains(SocialUser(publicUser.username, publicUser.profilePictureName)))

        // when unfollowing the user
        unfollow(privateUser.id, publicUser.id)

        // then the user is unfollowed successfully
        val publicUserFollowersAfterUnfollow = getFollowers(publicUser.id)
        val privateUserFollowingAfterUnfollow = getFollowing(privateUser.id)
        assertTrue(publicUserFollowersAfterUnfollow.isEmpty())
        assertTrue(privateUserFollowingAfterUnfollow.isEmpty())
    }

    @Test
    fun `Try to follow a private user, get added to its follow requests and then cancel the request successfully`() {
        // given two existing users
        val publicUser = createTestUser(tm)
        val privateUser = createTestUser(tm, true)

        // when following a private user
        follow(publicUser.id, privateUser.id, FollowingStatus.PENDING.ordinal)

        // then the follow request is sent successfully
        val privateUserFollowRequests = getFollowRequests(privateUser.id)
        assertTrue(privateUserFollowRequests.isNotEmpty())
        assertEquals(privateUserFollowRequests.size, 1)
        assertTrue(privateUserFollowRequests.contains(SocialUser(publicUser.username, publicUser.profilePictureName)))

        // when cancelling the follow request
        cancelFollowRequest(privateUser.id, publicUser.id)

        // then the follow request is cancelled successfully
        val privateUserFollowRequestsAfterCancel = getFollowRequests(privateUser.id)
        assertTrue(privateUserFollowRequestsAfterCancel.isEmpty())
    }

    @Test
    fun `Checks if an existing user exists successfully`() {
        // given an existing user with a token hash
        val user = createTestUser(tm)
        val token = usersDomain.generateTokenValue()
        val tokenHash = usersDomain.hashToken(token)
        createToken(tokenHash, user.username)

        // when checking if the user exists by name
        val userExistsByName = getUserByName(user.username)

        // when checking if the user exists by email
        val userExistsByEmail = getUserByEmail(user.email)

        // when checking if the user exists by token hash
        val userExistsByTokenHash = getUserByTokenHash(tokenHash)

        // then the user exists
        assertNotNull(userExistsByName)
        assertNotNull(userExistsByEmail)
        assertNotNull(userExistsByTokenHash)
        assertEquals(userExistsByName.username, user.username)
        assertEquals(userExistsByEmail.username, user.username)
        assertEquals(userExistsByTokenHash.username, user.username)
        assertEquals(userExistsByName.email, user.email)
        assertEquals(userExistsByEmail.email, user.email)
        assertEquals(userExistsByTokenHash.email, user.email)
    }

    @Test
    fun `Checks if an non-existing user exists successfully`() {
        // given a non-existing user with non-existing token hash
        val username = ""
        val email = ""
        val tokenHash = ""

        // when checking if the user exists by name
        val userExistsByName = getUserByName(username)

        // when checking if the user exists by email
        val userExistsByEmail = getUserByEmail(email)

        // when checking if the user exists by token hash
        val userExistsByTokenHash = getUserByTokenHash(tokenHash)

        // then the user does not exist
        assertNull(userExistsByName)
        assertNull(userExistsByEmail)
        assertNull(userExistsByTokenHash)
    }

    @Test
    fun `Checks if an existing user is logged in successfully`() {
        // given an existing user logged in
        val user = createTestUser(tm)
        val token = usersDomain.generateTokenValue()
        val tokenHash = usersDomain.hashToken(token)
        createToken(tokenHash, user.username)

        // when checking if the user is logged in
        val userExistsByName = checkIfUserIsLoggedIn(user.username)

        // when checking if the user exists by email
        val userExistsByEmail = checkIfUserIsLoggedIn(email = user.email)

        // then the user is logged in
        assertTrue(userExistsByName)
        assertTrue(userExistsByEmail)
    }

    @Test
    fun `Checks if not logged in user is not logged in successfully`() {
        // given an existing user not logged in
        val user = createTestUser(tm)

        // when checking if the user is logged in
        val userExistsByName = checkIfUserIsLoggedIn(user.username)

        // when checking if the user exists by email
        val userExistsByEmail = checkIfUserIsLoggedIn(email = user.email)

        // then the user is not logged in
        assertFalse(userExistsByName)
        assertFalse(userExistsByEmail)
    }

    @Test
    fun `Check if an user is being followed by other user successfully`() {
        // given 2 existing users
        val publicUser = createTestUser(tm)
        val privateUser = createTestUser(tm, true)

        // when checking if the user is being followed by the other user
        val userBeingFollowedBy = checkIfUserIsBeingFollowedBy(privateUser.id, publicUser.id)

        // then the user is not being followed by the other user
        assertFalse(userBeingFollowedBy)
    }

    @Test
    fun `Check if an user already sent a follow request to other user successfully`() {
        // given 2 existing users
        val privateUser = createTestUser(tm, true)
        val privateUser2 = createTestUser(tm, true)
        follow(privateUser.id, privateUser2.id, FollowingStatus.PENDING.ordinal)

        // when checking if the user already sent a follow request to the other user
        val userAlreadySentFollowRequest = checkIfUserAlreadySentFollowRequest(privateUser2.id, privateUser.id)

        // then a follow request was already sent
        assertTrue(userAlreadySentFollowRequest)
    }
}
