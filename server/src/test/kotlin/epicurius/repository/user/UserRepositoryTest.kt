package epicurius.repository.user

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.user.UpdateUserModel
import epicurius.repository.RepositoryTest
import epicurius.utils.createTestUser
import epicurius.utils.generateEmail
import epicurius.utils.generateRandomUsername
import epicurius.utils.generateSecurePassword
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class UserRepositoryTest : RepositoryTest() {

    @Test
    fun `Adds a profile picture to the Cloud Storage, retrieves it and then deletes it successfully`() {
        // given a profile picture
        val profilePicture = testProfilePicture
        val profilePictureName = UUID.randomUUID().toString()

        // when adding a profile picture
        updateProfilePicture(profilePictureName, profilePicture)

        // then the profile picture is added successfully
        val newProfilePicture = getProfilePicture(profilePictureName)
        assertNotNull(newProfilePicture)
        assertContentEquals(profilePicture.bytes, newProfilePicture)

        // when deleting the profile picture
        deleteProfilePicture(profilePictureName)

        // then the profile picture is deleted successfully
        assertFailsWith<NullPointerException> { getProfilePicture(profilePictureName) }
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
            UpdateUserModel(
                username = newUsername,
                email = newEmail,
                country = newCountry,
                passwordHash = newPasswordHash,
                privacy = newPrivacy,
                intolerances = newIntolerances.map { Intolerance.entries.indexOf(it) },
                diets = newDiet.map { Diet.entries.indexOf(it) }
            )
        )

        // then the user is updated successfully
        assertEquals(newUsername, updatedUser.username)
        assertEquals(newEmail, updatedUser.email)
        assertEquals(newCountry, updatedUser.country)
        assertEquals(newPasswordHash, updatedUser.passwordHash)
        assertEquals(newPrivacy, updatedUser.privacy)
        assertEquals(newIntolerances, updatedUser.intolerances)
        assertEquals(newDiet, updatedUser.diets)
    }
}
