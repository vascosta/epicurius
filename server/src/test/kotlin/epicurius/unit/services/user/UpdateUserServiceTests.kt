package epicurius.unit.services.user

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.exceptions.InvalidCountry
import epicurius.domain.exceptions.PasswordsDoNotMatch
import epicurius.domain.exceptions.UserAlreadyExists
import epicurius.domain.user.User
import epicurius.http.user.models.input.UpdateUserInputModel
import epicurius.utils.generateEmail
import epicurius.utils.generateRandomUsername
import epicurius.utils.generateSecurePassword
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UpdateUserServiceTests : UserServiceTest() {

    @Test
    fun `Should update a user successfully`() {
        // given information to update a user
        val newUsername = generateRandomUsername()
        val newPassword = generateSecurePassword()
        val updateUserInfo = UpdateUserInputModel(
            name = newUsername,
            email = generateEmail(newUsername),
            country = "ES",
            password = newPassword,
            confirmPassword = newPassword,
            privacy = true,
            intolerances = listOf(Intolerance.GLUTEN),
            diets = listOf(Diet.VEGAN)
        )

        // mock
        val mockPasswordHash = userDomain.encodePassword(newPassword)
        val mockUser = User(
            testUser.id,
            updateUserInfo.name!!,
            updateUserInfo.email!!,
            mockPasswordHash,
            testUser.tokenHash,
            updateUserInfo.country!!,
            updateUserInfo.privacy!!,
            updateUserInfo.intolerances!!,
            updateUserInfo.diets!!,
            testUser.profilePictureName
        )
        whenever(jdbiUserRepositoryMock.getUser(newUsername)).thenReturn(null)
        whenever(countriesDomainMock.checkIfCountryCodeIsValid(updateUserInfo.country!!)).thenReturn(true)
        whenever(userDomainMock.encodePassword(newPassword)).thenReturn(mockPasswordHash)
        whenever(jdbiUserRepositoryMock.updateUser(testUsername, updateUserInfo.toJdbiUpdateUser(mockPasswordHash)))
            .thenReturn(mockUser)

        // when updating the user
        val updatedUser = updateUser(testUsername, updateUserInfo)

        // then the user is updated successfully
        assertEquals(newUsername, updatedUser.name)
        assertEquals(updateUserInfo.email, updatedUser.email)
        assertEquals(updateUserInfo.country, updatedUser.country)
        assertEquals(updateUserInfo.privacy, updatedUser.privacy)
        assertEquals(updateUserInfo.intolerances, updatedUser.intolerances)
        assertEquals(updateUserInfo.diets, updatedUser.diets)
    }

    @Test
    fun `Should throw UserAlreadyExists exception when updating a user with an existing username or email`() {
        // given two existing users (testUser, testUser2)

        // mock
        whenever(jdbiUserRepositoryMock.getUser(testUsername2)).thenReturn(testUser)
        whenever(jdbiUserRepositoryMock.getUser(email = testUser2.email)).thenReturn(testUser)
        whenever(jdbiUserRepositoryMock.getUser(testUsername2, testUser2.email)).thenReturn(testUser)

        // when updating the user with an existing username
        // then the user cannot be updated and throws UserAlreadyExists exception
        assertFailsWith<UserAlreadyExists> {
            updateUser(
                testUser.name,
                UpdateUserInputModel(
                    name = testUser2.name
                )
            )
        }

        // when updating the user with an existing email
        // then the user cannot be updated and throws UserAlreadyExists exception
        assertFailsWith<UserAlreadyExists> {
            updateUser(
                testUser.name,
                UpdateUserInputModel(
                    email = testUser2.email
                )
            )
        }

        // when updating the user with an existing username and email
        // then the user cannot be updated and throws UserAlreadyExists exception
        assertFailsWith<UserAlreadyExists> {
            updateUser(
                testUser.name,
                UpdateUserInputModel(
                    name = testUser2.name,
                    email = testUser2.email
                )
            )
        }
    }

    @Test
    fun `Should throw InvalidCountry exception when updating a user with an invalid country`() {
        // given an existing user (testUser) and an invalid country
        val invalidCountry = "XX"

        // mock
        whenever(jdbiUserRepositoryMock.getUser(testUsername)).thenReturn(null)
        whenever(countriesDomainMock.checkIfCountryCodeIsValid(invalidCountry)).thenReturn(false)

        // when updating the user with an invalid country
        // then the user cannot be updated and throws InvalidCountry exception
        assertFailsWith<InvalidCountry> {
            updateUser(
                testUsername,
                UpdateUserInputModel(
                    country = invalidCountry
                )
            )
        }
    }

    @Test
    fun `Should throw PasswordsDoNotMatch exception when updating a user with different passwords`() {
        // given an existing user (testUser)
        val password1 = generateSecurePassword()
        val password2 = generateSecurePassword()

        // mock
        whenever(jdbiUserRepositoryMock.getUser(testUsername)).thenReturn(null)
        whenever(countriesDomainMock.checkIfCountryCodeIsValid(testUser.country)).thenReturn(true)

        // when updating the user with different passwords
        // then the user cannot be updated and throws PasswordsDoNotMatch exception
        assertFailsWith<PasswordsDoNotMatch> {
            updateUser(
                testUsername,
                UpdateUserInputModel(
                    password = password1,
                    confirmPassword = password2
                )
            )
        }

        assertFailsWith<PasswordsDoNotMatch> {
            updateUser(
                testUsername,
                UpdateUserInputModel(
                    password = password1,
                    confirmPassword = null
                )
            )
        }
    }
}
