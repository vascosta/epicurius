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
            intolerances = setOf(Intolerance.GLUTEN),
            diets = setOf(Diet.VEGAN)
        )

        // mock
        val mockPasswordHash = userDomain.encodePassword(newPassword)
        val mockUser = User(
            publicTestUser.id,
            updateUserInfo.name!!,
            updateUserInfo.email!!,
            mockPasswordHash,
            publicTestUser.tokenHash,
            updateUserInfo.country!!,
            updateUserInfo.privacy!!,
            updateUserInfo.intolerances!!,
            updateUserInfo.diets!!,
            publicTestUser.profilePictureName
        )
        whenever(jdbiUserRepositoryMock.getUser(newUsername)).thenReturn(null)
        whenever(countriesDomainMock.checkIfCountryCodeIsValid(updateUserInfo.country!!)).thenReturn(true)
        whenever(userDomainMock.encodePassword(newPassword)).thenReturn(mockPasswordHash)
        whenever(jdbiUserRepositoryMock.updateUser(publicTestUsername, updateUserInfo.toJdbiUpdateUser(mockPasswordHash)))
            .thenReturn(mockUser)

        // when updating the user
        val updatedUser = updateUser(publicTestUsername, updateUserInfo)

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
        // given two users (publicTestUser, privateTestUser)

        // mock
        whenever(jdbiUserRepositoryMock.getUser(privateTestUsername)).thenReturn(publicTestUser)
        whenever(jdbiUserRepositoryMock.getUser(email = privateTestUser.email)).thenReturn(publicTestUser)
        whenever(jdbiUserRepositoryMock.getUser(privateTestUsername, privateTestUser.email)).thenReturn(publicTestUser)

        // when updating the user with an existing username
        // then the user cannot be updated and throws UserAlreadyExists exception
        assertFailsWith<UserAlreadyExists> {
            updateUser(
                publicTestUser.name,
                UpdateUserInputModel(
                    name = privateTestUser.name
                )
            )
        }

        // when updating the user with an existing email
        // then the user cannot be updated and throws UserAlreadyExists exception
        assertFailsWith<UserAlreadyExists> {
            updateUser(
                publicTestUser.name,
                UpdateUserInputModel(
                    email = privateTestUser.email
                )
            )
        }

        // when updating the user with an existing username and email
        // then the user cannot be updated and throws UserAlreadyExists exception
        assertFailsWith<UserAlreadyExists> {
            updateUser(
                publicTestUser.name,
                UpdateUserInputModel(
                    name = privateTestUser.name,
                    email = privateTestUser.email
                )
            )
        }
    }

    @Test
    fun `Should throw InvalidCountry exception when updating a user with an invalid country`() {
        // given a user (publicTestUser) and an invalid country
        val invalidCountry = "XX"

        // mock
        whenever(jdbiUserRepositoryMock.getUser(publicTestUsername)).thenReturn(null)
        whenever(countriesDomainMock.checkIfCountryCodeIsValid(invalidCountry)).thenReturn(false)

        // when updating the user with an invalid country
        // then the user cannot be updated and throws InvalidCountry exception
        assertFailsWith<InvalidCountry> {
            updateUser(
                publicTestUsername,
                UpdateUserInputModel(
                    country = invalidCountry
                )
            )
        }
    }

    @Test
    fun `Should throw PasswordsDoNotMatch exception when updating a user with different passwords`() {
        // given a user (publicTestUser)
        val password1 = generateSecurePassword()
        val password2 = generateSecurePassword()

        // mock
        whenever(jdbiUserRepositoryMock.getUser(publicTestUsername)).thenReturn(null)
        whenever(countriesDomainMock.checkIfCountryCodeIsValid(publicTestUser.country)).thenReturn(true)

        // when updating the user with different passwords
        // then the user cannot be updated and throws PasswordsDoNotMatch exception
        assertFailsWith<PasswordsDoNotMatch> {
            updateUser(
                publicTestUsername,
                UpdateUserInputModel(
                    password = password1,
                    confirmPassword = password2
                )
            )
        }

        assertFailsWith<PasswordsDoNotMatch> {
            updateUser(
                publicTestUsername,
                UpdateUserInputModel(
                    password = password1,
                    confirmPassword = null
                )
            )
        }
    }
}
