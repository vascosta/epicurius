package epicurius.services

import epicurius.domain.AuthenticatedUser
import epicurius.domain.CountriesDomain
import epicurius.domain.UserDomain
import epicurius.domain.exceptions.UserException
import epicurius.repository.transaction.TransactionManager
import epicurius.repository.transaction.firestore.FirestoreManager
import org.springframework.stereotype.Component

@Component
class UserService(
    private val tm: TransactionManager,
    private val fs: FirestoreManager,
    private val userDomain: UserDomain,
    private val countriesDomain: CountriesDomain
) {
    fun createUser(username: String, email: String, country: String, password: String): String {
        if (checkIfUserExists(username, email)) throw UserAlreadyExits()
        if (!countriesDomain.checkIfCodeIsValid(country)) throw UserException.InvalidCountry()
        val passwordHash = userDomain.encodePassword(password)

        tm.run { it.userRepository.createUser(username, email, country, passwordHash) }
        fs.userRepository.createUserFollowersAndFollowing(username, false)

        return createToken(username, email)
    }

    fun login(username: String?, email: String?, password: String): String {
        if (!checkIfUserExists(username, email)) throw UserException.UserNotFound()
        if (checkIfUserIsLoggedIn(username, email)) throw UserException.UserAlreadyLoggedIn()

        val user = tm.run { it.userRepository.getUser(username, email) }
        if (!userDomain.verifyPassword(password, user.passwordHash)) throw UserException.InvalidPassword()
        return createToken(username, email)
    }

    fun logout(username: String) {
        deleteToken(username)
    }

    fun follow(username: String, usernameToFollow: String) {
        fs.userRepository.addFollowing(username, usernameToFollow)
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
        if (!checkIfUserExists(username, email)) throw UserException.UserNotFound()
        if (checkIfUserIsLoggedIn(username, email)) throw UserException.UserAlreadyLoggedIn()

        val token = userDomain.generateTokenValue()
        val tokenHash = userDomain.hashToken(token)
        tm.run { it.tokenRepository.createToken(tokenHash, username, email) }
        return token
    }

    private fun deleteToken(username: String) {
        tm.run { it.tokenRepository.deleteToken(username) }
    }

    private fun checkIfUserExists(name: String? = null, email: String? = null, tokenHash: String? = null): Boolean =
        tm.run { it.userRepository.checkIfUserExists(name, email, tokenHash) }

    private fun checkIfUserIsLoggedIn(username: String?, email: String?): Boolean =
        tm.run { it.userRepository.checkIfUserIsLoggedIn(username, email) }
}