package epicurius.unit.http.user

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.exceptions.InvalidCountry
import epicurius.domain.exceptions.PasswordsDoNotMatch
import epicurius.domain.exceptions.UserAlreadyExists
import epicurius.domain.user.UserInfo
import epicurius.http.user.models.input.UpdateUserInputModel
import epicurius.http.user.models.output.UpdateUserOutputModel
import epicurius.utils.generateEmail
import epicurius.utils.generateRandomUsername
import epicurius.utils.generateSecurePassword
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UpdateUserControllerTests : UserHttpTest() {

    private val newUsername = generateRandomUsername()
    val newPassword = generateSecurePassword()
    private val updateUserInputInfo = UpdateUserInputModel(
        name = newUsername,
        email = generateEmail(newUsername),
        country = "ES",
        password = newPassword,
        confirmPassword = newPassword,
        privacy = true,
        intolerances = setOf(Intolerance.GLUTEN),
        diets = setOf(Diet.VEGAN)
    )

    @Test
    fun `Should update a user successfully`() {
        // given information to update a user (updateUserInputInfo)

        // mock
        val mockUserInfo = UserInfo(
            updateUserInputInfo.name!!,
            updateUserInputInfo.email!!,
            updateUserInputInfo.country!!,
            updateUserInputInfo.privacy!!,
            updateUserInputInfo.intolerances!!.toList(),
            updateUserInputInfo.diets!!.toList(),
            publicTestUser.user.profilePictureName
        )
        whenever(userServiceMock.updateUser(publicTestUsername, updateUserInputInfo))
            .thenReturn(mockUserInfo)

        // when updating the user
        val response = updateUser(publicTestUser, updateUserInputInfo)
        val body = response.body as UpdateUserOutputModel

        // then the user is updated successfully
        assertEquals(HttpStatusCode.valueOf(200), response.statusCode)
        assertEquals(newUsername, body.userInfo.name)
        assertEquals(updateUserInputInfo.email, body.userInfo.email)
        assertEquals(updateUserInputInfo.country, body.userInfo.country)
        assertEquals(updateUserInputInfo.privacy, body.userInfo.privacy)
        assertEquals(updateUserInputInfo.intolerances?.toList(), body.userInfo.intolerances)
        assertEquals(updateUserInputInfo.diets?.toList(), body.userInfo.diets)
    }

    @Test
    fun `Should throw UserAlreadyExists exception when updating a user with an existing username or email`() {
        // given two users (publicTestUser, privateTestUser)

        // mock
        whenever(userServiceMock.updateUser(publicTestUsername, updateUserInputInfo.copy(name = privateTestUsername)))
            .thenThrow(UserAlreadyExists())
        whenever(
            userServiceMock
                .updateUser(publicTestUsername, updateUserInputInfo.copy(email = privateTestUser.user.email))
        ).thenThrow(UserAlreadyExists())
        whenever(
            userServiceMock
                .updateUser(publicTestUsername, updateUserInputInfo.copy(name = privateTestUsername, email = privateTestUser.user.email))
        ).thenThrow(UserAlreadyExists())

        // when updating the user with an existing username
        // then the user cannot be updated and throws UserAlreadyExists exception
        assertFailsWith<UserAlreadyExists> {
            updateUser(publicTestUser, updateUserInputInfo.copy(name = privateTestUsername))
        }

        // when updating the user with an existing email
        // then the user cannot be updated and throws UserAlreadyExists exception
        assertFailsWith<UserAlreadyExists> {
            updateUser(publicTestUser, updateUserInputInfo.copy(email = privateTestUser.user.email))
        }

        // when updating the user with an existing username and email
        // then the user cannot be updated and throws UserAlreadyExists exception
        assertFailsWith<UserAlreadyExists> {
            updateUser(
                publicTestUser,
                updateUserInputInfo.copy(name = privateTestUsername, email = privateTestUser.user.email)
            )
        }
    }

    @Test
    fun `Should throw InvalidCountry exception when updating a user with an invalid country`() {
        // given a user (publicTestUser) and an invalid country
        val invalidCountry = "XX"

        // mock
        whenever(userServiceMock.updateUser(publicTestUsername, updateUserInputInfo.copy(country = invalidCountry)))
            .thenThrow(InvalidCountry())

        // when updating the user with an invalid country
        // then the user cannot be updated and throws InvalidCountry exception
        assertFailsWith<InvalidCountry> { updateUser(publicTestUser, updateUserInputInfo.copy(country = invalidCountry)) }
    }

    @Test
    fun `Should throw PasswordsDoNotMatch exception when updating a user with different passwords`() {
        // given a user (publicTestUser) and different passwords
        val password1 = generateSecurePassword()
        val password2 = generateSecurePassword()

        // mock
        whenever(
            userServiceMock
                .updateUser(publicTestUsername, updateUserInputInfo.copy(password = password1, confirmPassword = password2))
        ).thenThrow(PasswordsDoNotMatch())
        whenever(
            userServiceMock
                .updateUser(publicTestUsername, updateUserInputInfo.copy(password = password1, confirmPassword = null))
        ).thenThrow(PasswordsDoNotMatch())

        // when updating the user with different passwords
        // then the user cannot be updated and throws PasswordsDoNotMatch exception
        assertFailsWith<PasswordsDoNotMatch> {
            updateUser(publicTestUser, updateUserInputInfo.copy(password = password1, confirmPassword = password2))
        }

        assertFailsWith<PasswordsDoNotMatch> {
            updateUser(publicTestUser, updateUserInputInfo.copy(password = password1, confirmPassword = null))
        }
    }
}
