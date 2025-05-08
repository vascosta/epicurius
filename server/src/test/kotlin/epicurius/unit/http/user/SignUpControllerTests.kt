package epicurius.unit.http.user

import epicurius.domain.exceptions.InvalidCountry
import epicurius.domain.exceptions.PasswordsDoNotMatch
import epicurius.domain.exceptions.UserAlreadyExists
import epicurius.http.user.models.input.SignUpInputModel
import epicurius.utils.generateEmail
import epicurius.utils.generateRandomUsername
import epicurius.utils.generateSecurePassword
import jakarta.servlet.http.Cookie
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SignUpControllerTests : UserHttpTest() {

    private val username = generateRandomUsername()
    private val password = generateSecurePassword()
    private val signUpInfo = SignUpInputModel(
        username,
        generateEmail(username),
        "PT",
        password,
        password
    )

    @Test
    fun `Should create new user and retrieve it successfully`() {
        // given information to create a user (signUpInfo)

        // mock
        val mockToken = userDomain.generateTokenValue()
        whenever(
            userServiceMock
                .createUser(signUpInfo.name, signUpInfo.email, signUpInfo.country, signUpInfo.password, signUpInfo.confirmPassword)
        ).thenReturn(mockToken)


        // when creating a user
        val response = signUp(signUpInfo, mockResponse)

        // then the user is created successfully
        verify(mockResponse).addCookie(Cookie("token", mockToken))
        assertEquals(HttpStatus.CREATED, response.statusCode)
    }

    @Test
    fun `Should throw UserAlreadyExists exception when creating an user with an existing username or email`() {
        // given an existing user (publicTestUser)
        val signUpInfoExistingUsername = signUpInfo.copy(name = publicTestUsername)
        val signUpInfoExistingEmail = signUpInfo.copy(email = publicTestUser.user.email)
        val signUpInfoExistingUsernameAndEmail = signUpInfo.copy(name = publicTestUsername, email = publicTestUser.user.email)

        // mock
        whenever(
            userServiceMock
                .createUser(signUpInfoExistingUsername.name, signUpInfoExistingUsername.email, signUpInfo.country, signUpInfo.password, signUpInfo.confirmPassword)
        ).thenThrow(UserAlreadyExists())
        whenever(
            userServiceMock
                .createUser(signUpInfoExistingEmail.name, signUpInfoExistingEmail.email, signUpInfo.country, signUpInfo.password, signUpInfo.confirmPassword)
        ).thenThrow(UserAlreadyExists())
        whenever(
            userServiceMock
                .createUser(signUpInfoExistingUsernameAndEmail.name, signUpInfoExistingUsernameAndEmail.email, signUpInfo.country, signUpInfo.password, signUpInfo.confirmPassword)
        ).thenThrow(UserAlreadyExists())

        // when creating a user with an existing username
        // then the user cannot be created and throws UserAlreadyExists exception
        assertFailsWith<UserAlreadyExists> {
            signUp(signUpInfoExistingUsername, mockResponse)
        }

        // when creating a user with an existing email
        // then the user cannot be created and throws UserAlreadyExists exception
        assertFailsWith<UserAlreadyExists> {
            signUp(signUpInfoExistingEmail, mockResponse)
        }

        // when creating a user with an existing username and email
        // then the user cannot be created and throws UserAlreadyExists exception
        assertFailsWith<UserAlreadyExists> {
            signUp(signUpInfoExistingUsernameAndEmail, mockResponse)
        }
    }

    @Test
    fun `Should throw InvalidCountry exception creating an user with an invalid country`() {
        // given an invalid country
        val signUpInfoInvalidCountry = signUpInfo.copy(country = "XX")

        // mock
        whenever(
            userServiceMock
                .createUser(signUpInfo.name, signUpInfo.email, signUpInfoInvalidCountry.country, signUpInfo.password, signUpInfo.confirmPassword)
        ).thenThrow(InvalidCountry())

        // when creating a user with an invalid country
        // then the user cannot be created and throws InvalidCountry exception
        assertFailsWith<InvalidCountry> { signUp(signUpInfoInvalidCountry, mockResponse) }
    }

    @Test
    fun `Should throw PasswordsDoNotMatch exception when creating an user with different passwords`() {
        // given different passwords
        val signUpInfoDifferentPasswords = signUpInfo.copy(password = generateSecurePassword(), confirmPassword = generateSecurePassword())

        // mock
        whenever(
            userServiceMock
                .createUser(signUpInfo.name, signUpInfo.email, signUpInfo.country, signUpInfoDifferentPasswords.password, signUpInfoDifferentPasswords.confirmPassword)
        ).thenThrow(PasswordsDoNotMatch())

        // when creating a user with different passwords
        // then the user cannot be created and throws PasswordsDoNotMatch exception
        assertFailsWith<PasswordsDoNotMatch> {
            signUp(signUpInfoDifferentPasswords, mockResponse)
        }
    }
}
