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
        val updateUserInputInfo = UpdateUserInputModel(
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
            updateUserInputInfo.name!!,
            updateUserInputInfo.email!!,
            mockPasswordHash,
            publicTestUser.tokenHash,
            updateUserInputInfo.country!!,
            updateUserInputInfo.privacy!!,
            updateUserInputInfo.intolerances!!.toList(),
            updateUserInputInfo.diets!!.toList(),
            publicTestUser.profilePictureName
        )
        whenever(jdbiUserRepositoryMock.getUser(newUsername)).thenReturn(null)
        whenever(countriesDomainMock.checkIfCountryCodeIsValid(updateUserInputInfo.country!!)).thenReturn(true)
        whenever(userDomainMock.encodePassword(newPassword)).thenReturn(mockPasswordHash)
        whenever(jdbiUserRepositoryMock.updateUser(publicTestUser.id, updateUserInputInfo.toJdbiUpdateUser(mockPasswordHash)))
            .thenReturn(mockUser)

        // when updating the user
        val updatedUser = updateUser(publicTestUser.id, updateUserInputInfo)

        // then the user is updated successfully
        assertEquals(newUsername, updatedUser.name)
        assertEquals(updateUserInputInfo.email, updatedUser.email)
        assertEquals(updateUserInputInfo.country, updatedUser.country)
        assertEquals(updateUserInputInfo.privacy, updatedUser.privacy)
        assertEquals(updateUserInputInfo.intolerances?.toList(), updatedUser.intolerances)
        assertEquals(updateUserInputInfo.diets?.toList(), updatedUser.diets)
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
                publicTestUser.id,
                UpdateUserInputModel(
                    name = privateTestUser.name
                )
            )
        }

        // when updating the user with an existing email
        // then the user cannot be updated and throws UserAlreadyExists exception
        assertFailsWith<UserAlreadyExists> {
            updateUser(
                publicTestUser.id,
                UpdateUserInputModel(
                    email = privateTestUser.email
                )
            )
        }

        // when updating the user with an existing username and email
        // then the user cannot be updated and throws UserAlreadyExists exception
        assertFailsWith<UserAlreadyExists> {
            updateUser(
                publicTestUser.id,
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
                publicTestUser.id,
                UpdateUserInputModel(
                    country = invalidCountry
                )
            )
        }
    }

    @Test
    fun `Should throw PasswordsDoNotMatch exception when updating a user with different passwords`() {
        // given a user (publicTestUser) and different passwords
        val password1 = generateSecurePassword()
        val password2 = generateSecurePassword()

        // mock
        whenever(jdbiUserRepositoryMock.getUser(publicTestUsername)).thenReturn(null)
        whenever(countriesDomainMock.checkIfCountryCodeIsValid(publicTestUser.country)).thenReturn(true)

        // when updating the user with different passwords
        // then the user cannot be updated and throws PasswordsDoNotMatch exception
        assertFailsWith<PasswordsDoNotMatch> {
            updateUser(
                publicTestUser.id,
                UpdateUserInputModel(
                    password = password1,
                    confirmPassword = password2
                )
            )
        }

        assertFailsWith<PasswordsDoNotMatch> {
            updateUser(
                publicTestUser.id,
                UpdateUserInputModel(
                    password = password1,
                    confirmPassword = null
                )
            )
        }
    }
}
