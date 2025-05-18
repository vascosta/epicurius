package epicurius.unit.repository.user

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.repository.jdbi.user.models.JdbiUpdateUserModel
import epicurius.utils.createTestUser
import epicurius.utils.generateEmail
import epicurius.utils.generateRandomUsername
import epicurius.utils.generateSecurePassword
import kotlin.test.Test
import kotlin.test.assertEquals

class UpdateUserRepositoryTests : UserRepositoryTest() {

    private val testUser = createTestUser(tm)

    @Test
    fun `Should update an user successfully`() {
        // given information to update the user
        val newUsername = generateRandomUsername()
        val newEmail = generateEmail(newUsername)
        val newCountry = "ES"
        val newPassword = generateSecurePassword()
        val newPasswordHash = userDomain.encodePassword(newPassword)
        val newPrivacy = true
        val newIntolerances = setOf(Intolerance.GLUTEN)
        val newDiet = setOf(Diet.VEGAN)

        // when updating the user (testUser)
        val updatedUser = updateUser(
            testUser.user.id,
            JdbiUpdateUserModel(
                name = newUsername,
                email = newEmail,
                country = newCountry,
                passwordHash = newPasswordHash,
                privacy = newPrivacy,
                intolerances = newIntolerances.map { Intolerance.entries.indexOf(it) },
                diets = newDiet.map { Diet.entries.indexOf(it) }
            )
        )

        // then the user is updated successfully
        assertEquals(newUsername, updatedUser.name)
        assertEquals(newEmail, updatedUser.email)
        assertEquals(newCountry, updatedUser.country)
        assertEquals(newPasswordHash, updatedUser.passwordHash)
        assertEquals(newPrivacy, updatedUser.privacy)
        assertEquals(newIntolerances.toList(), updatedUser.intolerances)
        assertEquals(newDiet.toList(), updatedUser.diets)
    }
}
