package epicurius.domain.user

import epicurius.domain.token.TokenEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.util.Base64

@Component
class UserDomain(
    private val passwordEncoder: PasswordEncoder,
    private val tokenEncoder: TokenEncoder
) {
    fun encodePassword(password: String): String = passwordEncoder.encode(password)

    fun verifyPassword(password: String, hashedPassword: String) = passwordEncoder.matches(password, hashedPassword)

    fun generateTokenValue(): String =
        ByteArray(TOKEN_SIZE_IN_BYTES).let { byteArray ->
            SecureRandom.getInstanceStrong().nextBytes(byteArray)
            Base64.getUrlEncoder().encodeToString(byteArray)
        }

    fun isToken(token: String): Boolean = try {
        Base64.getUrlDecoder().decode(token).size == TOKEN_SIZE_IN_BYTES
    } catch (ex: IllegalArgumentException) {
        false
    }

    fun hashToken(token: String): String = tokenEncoder.hash(token)

    companion object {
        const val MIN_USERNAME_LENGTH = 3
        const val MAX_USERNAME_LENGTH = 25
        const val USERNAME_LENGTH_MSG = "must be between $MIN_USERNAME_LENGTH and $MAX_USERNAME_LENGTH characters"
        const val VALID_EMAIL_MSG = "must be a valid email address"
        const val MIN_PASSWORD_LENGTH = 8
        const val MAX_PASSWORD_LENGTH = 30
        const val TOKEN_SIZE_IN_BYTES = 32

    }
}
