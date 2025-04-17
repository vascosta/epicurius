package epicurius.unit.services.user

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.exceptions.InvalidCountry
import epicurius.domain.exceptions.PasswordsDoNotMatch
import epicurius.domain.exceptions.UserAlreadyExists
import epicurius.domain.user.User
import epicurius.http.user.models.input.UpdateUserInputModel
import epicurius.unit.services.ServiceTest
import epicurius.utils.generateEmail
import epicurius.utils.generateRandomUsername
import epicurius.utils.generateSecurePassword
import org.mockito.kotlin.whenever
import java.util.UUID.randomUUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UpdateUserServiceTests: ServiceTest() {

    private val testUsername = generateRandomUsername()
    private val testUser = User(
        1,
        testUsername,
        generateEmail(testUsername),
        usersDomain.encodePassword(randomUUID().toString()),
        usersDomain.hashToken(randomUUID().toString()),
        "PT",
        false,
        listOf(Intolerance.GLUTEN),
        listOf(Diet.GLUTEN_FREE),
        randomUUID().toString()
    )

    private val testUsername2 = generateRandomUsername()
    private val testUser2 = User(
        2,
        testUsername2,
        generateEmail(testUsername2),
        usersDomain.encodePassword(randomUUID().toString()),
        usersDomain.hashToken(randomUUID().toString()),
        "PT",
        false,
        listOf(Intolerance.GLUTEN),
        listOf(Diet.GLUTEN_FREE),
        randomUUID().toString()
    )

    @Test
    fun `Should update a user successfully`() {
        // given information to update a recipe
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
        val passwordHash = randomUUID().toString()
        val userMock = User (
            testUser.id,
            updateUserInfo.name!!,
            updateUserInfo.email!!,
            passwordHash,
            testUser.tokenHash,
            updateUserInfo.country!!,
            updateUserInfo.privacy!!,
            updateUserInfo.intolerances!!,
            updateUserInfo.diets!!,
            testUser.profilePictureName
        )
        whenever(jdbiUserRepositoryMock.getUser(newUsername)).thenReturn(null)
        whenever(countriesDomainMock.checkIfCountryCodeIsValid(updateUserInfo.country!!)).thenReturn(true)
        whenever(usersDomainMock.encodePassword(newPassword)).thenReturn(passwordHash)
        whenever(jdbiUserRepositoryMock.updateUser(testUsername, updateUserInfo.toJdbiUpdateUser(passwordHash)))
            .thenReturn(userMock)

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
    fun `Try to update user with existing username or email and throws UserAlreadyExists Exception`() {
        // given two existing users (testUser, testUser2)

        // mock
        whenever(jdbiUserRepositoryMock.getUser(testUsername2)).thenReturn(testUser)
        whenever(jdbiUserRepositoryMock.getUser(email = testUser2.email)).thenReturn(testUser)
        whenever(jdbiUserRepositoryMock.getUser(testUsername2, testUser2.email)).thenReturn(testUser)

        // when updating the user with an existing username
        // then the user cannot be updated and an exception is thrown
        assertFailsWith<UserAlreadyExists> {
            updateUser(
                testUser.name,
                UpdateUserInputModel(
                    name = testUser2.name
                )
            )
        }

        // when updating the user with an existing email
        // then the user cannot be updated and an exception is thrown
        assertFailsWith<UserAlreadyExists> {
            updateUser(
                testUser.name,
                UpdateUserInputModel(
                    email = testUser2.email
                )
            )
        }

        // when updating the user with an existing username and email
        // then the user cannot be updated and an exception is thrown
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
        // given an existing user (testUser)

        // mock
        whenever(jdbiUserRepositoryMock.getUser(testUsername)).thenReturn(null)
        whenever(countriesDomainMock.checkIfCountryCodeIsValid("XX")).thenReturn(false)

        // when updating the user with an invalid country
        // then the user cannot be updated and an exception is thrown
        assertFailsWith<InvalidCountry> {
            updateUser(
                testUsername,
                UpdateUserInputModel(
                    country = "XX"
                )
            )
        }
    }

    @Test
    fun `Should throw PasswordsDoNotMatch exception when updating a user with different passwords`() {
        // given an existing user (testUser)

        // mock
        whenever(jdbiUserRepositoryMock.getUser(testUsername)).thenReturn(null)
        whenever(countriesDomainMock.checkIfCountryCodeIsValid(testUser.country)).thenReturn(true)

        // when updating the user with different passwords
        // then the user cannot be updated and an exception is thrown
        assertFailsWith<PasswordsDoNotMatch> {
            updateUser(
                testUsername,
                UpdateUserInputModel(
                    password = randomUUID().toString(),
                    confirmPassword = randomUUID().toString()
                )
            )
        }

        assertFailsWith<PasswordsDoNotMatch> {
            updateUser(
                testUsername,
                UpdateUserInputModel(
                    password = randomUUID().toString(),
                    confirmPassword = null
                )
            )
        }
    }
}