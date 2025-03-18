package epicurius.services

import epicurius.domain.AuthenticatedUser
import epicurius.domain.UserDomain
import epicurius.repository.transaction.TransactionManager
import org.springframework.stereotype.Component
import java.util.Locale

@Component
class UserService(
    private val tm: TransactionManager,
    private val userDomain: UserDomain
) {
    // retornar token, inicio de sessao
    fun createUser(username: String, email: String, country: String, passwordHash: String): Int {
        if (checkIfUserExists(username, email)) throw IllegalArgumentException("User already exists")
        if (!checkIfCountryIsValid(country)) throw IllegalArgumentException("Invalid country")
        //val local = Locale("", country)
        val acronym = ""//local.country

        return tm.run {
            return@run it.userRepository.createUser(username, email, acronym, passwordHash)
        }
    }

    fun login(username: String?, email: String?, password: String): String {
        if (!checkIfUserExists(username, email)) throw IllegalArgumentException("User not found")
        if (checkIfUserIsLoggedIn(username, email)) throw IllegalArgumentException("User already logged in")
        val user = tm.run { it.userRepository.getUser(username, email) }
        if (!userDomain.verifyPassword(password, user.passwordHash)) throw IllegalArgumentException("Invalid password")
        return createToken(username, email)
    }

    fun logout(username: String) {
        deleteToken(username)
    }

    fun getAuthenticatedUser(token: String): AuthenticatedUser? {
        val tokenHash = userDomain.hashToken(token)
        if (!checkIfUserExists(tokenHash = tokenHash)) return null
        return tm.run {
            val user = it.userRepository.getUserFromTokenHash(tokenHash)
            AuthenticatedUser(user, token)
        }
    }

    private fun createToken(username: String?, email: String?): String {
        if (!checkIfUserExists(username, email)) throw IllegalArgumentException("User not found")
        if (checkIfUserIsLoggedIn(username, email)) throw IllegalArgumentException("User already logged in")

        val token = userDomain.generateTokenValue()
        tm.run { it.tokenRepository.createToken(token, username, email) }
        return token
    }

    private fun deleteToken(username: String) {
        tm.run { it.tokenRepository.deleteToken(username) }
    }

    private fun checkIfUserExists(name: String? = null, email: String? = null, tokenHash: String? = null): Boolean =
        tm.run { it.userRepository.checkIfUserExists(name, email, tokenHash) }

    private fun checkIfUserIsLoggedIn(username: String?, email: String?): Boolean =
        tm.run { it.userRepository.checkIfUserIsLoggedIn(username, email) }

    private fun checkIfCountryIsValid(country: String): Boolean = country in Locale.getISOCountries()

}